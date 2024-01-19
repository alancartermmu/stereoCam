package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

public class StdDevIntrinsicsVector
{
 /*---------
  * FIELDS *
  ---------*/

 private final double stDev_fx;
 private final double stDev_fy;
 private final double stDev_cx;
 private final double stDev_cy;
 private final double stDev_k1;
 private final double stDev_k2;
 private final double stDev_k3;
 private final double stDev_k4;
 private final double stDev_k5;
 private final double stDev_k6;
 private final double stDev_p1;
 private final double stDev_p2;
 private final double stDev_s1;
 private final double stDev_s2;
 private final double stDev_s3;
 private final double stDev_s4;
 private final double stDev_tauX;
 private final double stDev_tauY;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

 public StdDevIntrinsicsVector(double stDev_fx, double stDev_fy, double stDev_cx, double stDev_cy,
                                double stDev_k1, double stDev_k2, double stDev_k3, double stDev_k4,
                                double stDev_k5, double stDev_k6, double stDev_p1, double stDev_p2,
                                double stDev_s1, double stDev_s2, double stDev_s3, double stDev_s4,
                                double stDev_tauX, double stDev_tauY)
 {
  this.stDev_fx = stDev_fx;
  this.stDev_fy = stDev_fy;
  this.stDev_cx = stDev_cx;
  this.stDev_cy = stDev_cy;
  this.stDev_k1 = stDev_k1;
  this.stDev_k2 = stDev_k2;
  this.stDev_k3 = stDev_k3;
  this.stDev_k4 = stDev_k4;
  this.stDev_k5 = stDev_k5;
  this.stDev_k6 = stDev_k6;
  this.stDev_p1 = stDev_p1;
  this.stDev_p2 = stDev_p2;
  this.stDev_s1 = stDev_s1;
  this.stDev_s2 = stDev_s2;
  this.stDev_s3 = stDev_s3;
  this.stDev_s4 = stDev_s4;
  this.stDev_tauX = stDev_tauX;
  this.stDev_tauY = stDev_tauY;
 }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

 public double StDev_fx() { return stDev_fx; }
 public double StDev_fy() { return stDev_fy; }
 public double StDev_cx() { return stDev_cx; }
 public double StDev_cy() { return stDev_cy; }
 public double StDev_k1() { return stDev_k1; }
 public double StDev_k2() { return stDev_k2; }
 public double StDev_k3() { return stDev_k3; }
 public double StDev_k4() { return stDev_k4; }
 public double StDev_k5() { return stDev_k5; }
 public double StDev_k6() { return stDev_k6; }
 public double StDev_p1() { return stDev_p1; }
 public double StDev_p2() { return stDev_p2; }
 public double StDev_s1() { return stDev_s1; }
 public double StDev_s2() { return stDev_s2; }
 public double StDev_s3() { return stDev_s3; }
 public double StDev_s4() { return stDev_s4; }
 public double StDev_tauX() { return stDev_tauX; }
 public double StDev_tauY() { return stDev_tauY; }

    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/

 public static StdDevIntrinsicsVector fromMat(Mat mat)
 {
  // OpenCV matrix structure: [fx; fy; cx; cy; k1; k2; p1; p2; k3; k4; k5; k6; s1; s2; s3; s4; τx; τy]
  double fx = mat.get(0,0)[0];
  double fy = mat.get(1,0)[0];
  double cx = mat.get(2,0)[0];
  double cy = mat.get(3,0)[0];
  double k1 = mat.get(4,0)[0];
  double k2 = mat.get(5,0)[0];
  double p1 = mat.get(6,0)[0];
  double p2 = mat.get(7,0)[0];
  double k3 = mat.get(8,0)[0];
  double k4 = mat.get(9,0)[0];
  double k5 = mat.get(10,0)[0];
  double k6 = mat.get(11,0)[0];
  double s1 = mat.get(12,0)[0];
  double s2 = mat.get(13,0)[0];
  double s3 = mat.get(14,0)[0];
  double s4 = mat.get(15,0)[0];
  double tauX = mat.get(16,0)[0];
  double tauY = mat.get(17,0)[0];

  return new StdDevIntrinsicsVector(fx, fy, cx, cy, k1, k2, k3, k4, k5, k6, p1, p2, s1, s2, s3, s4, tauX, tauY);
 }

    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/

 public Mat toMat()
 {
  Mat m = new Mat(18,1,CV_64F);

  m.put(0, 0, stDev_fx);
  m.put(1, 0, stDev_fy);
  m.put(2, 0, stDev_cx);
  m.put(3, 0, stDev_cy);
  m.put(4, 0, stDev_k1);
  m.put(5, 0, stDev_k2);
  m.put(6, 0, stDev_p1);
  m.put(7, 0, stDev_p2);
  m.put(8, 0, stDev_k3);
  m.put(9, 0, stDev_k4);
  m.put(10, 0, stDev_k5);
  m.put(11, 0, stDev_k6);
  m.put(12, 0, stDev_s1);
  m.put(13, 0, stDev_s2);
  m.put(14, 0, stDev_s3);
  m.put(15, 0, stDev_s4);
  m.put(16, 0, stDev_tauX);
  m.put(17, 0, stDev_tauY);

  return m;
 }
}
