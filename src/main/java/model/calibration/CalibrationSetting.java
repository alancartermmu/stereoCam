// TODO - javadoc
// TODO - unit tests

package model.calibration;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.TermCriteria;

public class CalibrationSetting
{
    /*---------
     * FIELDS *
     ---------*/

    private boolean enableK3 = true;
    private boolean enableTangentialDistortion = true;
    private int termMaxCount = 100;
    private double termEpsilon = 0.001;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public CalibrationSetting(){}

    public CalibrationSetting(boolean enableK3, boolean enableTangentialDistortion)
    {
        this.enableK3 = enableK3;
        this.enableTangentialDistortion = enableTangentialDistortion;
    }

    public CalibrationSetting(
            boolean enableK3,
            boolean enableTangentialDistortion,
            int termMaxCount,
            double termEpsilon
    )
    {
        this(enableK3, enableTangentialDistortion);
        this.termMaxCount = termMaxCount;
        this.termEpsilon = termEpsilon;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/
    
    public boolean K3Enabled() { return enableK3; }
    public void enableK3(boolean bool) { this.enableK3 = bool; }
    public boolean TangentialDistortionEnabled() { return enableTangentialDistortion; }
    public void enableTangentialDistortion(boolean bool) { this.enableTangentialDistortion = bool; }
    public int TermMaxCount() { return termMaxCount; }
    public void setTermMaxCount(int termMaxCount) { this.termMaxCount = termMaxCount; }
    public double TermEpsilon() { return termEpsilon; }
    public void setTermEpsilon(double termEpsilon) { this.termEpsilon = termEpsilon; }
    
    /*----------
     * METHODS *
     ----------*/
    
    public TermCriteria TermCriteria()
    {
        return new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, termMaxCount, termEpsilon);
    }

    public int Flags()
    {
        return (
                (enableTangentialDistortion ? 0 : Calib3d.CALIB_FIX_TANGENT_DIST + Calib3d.CALIB_ZERO_TANGENT_DIST)
                + (enableK3 ? 0 : Calib3d.CALIB_FIX_K3)
        );
    }

    @Override
    public String toString()
    {
        String k3 = enableK3 ? "YES" : "NO";
        String tang = enableTangentialDistortion ? "YES" : "NO";

        return String.format(
            "CALIBRATION SETTINGS: \r\n" +
                    "\t K3 enabled = %s \r\n" +
                    "\t Tangential distortion enabled = %s \r\n" +
                    "\t Term max count = %d \r\n" +
                    "\t Term epsilon = %f \r\n",
            k3, tang, termMaxCount, termEpsilon
    );
    }
}
