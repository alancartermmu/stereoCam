package test;

import model.calibration.Calibration;
import model.calibration.CalibrationSetting;
import model.calibration.StereoCalibration;
import model.correspondence.Checkerboard;
import model.correspondence.SearchParams;
import model.correspondence.StereoProjection;
import model.image.StereoImageSet;
import org.opencv.core.Core;

import java.util.List;

import static model.calibration.StereoCalibration.calibrateStereo;
import static model.correspondence.CheckerboardUtilities.findCornersStereo;
import static model.image.ImageUtilities.importImages;

public class CalibrateFromTestImages
{
    private static Calibration calibration;
    
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        String path = "D:/Cloud/OneDrive - MMU/6G7V0007_2122_9F MSc Project/Images/Set3";
        StereoImageSet set = importImages(path);
        Checkerboard cb = new Checkerboard(9,14,25);
        
        CalibrationSetting cset = new CalibrationSetting(true,false);
        //SearchParams sparams = new SearchParams(17,-1,(int) 1.0E9,1.0E-9);
        SearchParams sparams = new SearchParams();
        
        List<StereoProjection> projections = findCornersStereo(set.getAll(),cb,sparams);
        StereoCalibration scal = calibrateStereo(projections,cset);
        
        System.out.println("K1 = " + scal.CalibrationLeft().K().toMat().dump());
        System.out.println("K2 = " + scal.CalibrationRight().K().toMat().dump());
        
        System.out.println("dist1 = " + scal.CalibrationLeft().Distortion().toMat().dump());
        System.out.println("dist2 = " + scal.CalibrationRight().Distortion().toMat().dump());
        
        System.out.println("F = " + scal.F().toMat().dump());
        System.out.println("E = " + scal.E().toMat().dump());
        System.out.println("R = " + scal.R().toMat().dump());
        System.out.println("T = " + scal.T().toMat().dump());
        
        System.out.println("Reproj = " + scal.RmsReprojectionError());
    }
}
