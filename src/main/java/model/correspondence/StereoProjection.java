package model.correspondence;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

public class StereoProjection
{
    /*---------
     * FIELDS *
     ---------*/

    private final MatOfPoint3f objectPoints;
    private final MatOfPoint2f imagePointsLeft;
    private final MatOfPoint2f imagePointsRight;
    private final Size resolution;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public StereoProjection(
            MatOfPoint3f objectPoints,
            MatOfPoint2f imagePointsLeft,
            MatOfPoint2f imagePointsRight,
            Size resolution
    )
    {
        this.objectPoints = objectPoints;
        this.imagePointsLeft = imagePointsLeft;
        this.imagePointsRight = imagePointsRight;
        this.resolution = resolution;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public MatOfPoint3f ObjectPoints() { return objectPoints; }
    public MatOfPoint2f ImagePointsLeft() { return imagePointsLeft; }
    public MatOfPoint2f ImagePointsRight() { return imagePointsRight; }
    public Size ImageSize() { return resolution; }

    /*----------
     * METHODS *
     ----------*/

    public Projection LeftProjection()
    {
        return new Projection(objectPoints,imagePointsLeft, resolution);
    }

    public Projection RightProjection()
    {
        return new Projection(objectPoints,imagePointsRight, resolution);
    }
}
