package model.camera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import model.image.ImageUtilities;
import model.image.StereoImage;
import model.rectification.StereoRemapping;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;

import static model.rectification.StereoRemapping.computeMapping;

/**
 * OpenCV stereo camera class.
 * <p>Contains methods for starting and stopping the camera, serialisation,
 * stereo image capture and rectification, and image control adjustment</p>
 *
 * @author Alan Carter
 * @version 1.0
 */
public class StereoCamera implements Camera
{
    /*---------
     * FIELDS *
     ---------*/

    private final UUID id;

    // Configuration (properties which govern video capture, need to be set prior to calling start())
    private CameraConfig config;

    // Device addresses
    private int address1;
    private int address2;

    // Calibration
    private StereoIntrinsics intrinsics;
    
    // Properties
    private CameraProperties properties;

    // Internal objects (no dependency injection, not serialised)
    private transient VideoCapture vc1;
    private transient VideoCapture vc2;
    private transient Mat imgCacheLeft; // use this to avoid having to instantiate a new object every time a new frame is captured
    private transient Mat imgCacheRight;
    private transient ExecutorService exec; // Internal executor service for multithreaded operations
    private transient StereoRemapping remapping;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public StereoCamera(
            UUID id,
            int address1, int address2,
            CameraConfig config,
            StereoIntrinsics intrinsics,
            CameraProperties properties
    )
    {
        this(id, address1, address2, config, intrinsics);
        this.properties = properties;
    }

    public StereoCamera(
            UUID id,
            int address1, int address2,
            CameraConfig config,
            StereoIntrinsics intrinsics
    )
    {
        this.id = id;
        this.address1 = address1;
        this.address2 = address2;
        this.config = config;
        this.intrinsics = intrinsics;
    }

    public StereoCamera(
            UUID id,
            int address1, int address2,
            CameraConfig config,
            CameraProperties properties
    )
    {
        this(id, address1, address2, config, null, properties);
    }

