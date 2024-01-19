package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV camera intrinsic matrix
 *
 * @author Alan Carter
 * @version 1.0
 */
public class IntrinsicMatrix
{
    /*---------
     * FIELDS *
     ---------*/

    private final double fx;
    private final double fy;
    private final double cx;
    private final double cy;
    private final double skew;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public IntrinsicMatrix(double fx, double fy, double cx, double cy, double skew)
    {
        this.fx = fx;
        this.fy = fy;
        this.cx = cx;
        this.cy = cy;
        this.skew = skew;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public double FX() { return fx; }
    public double FY() { return fy; }
    public double CX() { return cx; }
    public double CY() { return cy; }
    public double Skew() { return skew; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static IntrinsicMatrix fromMat(Mat mat)
    {
        // OpenCV matrix structure: [fx skew cx; 0 fy cy; 0 0 1]
        double fx = mat.get(0,0)[0];
        double skew = mat.get(0,1)[0];
        double cx = mat.get(0,2)[0];
        double fy = mat.get(1,1)[0];
        double cy = mat.get(1,2)[0];

        return new IntrinsicMatrix(fx, fy, cx, cy, skew);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        // OpenCV matrix structure: [fx skew cx; 0 fy cy; 0 0 1]
        Mat m = new Mat(3,3,CV_64F);

        m.put(0, 0, fx);
        m.put(0, 1, skew);
        m.put(0, 2, cx);
        m.put(1, 0, 0);
        m.put(1, 1, fy);
        m.put(1, 2, cy);
        m.put(2, 0, 0);
        m.put(2, 1, 0);
        m.put(2, 2, 1);

        return m;
    }
}
