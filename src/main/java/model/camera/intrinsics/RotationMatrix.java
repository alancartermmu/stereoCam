package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV 3x3 rotation matrix
 *
 * @author Alan Carter
 * @version 1.0
 */
public class RotationMatrix
{
    /*---------
     * FIELDS *
     ---------*/

    private final double R11;
    private final double R12;
    private final double R13;
    private final double R21;
    private final double R22;
    private final double R23;
    private final double R31;
    private final double R32;
    private final double R33;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public RotationMatrix(double r11, double r12, double r13,
                          double r21, double r22, double r23,
                          double r31, double r32, double r33
    )
    {
        R11 = r11;
        R12 = r12;
        R13 = r13;
        R21 = r21;
        R22 = r22;
        R23 = r23;
        R31 = r31;
        R32 = r32;
        R33 = r33;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double R11() { return R11; }
    public double R12() { return R12; }
    public double R13() { return R13; }
    public double R21() { return R21; }
    public double R22() { return R22; }
    public double R23() { return R23; }
    public double R31() { return R31; }
    public double R32() { return R32; }
    public double R33() { return R33; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static RotationMatrix fromMat(Mat mat)
    {
        double R11 = mat.get(0,0)[0];
        double R12 = mat.get(0,1)[0];
        double R13 = mat.get(0,2)[0];
        double R21 = mat.get(1,0)[0];
        double R22 = mat.get(1,1)[0];
        double R23 = mat.get(1,2)[0];
        double R31 = mat.get(2,0)[0];
        double R32 = mat.get(2,1)[0];
        double R33 = mat.get(2,2)[0];

        return new RotationMatrix(R11,R12,R13,R21,R22,R23,R31,R32,R33);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        Mat m = new Mat(3,3,CV_64F);

        m.put(0, 0, R11);
        m.put(0, 1, R12);
        m.put(0, 2, R13);
        m.put(1, 0, R21);
        m.put(1, 1, R22);
        m.put(1, 2, R23);
        m.put(2, 0, R31);
        m.put(2, 1, R32);
        m.put(2, 2, R33);

        return m;
    }
}
