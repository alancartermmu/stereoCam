package model.camera;

import java.util.Objects;

public class CameraProperty
{
    /*---------
     * FIELDS *
     ---------*/

    private String name;
    private final int id;
    private double currentValue;
    private double defaultValue;
    private double minValue;
    private double maxValue;

    /*---------------
     * CONSTRUCTORS *
     ---------------*/

    public CameraProperty(String name, int id, double currentValue, double defaultValue, double minValue, double maxValue)
    {
        this.name = name;
        this.id = id;
        this.currentValue = currentValue;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public CameraProperty(String name, int id, double defaultValue, double minValue, double maxValue)
    {
        this(name, id, defaultValue, defaultValue, minValue, maxValue);
    }

    public CameraProperty(int id, double currentValue, double defaultValue, double minValue, double maxValue)
    {
        this.id = id;
        this.currentValue = currentValue;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public CameraProperty(int id, double defaultValue, double minValue, double maxValue)
    {
        this(id, defaultValue, defaultValue, minValue, maxValue);
    }

    protected CameraProperty(int id)
    {
        this.id = id;
    }

    /*--------------------
     * GETTERS & SETTERS *
     --------------------*/

    public String Name() { return this.name.isEmpty() ? null : this.name; }
    public int Id() { return id; }
    public double Value() { return currentValue; }
    public void setValue(double value) { this.currentValue = value; }
    public double DefaultValue() { return defaultValue; }
    public double MinValue() { return minValue; }
    public double MaxValue() { return maxValue; }
    
    /*----------
     * METHODS *
     ----------*/
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraProperty that = (CameraProperty) o;
        return id == that.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    public boolean updateCamera(StereoCamera c)
    {
        return c.setProperty(this.id, this.currentValue);
    }
}