    public StereoCamera(
            UUID id,
            int address1, int address2,
            CameraConfig config
    )
    {
        this(id, address1, address2, config, (StereoIntrinsics) null);
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public UUID Id() { return id; }
    public CameraConfig Config() { return config; }
    public int Address1() { return address1; }
    public int Address2() { return address2; }
    public StereoIntrinsics Intrinsics() { return intrinsics; }
    public CameraProperties Properties() { return properties; }
    
    /*----------------------
     * METHODS - Operation *
     ----------------------*/

    // Start
    public boolean start()
    {
        System.out.print("Starting camera...");

        // Initialise resources
        this.vc1 = new VideoCapture();
        this.vc2 = new VideoCapture();
        this.exec = Executors.newCachedThreadPool();
        this.imgCacheLeft = new Mat();
        this.imgCacheRight = new Mat();

        // Starting procedure
        boolean open1 = this.vc1.open(this.address1, this.config.Api(), this.config.InitialParams());
        boolean open2 = this.vc2.open(this.address2, this.config.Api(), this.config.InitialParams());

        // Set properties to previously stored parameters or default values
        properties.updateCamera(this);

        boolean success = open1 && open2;
        System.out.println(success ? "DONE" : "FAILED");

        return success;
    }

    // Is started
    public boolean isStarted()
    {
        return this.vc1 != null && this.vc2 != null && this.vc1.isOpened() && this.vc2.isOpened();
    }

    // Stop
    public void stop()
    {
        if (this.isStarted())
        {
            System.out.print("Stopping camera...");

            this.vc1.release();
            this.vc2.release();
            this.imgCacheLeft.release();
            this.imgCacheRight.release();
            try {
                this.exec.shutdown();
                if (!exec.awaitTermination(5, TimeUnit.SECONDS))
                {
                    exec.shutdownNow();
                }
            } catch (InterruptedException e)
            {
                exec.shutdownNow();
            }

            System.out.println("DONE");
        }
    }

    // Swap cameras
    public void swapCameras()
    {
        VideoCapture temp = this.vc1;
        this.vc1 = this.vc2;
        this.vc2 = temp;

        int temp2 = this.address1;
        this.address1 = this.address2;
        this.address2 = temp2;
    }
    
    // Change config
    public boolean changeConfig(CameraConfig newConfig)
    {
        if (!this.isStarted())
        {
            this.config = newConfig;
            return true;
        }
        return false;
    }

    /*--------------------------
     * METHODS - Image capture *
     --------------------------*/

    // Multithreaded image fetch and store in cache
    private boolean updateCache()
    {
        if (!this.isStarted())
        {
            return false;
        }
        try
        {
            // Grab images from both VideoCapture devices
            Future<?> operation1 = exec.submit(this.vc1::grab);
            Future<?> operation2 = exec.submit(this.vc2::grab);

            // Block until grab operations finished
            operation1.get();
            operation2.get();

            // Retrieve images
            operation1 = exec.submit(() -> this.vc1.retrieve(this.imgCacheLeft));
            operation2 = exec.submit(() -> this.vc2.retrieve(this.imgCacheRight));

            // Block until retrieve operations finished
            operation1.get();
            operation2.get();

            return true;
        }
        catch (InterruptedException | ExecutionException e)
        {
            this.imgCacheLeft.release();
            this.imgCacheRight.release();

            return false;
        }
    }
    
    public Mat capture()
    {
        updateCache();
        return ImageUtilities.hConcat(imgCacheLeft, imgCacheRight);
    }

    // Capture a stereo image
    public StereoImage captureStereoImage()
    {
        updateCache();
        return new StereoImage(this.imgCacheLeft, this.imgCacheRight);
    }
    
    public StereoImage captureRectifiedStereoImage()
    {
        if (this.isCalibrated())
        {
            if (this.remapping == null) updateRemapping();
            updateCache();

            Mat out1 = new Mat();
            Mat out2 = new Mat();
            
            Imgproc.remap(imgCacheLeft, out1, this.remapping.Map1x(), this.remapping.Map1y(), this.remapping.Interpolation());
            Imgproc.remap(imgCacheRight, out2, this.remapping.Map2x(), this.remapping.Map2y(), this.remapping.Interpolation());
            
            return new StereoImage(out1, out2);
        }
        return null;
    }

    /*---------------------
     * METHODS - Controls *
     ---------------------*/

    /* Modify a control */
    public boolean setProperty(int id, double value)
    {
        CameraProperty p = this.properties.get(id);

        if (p != null && p.MinValue() <= value && value <= p.MaxValue())
        {
            //TODO handle camera not started (calling vc.set() throws NullPointerException)

            // Set value in each camera
            vc1.set(id, value);
            vc2.set(id, value);

            // Check whether change has taken effect
            boolean success1 = vc1.get(id) == value;
            boolean success2 = vc2.get(id) == value;

            // if successful, store new value and return true
            if (success1 && success2)
            {
                p.setValue(value);
                return true;
            }
        }
        // if invalid or unsuccessful, return false and revert to old value
        return false;
    }

    /*---------------------------------------
     * METHODS - Serialisation and file I/O *
     ---------------------------------------*/

    // Serialize
    private String toJson()
    {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        return gson.toJson(this);
    }

    // Deserialize
    private static StereoCamera fromJson(String json)
    {
        try
        {
            return new Gson().fromJson(json, StereoCamera.class);
        }
        catch (JsonParseException e)
        {
            return null;
        }
    }

    /* Save */
    public boolean save(File file)
    {
        try
        {
            // try to create new file - will ignore if already exists
            file.createNewFile();

            // try to create BufferedWriter which will overwrite rather than append to existing file
            boolean overwrite = true;
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getCanonicalPath(), !overwrite));

            // write json to file
            writer.flush();
            writer.write(this.toJson());

            // close file
            writer.close();

            return true;
        } catch (IOException | SecurityException e)
        {
            return false;
        }
    }

    /* Load */
    public static StereoCamera load(File file)
    {
        try
        {
            Scanner reader = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (reader.hasNextLine())
            {
                builder.append(reader.nextLine());
            }
            reader.close();
            String fileContent = builder.toString();

            return StereoCamera.fromJson(fileContent);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    /*--------------------
     * METHODS - General *
     --------------------*/
    
    public boolean isCalibrated()
    {
        return (this.intrinsics != null);
    }

    // Update calibration and compute rectification transforms
    public void updateIntrinsics(StereoIntrinsics intrinsics)
    {
        if (intrinsics != null && intrinsics.ImageSize().equals(this.config.ImageSize()))
        {
            this.intrinsics = intrinsics;
            updateRemapping();
        }
    }
    
    // Recompute rectification transforms
    private void updateRemapping()
    {
        if (intrinsics != null)
        {
            this.remapping = computeMapping(this.intrinsics, Imgproc.INTER_LINEAR, true);
        }
    }
}
