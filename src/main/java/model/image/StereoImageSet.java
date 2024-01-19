package model.image;

import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

// Wrapper class for a List of StereoImage objects with identical sizes
public class StereoImageSet
{
    /*---------
     * FIELDS *
     ---------*/
    
    private final List<StereoImage> images;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public StereoImageSet(List<StereoImage> images)
    {
        this.images = equalSize(images) ? images : null;
    }

    public StereoImageSet(StereoImage image)
    {
        this();
        this.images.add(image);
    }

    public StereoImageSet()
    {
        this.images = new ArrayList<>();
    }
    
    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/
    
    public List<StereoImage> getAll() { return this.images; }

    /*----------
     * METHODS *
     ----------*/

    public Size Size()
    {
        return (!this.images.isEmpty()) ? this.images.get(0).Resolution() : null;
    }

    // TODO add logic for if set already contains image
    public boolean add(StereoImage image)
    {
        if (images.isEmpty() || image.Resolution().equals(this.Size()))
        {
            images.add(image);
            return true;
        }
        return false;
    }

    public int nImages()
    {
        return this.images.size();
    }

    private static boolean equalSize(List<StereoImage> images)
    {
        for (StereoImage i : images)
        {
            if (!i.Resolution().equals(images.get(0).Resolution()))
            {
                return false;
            }
        }
        return true;
    }
}
