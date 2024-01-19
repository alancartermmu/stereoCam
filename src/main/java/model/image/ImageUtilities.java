package model.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ImageUtilities
{
    // wrapper function for OpenCV hconcat() with return object
    public static Mat hConcat(Mat[] imgs)
    {
        // TODO - add input validation (same height and width), throw exception if not
        // TODO - null handling for either image or completely null array
        Mat joined = new Mat();
        Core.hconcat(Arrays.asList(imgs), joined);

        return joined;
    }

    // wrapper function for OpenCV hconcat() with return object
    public static Mat hConcat(Mat img1, Mat img2)
    {
        // TODO - try/catch block or null handling
        return hConcat(new Mat[]{img1, img2});
    }

    // Divides an image in half (reverse of hConcat)
    public static Mat[] hSplit(Mat img)
    {
        int cols = img.cols();
        if (cols % 2 == 0)
        {
            Mat imgL = img.colRange(0,cols/2);
            Mat imgR = img.colRange(cols/2,cols);

            return new Mat[]{imgL, imgR};
        }
        return null;
    }

    public static Frame[] hSplit(Frame image)
    {
        Mat[] mats = hSplit(image.Data());
        if (mats != null)
        {
            Frame imgL = new Frame(mats[0]);
            Frame imgR = new Frame(mats[1]);

            return new Frame[]{imgL, imgR};
        }
        return null;
    }
    
    public static Image MatToFxImage(Mat mat)
    {
        BufferedImage bimg = ImageUtilities.MatToBufferedImg(mat);
        return SwingFXUtils.toFXImage(bimg, null);
    }

    public static BufferedImage MatToBufferedImg(Mat mat)
    {
        // Create empty BufferedImage
        BufferedImage bImg = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);

        // Get BufferedImage's internal byte data array to use as a buffer
        byte[] buffer = ((DataBufferByte) bImg.getRaster().getDataBuffer()).getData();

        // Read data from Mat using BufferedImage's byte array as the buffer (efficient -> avoids array copy)
        mat.get(0, 0, buffer);

        // Return BufferedImage
        return bImg;
    }

    public static Mat BufferedImgToMat(BufferedImage bimg)
    {
        // Create empty Mat
        Mat mat = new Mat(bimg.getHeight(), bimg.getWidth(), CvType.CV_8UC3);

        // Get data from BufferedImage's internal buffer
        byte[] buffer = ((DataBufferByte) bimg.getRaster().getDataBuffer()).getData();

        // Write data from buffer to Mat
        mat.put(0, 0, buffer);

        // Return Mat
        return mat;
    }

    public static Mat cvtBGR2GRAY(Mat imgBGR)
    {
        Mat imgGRAY = new Mat();
        Imgproc.cvtColor(imgBGR, imgGRAY, Imgproc.COLOR_BGR2GRAY);

        return imgGRAY;
    }

    public static boolean exportImage(Mat imageMat, String destinationFolder, String format)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String filename = "img_" + dtf.format(now) + "." + format;
        String path = destinationFolder + filename;

        return Imgcodecs.imwrite(path,imageMat);
    }

    public static StereoImageSet importImages(String path)
    {
        File dir = new File(path);
        File[] files = dir.listFiles();
        StereoImageSet images = new StereoImageSet();

        if (files != null)
        {
            for (File f : files)
            {
                Mat m = Imgcodecs.imread(f.getAbsolutePath());
                images.add(new StereoImage(m));
            }
        }

        return images;
    }

    public static Mat drawPointsOnImage(
            Mat sourceImage,
            MatOfPoint2f points,
            Scalar markerColour,
            int markerType,
            int markerSize,
            int lineThickness
    )
    {
        Mat targetImage = sourceImage.clone();
        for (Point p : points.toArray())
        {
            Imgproc.drawMarker(targetImage, p, markerColour, markerType, markerSize, lineThickness);
        }

        return targetImage;
    }
    
    public static Mat drawLinesOnImage(
            Mat sourceImage,
            int divs,
            Scalar lineColour,
            int lineType,
            int lineThickness
    )
    {
        Mat targetImage = sourceImage.clone();
        int width = sourceImage.width();
        int height = sourceImage.height();
        double divHeight = (double) height / divs;
        
        for (int i = 0; i <= divs; i++)
        {
            int y = (int) (i * divHeight);
            Point p1 = new Point(0,y);
            Point p2 = new Point(width,y);
            Imgproc.line(targetImage,p1,p2,lineColour,lineThickness,lineType);
        }
        
        return targetImage;
    }
}
