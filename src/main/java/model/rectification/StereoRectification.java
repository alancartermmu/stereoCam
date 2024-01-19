package model.rectification;

import model.camera.StereoIntrinsics;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;

// Wrapper class for computing stereo rectification on calibrated camera using Bouguet's algorithm
public class StereoRectification
{
    private Mat R1;
    private Mat R2;
    private Mat P1;
    private Mat P2;
    private Mat Q;
    private boolean crop;
    
    private StereoRectification(){}  // prevent instantiation
    
    public Mat R1() { return R1; }
    public Mat R2() { return R2; }
    public Mat P1() { return P1; }
    public Mat P2() { return P2; }
    public Mat Q() { return Q; }
    public boolean CropResult() { return crop; }
    
    public static StereoRectification rectify(StereoIntrinsics intrinsics, boolean cropResult)
    {
        StereoRectification rect = new StereoRectification();
        
        // Inputs
        Mat K1 = intrinsics.IntrinsicMatrixK1().toMat();
        Mat K2 = intrinsics.IntrinsicMatrixK2().toMat();
        Mat D1 = intrinsics.DistortionCoefficients1().toMat();
        Mat D2 = intrinsics.DistortionCoefficients2().toMat();
        Size imageSize = intrinsics.ImageSize();
        Mat R = intrinsics.RotationMatrixR().toMat();
        Mat T = intrinsics.TranslationVectorT().toMat();
        int alpha = cropResult ? 0 : -1;  // free scaling parameter
        
        // Outputs
        Mat R1 = new Mat();
        Mat R2 = new Mat();
        Mat P1 = new Mat();
        Mat P2 = new Mat();
        Mat Q = new Mat();
        
        Calib3d.stereoRectify(
                K1, D1,
                K2, D2,
                imageSize, R, T,
                R1,R2,P1,P2,Q,0,alpha
        );
        
        rect.R1 = R1;
        rect.R2 = R2;
        rect.P1 = P1;
        rect.P2 = P2;
        rect.Q = Q;
        
        return rect;
    }
    
    public static StereoRectification rectify(StereoIntrinsics intrinsics)
    {
        return rectify(intrinsics, false);
    }
}
