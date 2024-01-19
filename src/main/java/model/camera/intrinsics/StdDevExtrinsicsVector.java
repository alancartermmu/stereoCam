package model.camera.intrinsics;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_64F;

public class StdDevExtrinsicsVector
{
    /*---------
     * FIELDS *
     ---------*/
 
 private final double stDev_r1;
 private final double stDev_r2;
 private final double stDev_r3;
 private final double stDev_t1;
 private final double stDev_t2;
 private final double stDev_t3;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/
 
 public StdDevExtrinsicsVector(double stDev_r1, double stDev_r2, double stDev_r3,
                               double stDev_t1, double stDev_t2, double stDev_t3)
 {
  this.stDev_r1 = stDev_r1;
  this.stDev_r2 = stDev_r2;
  this.stDev_r3 = stDev_r3;
  this.stDev_t1 = stDev_t1;
  this.stDev_t2 = stDev_t2;
  this.stDev_t3 = stDev_t3;
 }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/
 
 public double StDev_R1() { return stDev_r1; }
 public double StDev_R2() { return stDev_r2; }
 public double StDev_R3() { return stDev_r3; }
 public double StDev_T1() { return stDev_t1; }
 public double StDev_T2() { return stDev_t2; }
 public double StDev_T3() { return stDev_t3; }
 
    /*-----------------------------------
     * METHODS - Instantiation From Mat *
     -----------------------------------*/
 
 public static StdDevExtrinsicsVector fromMat(Mat mat)
 {
  // OpenCV matrix structure: [fx; fy; cx; cy; k1; k2; p1; p2; k3; k4; k5; k6; s1; s2; s3; s4; τx; τy]
  double r1 = mat.get(0,0)[0];
  double r2 = mat.get(1,0)[0];
  double r3 = mat.get(2,0)[0];
  double t1 = mat.get(3,0)[0];
  double t2 = mat.get(4,0)[0];
  double t3 = mat.get(5,0)[0];
  
  return new StdDevExtrinsicsVector(r1, r2, r3, t1, t2, t3);
 }
 
    /*------------------------------
     * METHODS - Conversion To Mat *
     ------------------------------*/
 
 public Mat toMat()
 {
  Mat m = new Mat(6,1,CV_64F);
  
  m.put(0, 0, stDev_r1);
  m.put(1, 0, stDev_r2);
  m.put(2, 0, stDev_r3);
  m.put(3, 0, stDev_t1);
  m.put(4, 0, stDev_t2);
  m.put(5, 0, stDev_t3);
  
  return m;
 }
}
