package model.image;

import org.opencv.core.Mat;
import org.opencv.core.Size;

public class StereoImage
{
    /*---------
     * FIELDS *
     ---------*/
    
    private final Frame left;
    private final Frame right;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public StereoImage(Frame left, Frame right)
    {
        if (left.Resolution().equals(right.Resolution()))
        {
            this.left = left;
            this.right = right;
        }
        else
        {
            this.left = null;
            this.right = null;
        }
    }

    public StereoImage(Frame[] frames)
    {
        this(frames[0], frames[1]);
    }

    public StereoImage(Frame frame)
    {
        //TODO - handle invalid image that can't be split in half (null)
        this(ImageUtilities.hSplit(frame));
    }

    public StereoImage(Mat imageData)
    {
        this(new Frame(imageData));
    }

    public StereoImage(Mat left, Mat right)
    {
        this(new Frame(left), new Frame(right));
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public Frame Left() { return this.left; }
    public Frame Right() { return this.right; }

    /*----------
     * METHODS *
     ----------*/

    public Size Resolution()
    {
        return left.Resolution().equals(right.Resolution()) ? left.Resolution() : null;
    }

    public Frame toSingleImage()
    {
        return new Frame(ImageUtilities.hConcat(this.left.Data(), this.right.Data()));
    }

    public Size CombinedResolution()
    {
        return this.toSingleImage().Resolution();
    }

}
