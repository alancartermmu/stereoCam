package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV essential matrix
 *
 * @author Alan Carter
 * @version 1.0
 */
public class EssentialMatrix
{
    /*---------
     * FIELDS *
     ---------*/

    private final double E11;
    private final double E12;
    private final double E13;
    private final double E21;
    private final double E22;
    private final double E23;
    private final double E31;
    private final double E32;
    private final double E33;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public EssentialMatrix(double e11, double e12, double e13,
                           double e21, double e22, double e23,
                           double e31, double e32, double e33
    )
    {
        E11 = e11;
        E12 = e12;
        E13 = e13;
        E21 = e21;
        E22 = e22;
        E23 = e23;
        E31 = e31;
        E32 = e32;
        E33 = e33;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double E11() { return E11; }
    public double E12() { return E12; }
    public double E13() { return E13; }
    public double E21() { return E21; }
    public double E22() { return E22; }
    public double E23() { return E23; }
    public double E31() { return E31; }
    public double E32() { return E32; }
    public double E33() { return E33; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static EssentialMatrix fromMat(Mat mat)
    {
        double E11 = mat.get(0,0)[0];
        double E12 = mat.get(0,1)[0];
        double E13 = mat.get(0,2)[0];
        double E21 = mat.get(1,0)[0];
        double E22 = mat.get(1,1)[0];
        double E23 = mat.get(1,2)[0];
        double E31 = mat.get(2,0)[0];
        double E32 = mat.get(2,1)[0];
        double E33 = mat.get(2,2)[0];

        return new EssentialMatrix(E11,E12,E13,E21,E22,E23,E31,E32,E33);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        Mat m = new Mat(3,3,CV_64F);

        m.put(0, 0, E11);
        m.put(0, 1, E12);
        m.put(0, 2, E13);
        m.put(1, 0, E21);
        m.put(1, 1, E22);
        m.put(1, 2, E23);
        m.put(2, 0, E31);
        m.put(2, 1, E32);
        m.put(2, 2, E33);

        return m;
    }
}
