package model.camera;

import java.util.ArrayList;
import java.util.List;

public class CameraProperties
{
    // wrapper class for list of CameraProperty objects
    
    /*---------
     * FIELDS *
     ---------*/
    
    private final List<CameraProperty> properties;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public CameraProperties(List<CameraProperty> properties)
    {
        this.properties = properties;
    }

    public CameraProperties()
    {
        this(new ArrayList<>());
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public List<CameraProperty> getAll() { return properties; }

    /*----------
     * METHODS *
     ----------*/

    /* Add a control */
    public boolean add(int id, double def, double min, double max)
    {
        return this.add(new CameraProperty(id,def,min,max));
    }

    public boolean add(CameraProperty p)
    {
        if (!this.properties.contains(p))
        {
            return this.properties.add(p);
        }
        return false;
    }

    public boolean addMultiple(List<CameraProperty> list)
    {
        boolean result = true;
        for (CameraProperty p : list)
        {
            result &= this.add(p);
        }

        return result;
    }

    /* Get a control */
    public CameraProperty get(int id)
    {
        return this.get(new CameraProperty(id));
    }

    public CameraProperty get(CameraProperty p)
    {
        int index = this.properties.indexOf(p);
        if (index > -1)
        {
            return this.properties.get(index);
        }
        return null;
    }

    /* Remove a control */
    public boolean remove(int id)
    {
        return this.remove(new CameraProperty(id));
    }

    public boolean remove(CameraProperty p)
    {
        return this.properties.remove(p);
    }

    /* Update a camera */
    public boolean updateCamera(StereoCamera c)
    {
        boolean result = true;

        for (CameraProperty p : this.getAll())
        {
            result &= p.updateCamera(c);
        }

        return result;
    }
}
