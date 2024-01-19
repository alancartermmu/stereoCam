package model.image;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.Objects;

// Wrapper class for a Mat containing BGR image data
public class Frame
{
    /*---------
     * FIELDS *
     ---------*/

    private final Mat imageData;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public Frame(Mat imageMat) { this.imageData = imageMat; }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public Mat Data() { return this.imageData; }

    /*----------
     * METHODS *
     ----------*/

    public Size Resolution() { return this.imageData.size(); }

    public double Width() { return this.Resolution().width; }

    public double Height() { return this.Resolution().height; }

    public boolean isEmpty() { return imageData.empty(); }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frame frame = (Frame) o;
        return Objects.equals(imageData, frame.imageData);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(imageData);
    }

}
