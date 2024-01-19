package model.camera;

import model.camera.intrinsics.DistortionVector;
import model.camera.intrinsics.IntrinsicMatrix;
import org.opencv.core.Size;

// TODO quality information? number of views? view errors?
// TODO timestamp?  something to identify when calibration was made...
public class CameraIntrinsics
{
    /*---------
     * FIELDS *
     ---------*/

    private final Size imageSize;
    private final IntrinsicMatrix intrinsicMatrixK;
    private final DistortionVector distortionCoefficients;
    private final double rmsReprojectionError;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public CameraIntrinsics(IntrinsicMatrix intrinsicMatrixK, DistortionVector distCoeffs, Size imageSize, double rmsReprojectionError)
    {
        this.imageSize = imageSize;
        this.intrinsicMatrixK = intrinsicMatrixK;
        this.distortionCoefficients = distCoeffs;
        this.rmsReprojectionError = rmsReprojectionError;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public Size ImageSize() { return imageSize; }
    public IntrinsicMatrix IntrinsicMatrixK() { return intrinsicMatrixK; }
    public DistortionVector DistortionCoefficients() { return distortionCoefficients; }
    public double RmsReprojectionError() { return rmsReprojectionError; }

    /*----------
     * METHODS *
     ----------*/

    @Override
    public String toString()
    {
        // Focal lengths
        double fx = intrinsicMatrixK.FX();
        double fy = intrinsicMatrixK.FY();

        // Principal point
        double cx = intrinsicMatrixK.CX();
        double cy = intrinsicMatrixK.CY();

        // Skew
        double skew = intrinsicMatrixK.Skew();

        // Radial distortion
        double k1 = distortionCoefficients.K1();
        double k2 = distortionCoefficients.K2();
        double k3 = distortionCoefficients.K3();

        // Tangential distortion
        double p1 = distortionCoefficients.P1();
        double p2 = distortionCoefficients.P2();

        // Image Resolution
        double width = imageSize.width;
        double height = imageSize.height;

        // Return string
        return String.format(
                "CAMERA INTRINSICS: \r\n" +
                "\t Image Resolution [w, h] = [%.0f, %.0f]px \r\n" +
                "\t Focal length [fx, fy] = [%.1f, %.1f]px \r\n" +
                "\t Principal point [cx, cy] = [%.1f, %.1f]px \r\n" +
                "\t Skew = %.3f \r\n" +
                "\t Radial distortion [k1, k2, k3] = [%.4f, %.4f, %.4f] \r\n" +
                "\t Tangential distortion [p1, p2] = [%.4f, %.4f] \r\n",
                width, height,
                fx, fy,
                cx, cy,
                skew,
                k1, k2, k3,
                p1, p2
        );
    }
}
