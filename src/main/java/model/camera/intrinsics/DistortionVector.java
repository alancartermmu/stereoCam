package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

/**
 * Wrapper class for OpenCV camera distortion coefficients
 *
 * @author Alan Carter
 * @version 1.0
 */
public class DistortionVector
{
    /*---------
     * FIELDS *
     ---------*/

    private final Double k1;
    private final Double k2;
    private final Double k3;
    private final Double k4;
    private final Double k5;
    private final Double k6;
    private final Double p1;
    private final Double p2;
    private final Double s1;
    private final Double s2;
    private final Double s3;
    private final Double s4;
    private final Double tau_x;
    private final Double tau_y;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public DistortionVector(Double k1, Double k2, Double k3, Double k4, Double k5, Double k6,
                            Double p1, Double p2,
                            Double s1, Double s2, Double s3, Double s4,
                            Double tau_x, Double tau_y
    )
    {
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
        this.k5 = k5;
        this.k6 = k6;
        this.p1 = p1;
        this.p2 = p2;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.tau_x = tau_x;
        this.tau_y = tau_y;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public Double K1() { return k1; }
    public Double K2() { return k2; }
    public Double K3() { return k3; }
    public Double K4() { return k4; }
    public Double K5() { return k5; }
    public Double K6() { return k6; }
    public Double P1() { return p1; }
    public Double P2() { return p2; }
    public Double S1() { return s1; }
    public Double S2() { return s2; }
    public Double S3() { return s3; }
    public Double S4() { return s4; }
    public Double TauX() { return tau_x; }
    public Double TauY() { return tau_y; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

    public static DistortionVector fromMat(Mat mat)
    {
        // OpenCV matrix structure: [k1 k2 p1 p2 (k3 (k4 k5 k6 (s1 s2 s3 s4 (τx τy))))]
        Double k1 = mat.get(0,0)[0];
        Double k2 = mat.get(0,1)[0];
        Double p1 = mat.get(0,2)[0];
        Double p2 = mat.get(0,3)[0];
        Double k3 = mat.cols() > 4 ? mat.get(0,4)[0] : null;
        Double k4 = mat.cols() > 5 ? mat.get(0,5)[0] : null;
        Double k5 = mat.cols() > 5 ? mat.get(0,6)[0] : null;
        Double k6 = mat.cols() > 5 ? mat.get(0,7)[0] : null;
        Double s1 = mat.cols() > 8 ? mat.get(0,8)[0] : null;
        Double s2 = mat.cols() > 8 ? mat.get(0,9)[0] : null;
        Double s3 = mat.cols() > 8 ? mat.get(0,10)[0] : null;
        Double s4 = mat.cols() > 8 ? mat.get(0,11)[0] : null;
        Double tau_x = mat.cols() > 12 ? mat.get(0,12)[0] : null;
        Double tau_y = mat.cols() > 12 ? mat.get(0,13)[0] : null;

        return new DistortionVector(k1, k2, k3, k4, k5, k6, p1, p2, s1, s2, s3, s4, tau_x,tau_y);
    }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

    public Mat toMat()
    {
        // OpenCV matrix structure: [k1 k2 p1 p2 (k3 (k4 k5 k6 (s1 s2 s3 s4 (τx τy))))]
        Mat m = new Mat(1,14,CV_64F);

        m.put(0, 0, k1 == null ? 0 : k1);
        m.put(0, 1, k2 == null ? 0 : k2);
        m.put(0, 2, p1 == null ? 0 : p1);
        m.put(0, 3, p2 == null ? 0 : p2);

        if (k3 == null) { return m.colRange(0,4); }
        else
        {
            m.put(0, 4, k3);
        };

        if (k4 == null) { return m.colRange(0,5); }
        else
        {
            m.put(0, 5, k4);
            m.put(0, 6, k5);
            m.put(0, 7, k6);
        };

        if (s1 == null) { return m.colRange(0,8); }
        else
        {
            m.put(0, 8, s1);
            m.put(0, 9, s2);
            m.put(0, 10, s3);
            m.put(0, 11, s4);
        }

        if (tau_x == null) { return m.colRange(0,12); }
        else
        {
            m.put(0, 12, tau_x);
            m.put(0, 13, tau_y);

            return m;
        }
    }
}
