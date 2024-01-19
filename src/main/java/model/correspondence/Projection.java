package model.correspondence;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

public class Projection
{
    /*---------
     * FIELDS *
     ---------*/

    private final MatOfPoint3f objectPoints;
    private final MatOfPoint2f imagePoints;
    private final Size resolution;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public Projection(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Size resolution)
    {
        this.objectPoints = objectPoints;
        this.imagePoints = imagePoints;
        this.resolution = resolution;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public MatOfPoint3f ObjectPoints() { return objectPoints; }
    public MatOfPoint2f ImagePoints() { return imagePoints; }
    public Size Resolution() { return resolution; }
}
