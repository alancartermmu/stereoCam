package model.calibration;

import model.camera.*;
import model.camera.intrinsics.*;
import model.correspondence.Projection;
import model.correspondence.StereoProjection;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Wrapper class for the OpenCV Calib3d.stereoCalibrateExtended method
// TODO handle invalid stereo projections (i.e. cb in one but not other)
public class StereoCalibration
{
    /*---------
     * FIELDS *
     ---------*/

    private List<StereoProjection> projections;
    private Size imageSize;
    private Calibration calibrationLeft;
    private Calibration calibrationRight;
    private EssentialMatrix E;
    private FundamentalMatrix F;
    private RotationMatrix R;
    private TranslationVector T;
    private final Mat perViewReprojectionErrors = new Mat();
    private double rmsReprojectionError;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    private StereoCalibration(){}  // prevent instantiation

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public List<StereoProjection> Projections() { return projections; }
    public Size ImageSize() { return imageSize; }
    public Calibration CalibrationLeft() { return calibrationLeft; }
    public Calibration CalibrationRight() { return calibrationRight; }
    public EssentialMatrix E() { return E; }
    public FundamentalMatrix F() { return F; }
    public RotationMatrix R() { return R; }
    public TranslationVector T() { return T; }
    public Mat PerViewReprojectionErrors() { return perViewReprojectionErrors; }
    public double RmsReprojectionError() { return rmsReprojectionError; }

    /*----------
     * METHODS *
     ----------*/

    public static StereoCalibration calibrateStereo(List<StereoProjection> projections, CalibrationSetting settings)
    {
        // Initialise calibration object
        StereoCalibration cal = new StereoCalibration();

        // Remove any null projections and assign to calibration object
        cal.projections = projections.stream().filter(Objects::nonNull).collect(Collectors.toList());

        // Proceed only if list is not empty and projections are all same resolution
        if (!cal.projections.isEmpty() && cal.projections.stream().map(StereoProjection::ImageSize).distinct().count() == 1)
        {
            cal.imageSize = cal.projections.get(0).ImageSize();

            // Calibrate each camera individually first (to provide intrinsics)
            List<Projection> projL = cal.projections.stream().map(StereoProjection::LeftProjection).collect(Collectors.toList());
            List<Projection> projR = cal.projections.stream().map(StereoProjection::RightProjection).collect(Collectors.toList());
            cal.calibrationLeft = Calibration.calibrateSingle(projL,settings);
            cal.calibrationRight = Calibration.calibrateSingle(projR,settings);

            // Holding Mat objects
            Mat mat_E = new Mat();
            Mat mat_F = new Mat();
            Mat mat_R = new Mat();
            Mat mat_T = new Mat();

            // Run OpenCV stereo calibration routine
            cal.rmsReprojectionError = Calib3d.stereoCalibrateExtended(
                    cal.projections.stream().map(StereoProjection::ObjectPoints).collect(Collectors.toList()),
                    cal.projections.stream().map(StereoProjection::ImagePointsLeft).collect(Collectors.toList()),
                    cal.projections.stream().map(StereoProjection::ImagePointsRight).collect(Collectors.toList()),
                    cal.calibrationLeft.K().toMat(),            // precomputed above
                    cal.calibrationLeft.Distortion().toMat(),   // precomputed above
                    cal.calibrationRight.K().toMat(),           // precomputed above
                    cal.calibrationRight.Distortion().toMat(),  // precomputed above
                    cal.imageSize,
                    mat_R, mat_T, mat_E, mat_F,
                    cal.perViewReprojectionErrors,
                    settings.Flags() + Calib3d.CALIB_FIX_INTRINSIC, // don't recalculate intrinsics
                    settings.TermCriteria()
            );

            // Add holding Mats to calibration object
            cal.E = EssentialMatrix.fromMat(mat_E);
            cal.F = FundamentalMatrix.fromMat(mat_F);
            cal.R = RotationMatrix.fromMat(mat_R);
            cal.T = TranslationVector.fromMat(mat_T);

            return cal;
        }
        return null;
    }

    public static StereoCalibration calibrateStereo(List<StereoProjection> projections)
    {
        return calibrateStereo(projections, new CalibrationSetting());
    }

    public StereoIntrinsics StereoCameraIntrinsics()
    {
        return new StereoIntrinsics(
                imageSize,
                calibrationLeft.K(),
                calibrationRight.K(),
                calibrationLeft.Distortion(),
                calibrationRight.Distortion(),
                E,
                F,
                R,
                T,
                rmsReprojectionError
        );
    }

    public List<Double> LeftViewReprojectionErrors()
    {
        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < this.perViewReprojectionErrors.rows(); i++)
        {
            errors.add(this.perViewReprojectionErrors.get(i,0)[0]);
        }

        return errors;
    }

    public List<Double> RightViewReprojectionErrors()
    {
        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < this.perViewReprojectionErrors.rows(); i++)
        {
            errors.add(this.perViewReprojectionErrors.get(i,1)[0]);
        }

        return errors;
    }
    
    public int NumberOfViews()
    {
        return (int) projections.stream().filter(Objects::nonNull).count();
    }
    
    public int NumberOfPoints()
    {
        return projections.stream().filter(Objects::nonNull).mapToInt(p -> p.ObjectPoints().toList().size()).sum();
    }
}