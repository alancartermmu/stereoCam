package test;// TODO - javadoc
// TODO - unit tests

import model.calibration.CalibrationSetting;
import model.calibration.StereoCalibration;
import model.correspondence.Checkerboard;
import model.correspondence.CheckerboardUtilities;
import model.correspondence.StereoProjection;
import model.image.ImageUtilities;
import model.image.StereoImageSet;
import org.opencv.core.Core;

import java.util.List;

public class TestCalibration
{
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        /* IMPORT IMAGES */

        String pathS = "D:/Cloud/OneDrive - MMU/6G7V0007_2122_9F MSc Project/Images/Set3/";
        StereoImageSet imagesStereo = ImageUtilities.importImages(pathS);

        /* DETECTION */
        Checkerboard cb = new Checkerboard(9,14,25);
        List<StereoProjection> projections = CheckerboardUtilities.findCornersStereo(imagesStereo.getAll(), cb);
        System.out.println("Number of mappings: " + projections.size());

        /* CALIBRATION */
        CalibrationSetting setting = new CalibrationSetting(true,true);
        StereoCalibration cal = StereoCalibration.calibrateStereo(projections,setting);

        //System.out.println("PV Errors Left: " + cal.CalibrationLeft().PerViewReprojectionErrors().dump());
        //System.out.println("PV Errors Right: " + cal.CalibrationRight().PerViewReprojectionErrors().dump());

        //System.out.println(calibrationInput.getIntrinsicsLeft().toString());
        //System.out.println(calibrationInput.getIntrinsicsRight().toString());
        System.out.println("Essential Matrix: " + cal.E().toMat().dump());
        System.out.println("Fundamental Matrix: " + cal.F().toMat().dump());
        System.out.println("R: " + cal.R().toMat().dump());
        System.out.println("T: " + cal.T().toMat().dump());
        System.out.println("Overall error: " + cal.RmsReprojectionError());

    }
}
