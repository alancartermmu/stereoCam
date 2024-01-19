package model.camera;

import org.opencv.core.MatOfInt;
import org.opencv.core.Size;

import java.util.Objects;

import static org.opencv.videoio.Videoio.*;

public class CameraConfig
{
    /*---------
     * FIELDS *
     ---------*/
    
    private final int api; // CAP_PROP_BACKEND
    private final int codec; // CAP_PROP_FOURCC
    private final int width; // CAP_PROP_FRAME_WIDTH
    private final int height; // CAP_PROP_FRAME_HEIGHT
    private final int fps; // CAP_PROP_FPS

    /*---------------
     * CONSTRUCTORS *
     ---------------*/
    
    public CameraConfig(
            int api,
            int codec,
            int width,
            int height,
            int fps
    )
    {
        this.api = api;
        this.codec = codec;
        this.width = width;
        this.height = height;
        this.fps = fps;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public int Api() { return api; }
    public int Codec() { return codec; }
    public int Width() { return width; }
    public int Height() { return height; }
    public int Fps() { return fps; }
    
    /*----------
     * METHODS *
     ----------*/

    public Size ImageSize()
    {
        return new Size(this.width, this.height);
    }

    public MatOfInt InitialParams()
    {
        // N.B. Order of set() calls matters in OpenCV
        return new MatOfInt(
                CAP_PROP_FRAME_WIDTH, width,
                CAP_PROP_FRAME_HEIGHT, height,
                CAP_PROP_FPS, fps,
                CAP_PROP_FOURCC, codec
        );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraConfig that = (CameraConfig) o;
        return api == that.api && codec == that.codec && width == that.width && height == that.height && fps == that.fps;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(api, codec, width, height, fps);
    }
}
