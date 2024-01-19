package model.correspondence;

import model.image.Frame;
import model.image.StereoImage;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.stream.Collectors;

import static model.image.ImageUtilities.cvtBGR2GRAY;

public class CheckerboardUtilities
{
    // PRIVATE METHOD TO ACT DIRECTLY ON MAT

    private static MatOfPoint2f findCorners(Mat image, Checkerboard cb, SearchParams params)
    {
        // Create Mat to store image points
        MatOfPoint2f foundCorners = new MatOfPoint2f();

        // Convert input image to grayscale
        Mat imageGRAY = cvtBGR2GRAY(image);

        // Run fast checkerboard detection (speeds up method call when no checkerboard detected)
        boolean cbFound = Calib3d.findChessboardCorners(
                imageGRAY,
                cb.PatternSize(),
                foundCorners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH
                        + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                        + Calib3d.CALIB_CB_FAST_CHECK
        );
        
        // Sub-pixel refinement of corner locations if entire chessboard detected
        if (cbFound)
        {
            // Do refinement
            Imgproc.cornerSubPix(
                    imageGRAY,
                    foundCorners,
                    params.WinSize(),
                    params.ZeroZone(),
                    params.TermCriteria()
            );
        }
        
        return foundCorners;  // N.B. returns empty Mat if checkerboard not found
    }

    // PUBLIC METHODS FOR SINGLE FRAME

    public static Projection findCorners(Frame image, Checkerboard cb, SearchParams params)
    {
        // Find checkerboard corners in image
        MatOfPoint2f corners = findCorners(image.Data(), cb, params);

        // If all corners found, create a new mapping
        if (!corners.empty())
        {
            return new Projection(cb.ObjectPoints(), corners, image.Resolution());
        }
        return null;
    }

    public static Projection findCorners(Frame image, Checkerboard cb)
    {
        return findCorners(image, cb, new SearchParams());
    }

    // PUBLIC METHODS FOR SINGLE STEREO IMAGE

    public static StereoProjection findCornersStereo(StereoImage image, Checkerboard cb, SearchParams params)
    {
        // Find checkerboard corners in L & R images
        MatOfPoint2f cornersLeft = findCorners(image.Left().Data(), cb, params);
        MatOfPoint2f cornersRight = findCorners(image.Right().Data(), cb, params);

        // If found in both, create a new mapping
        if (!cornersLeft.empty() && !cornersRight.empty())
        {
            return new StereoProjection(cb.ObjectPoints(), cornersLeft, cornersRight, image.Resolution());
        }
        return null;
    }

    public static StereoProjection findCornersStereo(StereoImage image, Checkerboard cb)
    {
        return findCornersStereo(image, cb, new SearchParams());
    }

    // PUBLIC METHODS FOR MULTIPLE STEREO IMAGES

    //TODO N.B. THESE WILL RETURN LISTS CONTAINING NULLS IF CHECKERBOARD NOT FOUND IN L&R.  NEED TO HANDLE IN CALIBRATION.

    public static List<StereoProjection> findCornersStereo(List<StereoImage> images, Checkerboard cb, SearchParams params)
    {
        return images.stream().map(i -> findCornersStereo(i,cb,params)).collect(Collectors.toList());
    }

    public static List<StereoProjection> findCornersStereo(List<StereoImage> images, Checkerboard cb)
    {
        return findCornersStereo(images, cb, new SearchParams());
    }
}
