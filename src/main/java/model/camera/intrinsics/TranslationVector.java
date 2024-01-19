package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV 3x1 translation vector
 *
 * @author Alan Carter
 * @version 1.0
 */
public class TranslationVector
{
    /*---------
     * FIELDS *
     ---------*/

    private final double TX;
    private final double TY;
    private final double TZ;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public TranslationVector(double tx, double ty, double tz)
    {
        TX = tx;
        TY = ty;
        TZ = tz;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double TX() { return TX; }
    public double TY() { return TY; }
    public double TZ() { return TZ; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static TranslationVector fromMat(Mat mat)
    {
        // OpenCV matrix structure: [TX; TY; TZ]
        double TX = mat.get(0,0)[0];
        double TY = mat.get(1,0)[0];
        double TZ = mat.get(2,0)[0];

        return new TranslationVector(TX,TY,TZ);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        Mat m = new Mat(3,1,CV_64F);

        m.put(0, 0, TX);
        m.put(1, 0, TY);
        m.put(2, 0, TZ);

        return m;
    }
}
