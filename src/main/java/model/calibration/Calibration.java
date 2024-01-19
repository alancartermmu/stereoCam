package model.calibration;

import model.camera.CameraIntrinsics;
import model.camera.intrinsics.*;
import model.correspondence.Projection;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Wrapper class for the OpenCV Calib3d.calibrateCameraExtended method
// TODO handle invalid projections (i.e. incomplete checkerboard)
public class Calibration
{
    /*---------
     * FIELDS *
     ---------*/

    private List<Projection> projections;
    private Size imageSize;
    private IntrinsicMatrix K;
    private DistortionVector dc;
    private List<RotationVector> perViewRotations;
    private List<TranslationVector> perViewTranslations;
    private StdDevIntrinsicsVector stdDevIntrinsics;
    private List<StdDevExtrinsicsVector> stdDevExtrinsics;
    private List<Double> perViewReprojectionErrors;
    private double rmsReprojectionError;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    private Calibration(){}  // prevent instantiation

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public List<Projection> Projections() { return projections; }
    public Size ImageSize() { return imageSize; }
    public IntrinsicMatrix K() { return K; }
    public DistortionVector Distortion() { return dc; }
    public List<RotationVector> PerViewRotations() { return perViewRotations; }
    public List<TranslationVector> PerViewTranslations() { return perViewTranslations; }
    public StdDevIntrinsicsVector StdDevIntrinsics() { return stdDevIntrinsics; }
    public List<StdDevExtrinsicsVector> PerViewStdDevExtrinsics() { return stdDevExtrinsics; }
    public List<Double> PerViewReprojectionErrors() { return perViewReprojectionErrors; }
    public double RmsReprojectionError() { return rmsReprojectionError; }

    /*----------
     * METHODS *
     ----------*/

    public static Calibration calibrateSingle(List<Projection> projections, CalibrationSetting settings)
    {
        // Initialise calibration object
        Calibration cal = new Calibration();

        // Remove any null projections and assign to calibration object
        cal.projections = projections.stream().filter(Objects::nonNull).collect(Collectors.toList());

        // Proceed only if list is not empty and projections are all same resolution
        if (!cal.projections.isEmpty() && cal.projections.stream().map(Projection::Resolution).distinct().count() == 1)
        {
            cal.imageSize = cal.projections.get(0).Resolution();

            // Holding Mat objects
            Mat mat_K= new Mat();
            Mat mat_dc = new Mat();
            Mat mat_perViewErrors = new Mat();
            Mat mat_stDevIntrinsics = new Mat();
            Mat mat_stDevExtrinsics = new Mat();
            List<Mat> mat_perViewR = new ArrayList<>();
            List<Mat> mat_perViewT = new ArrayList<>();

            // Run OpenCV calibration routine
            cal.rmsReprojectionError = Calib3d.calibrateCameraExtended(
                    cal.projections.stream().map(Projection::ObjectPoints).collect(Collectors.toList()),
                    cal.projections.stream().map(Projection::ImagePoints).collect(Collectors.toList()),
                    cal.imageSize,
                    mat_K,
                    mat_dc,
                    mat_perViewR,
                    mat_perViewT,
                    mat_stDevIntrinsics,
                    mat_stDevExtrinsics,
                    mat_perViewErrors,
                    settings.Flags(),
                    settings.TermCriteria()
            );

            cal.K = IntrinsicMatrix.fromMat(mat_K);
            cal.dc = DistortionVector.fromMat(mat_dc);
            cal.stdDevIntrinsics = StdDevIntrinsicsVector.fromMat(mat_stDevIntrinsics);
            cal.perViewRotations = mat_perViewR.stream().map(RotationVector::fromMat).collect(Collectors.toList());
            cal.perViewTranslations = mat_perViewT.stream().map(TranslationVector::fromMat).collect(Collectors.toList());
            cal.perViewReprojectionErrors = new ArrayList<>();
            cal.stdDevExtrinsics = new ArrayList<>();

            for (int i = 0; i < mat_perViewErrors.rows(); i++)
            { cal.perViewReprojectionErrors.add(mat_perViewErrors.get(i,0)[0]); }

            for (int i = 0; i < mat_stDevExtrinsics.rows(); i = i + 6)
            { cal.stdDevExtrinsics.add(StdDevExtrinsicsVector.fromMat(mat_stDevExtrinsics.rowRange(i, i + 6))); }

            // Return result object
            return cal;
        }
        return null;
    }

    public static Calibration calibrateSingle(List<Projection> projections)
    {
        return calibrateSingle(projections, new CalibrationSetting());
    }

    public CameraIntrinsics CameraIntrinsics()
    {
        return new CameraIntrinsics(K,dc,imageSize,rmsReprojectionError);
    }
}
