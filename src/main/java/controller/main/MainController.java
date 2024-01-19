package controller.main;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import model.calibration.StereoCalibration;
import model.camera.StereoCamera;
import model.correspondence.Checkerboard;
import model.correspondence.StereoProjection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController
{
    // Hard-coded values
    private final String filePath = "C:/temp/camera/stereoCam.txt";
    public double errorThreshold = 1; //px
    public double viewsThreshold = 10;
    public double pointsThreshold = 500;

    // Global objects
    private StereoCamera camera = StereoCamera.load(new File(filePath));
    private Checkerboard checkerboard = new Checkerboard(9,14,25);
    public List<StereoProjection> calibrationProjections = new ArrayList<>();
    public StereoCalibration calibration;

    // Status properties
    public BooleanProperty status_CameraStarted = new SimpleBooleanProperty(camera != null && camera.isStarted());
    public BooleanProperty status_CheckerboardDetected = new SimpleBooleanProperty(false);
    public BooleanProperty status_CalibrationStarted = new SimpleBooleanProperty(false);
    public BooleanProperty status_CameraIsCalibrated = new SimpleBooleanProperty(camera != null && camera.isCalibrated());
    
    // Singleton field and constructor
    private static MainController instance;
    private MainController(){};

    // Singleton access method
    public static MainController getInstance()
    {
        if (instance == null)
        {
            instance = new MainController();
        }
        return instance;
    }

    /* GETTERS & SETTERS */

    public StereoCamera getCamera()
    {
        return camera;
    }

    public void setCamera(StereoCamera camera)
    {
        this.camera = camera;
    }

    public Checkerboard getCheckerboard()
    {
        return checkerboard;
    }

    public void setCheckerboard(Checkerboard checkerboard)
    {
        this.checkerboard = checkerboard;
    }

    /* METHODS */
    public void recalibrate()
    {
        calibration = StereoCalibration.calibrateStereo(calibrationProjections);
    }

    public void exit()
    {
        System.out.println("Exiting...");
        camera.stop();
        boolean saved = camera.save(new File(filePath));
        System.out.println("Camera configuration " + (saved ? "saved to " + filePath : "not saved"));
        Platform.exit();
        System.exit(0);
    }
    
    public boolean calibrationViewsCheck()
    {
        return calibration.NumberOfViews() >= viewsThreshold;
    }
    
    public boolean calibrationPointsCheck()
    {
        return calibration.NumberOfPoints() >= pointsThreshold;
    }
    
    public boolean calibrationErrorCheck()
    {
        return calibration.RmsReprojectionError() < errorThreshold;
    }
    
    public boolean calibrationOverallCheck()
    {
        return calibrationViewsCheck() && calibrationPointsCheck() && calibrationErrorCheck();
    }

}
