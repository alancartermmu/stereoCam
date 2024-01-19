package model.camera;

import org.opencv.core.Mat;

public interface Camera
{
    public boolean start();
    public void stop();
    public boolean isStarted();
    public Mat capture();
}
