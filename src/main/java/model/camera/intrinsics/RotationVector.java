package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV 3x1 rotation vector
 *
 * @author Alan Carter
 * @version 1.0
 */
public class RotationVector
{
    /*---------
     * FIELDS *
     ---------*/

    private final double R1;
    private final double R2;
    private final double R3;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public RotationVector(double r1, double r2, double r3)
    {
        R1 = r1;
        R2 = r2;
        R3 = r3;
    }
    
    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double R1() { return R1; }
    public double R2() { return R2; }
    public double R3() { return R3; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static RotationVector fromMat(Mat mat)
    {
        // OpenCV matrix structure: [R1; R2; R3]
        double R1 = mat.get(0,0)[0];
        double R2 = mat.get(1,0)[0];
        double R3 = mat.get(2,0)[0];

        return new RotationVector(R1,R2,R3);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        Mat m = new Mat(3,1,CV_64F);

        m.put(0, 0, R1);
        m.put(1, 0, R2);
        m.put(2, 0, R3);

        return m;
    }
}
