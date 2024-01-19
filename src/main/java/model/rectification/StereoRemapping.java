package model.rectification;

import model.camera.StereoIntrinsics;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

// Wrapper class for the mapping which rectifies a stereo camera image
public class StereoRemapping
{
    private Mat map1x;
    private Mat map1y;
    private Mat map2x;
    private Mat map2y;
    private int interpolation;
    
    private StereoRemapping(){}  // prevent instantiation
    
    public Mat Map1x() { return map1x; }
    public Mat Map1y() { return map1y; }
    public Mat Map2x() { return map2x; }
    public Mat Map2y() { return map2y; }
    public int Interpolation() { return interpolation; }

    public static StereoRemapping computeMapping(StereoIntrinsics i, int interpolation, boolean crop)
    {
        StereoRemapping remap = new StereoRemapping();
        
        Mat map1x = new Mat();
        Mat map1y = new Mat();
        Mat map2x = new Mat();
        Mat map2y = new Mat();
        
        StereoRectification r = StereoRectification.rectify(i, crop);
        
        // Compute map for camera 1
        Calib3d.initUndistortRectifyMap(
                i.IntrinsicMatrixK1().toMat(),
                i.DistortionCoefficients1().toMat(),
                r.R1(),
                r.P1(),
                i.ImageSize(),
                CvType.CV_32FC1,
                map1x,
                map1y
        );
        
        // Compute map for camera 2
        Calib3d.initUndistortRectifyMap(
                i.IntrinsicMatrixK2().toMat(),
                i.DistortionCoefficients2().toMat(),
                r.R2(),
                r.P2(),
                i.ImageSize(),
                CvType.CV_32FC1,
                map2x,
                map2y
        );
        
        remap.map1x = map1x;
        remap.map1y = map1y;
        remap.map2x = map2x;
        remap.map2y = map2y;
        remap.interpolation = interpolation;
        
        return remap;
    }
    
    public static StereoRemapping computeMapping(StereoIntrinsics i)
    {
        return computeMapping(i, Imgproc.INTER_NEAREST, true);
    }
}
