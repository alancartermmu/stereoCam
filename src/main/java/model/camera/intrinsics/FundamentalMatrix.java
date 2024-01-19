package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV fundamental matrix
 *
 * @author Alan Carter
 * @version 1.0
 */
public class FundamentalMatrix
{
    /*---------
     * FIELDS *
     ---------*/

    private final double F11;
    private final double F12;
    private final double F13;
    private final double F21;
    private final double F22;
    private final double F23;
    private final double F31;
    private final double F32;
    private final double F33;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public FundamentalMatrix(double f11, double f12, double f13,
                             double f21, double f22, double f23,
                             double f31, double f32, double f33
    )
    {
        F11 = f11;
        F12 = f12;
        F13 = f13;
        F21 = f21;
        F22 = f22;
        F23 = f23;
        F31 = f31;
        F32 = f32;
        F33 = f33;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double F11() { return F11; }
    public double F12() { return F12; }
    public double F13() { return F13; }
    public double F21() { return F21; }
    public double F22() { return F22; }
    public double F23() { return F23; }
    public double F31() { return F31; }
    public double F32() { return F32; }
    public double F33() { return F33; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static FundamentalMatrix fromMat(Mat mat)
    {
        double F11 = mat.get(0,0)[0];
        double F12 = mat.get(0,1)[0];
        double F13 = mat.get(0,2)[0];
        double F21 = mat.get(1,0)[0];
        double F22 = mat.get(1,1)[0];
        double F23 = mat.get(1,2)[0];
        double F31 = mat.get(2,0)[0];
        double F32 = mat.get(2,1)[0];
        double F33 = mat.get(2,2)[0];

        return new FundamentalMatrix(F11,F12,F13,F21,F22,F23,F31,F32,F33);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        Mat m = new Mat(3,3,CV_64F);

        m.put(0, 0, F11);
        m.put(0, 1, F12);
        m.put(0, 2, F13);
        m.put(1, 0, F21);
        m.put(1, 1, F22);
        m.put(1, 2, F23);
        m.put(2, 0, F31);
        m.put(2, 1, F32);
        m.put(2, 2, F33);

        return m;
    }
}
