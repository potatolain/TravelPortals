package com.bukkit.cppchriscpp.TravelPortals;

/**
 * @(#)WarpPoint.java
 *
 * Stores warp locations for users.
 *
 * @author cppchriscpp
 * @version 1.00
 */

/**
 * A quick serializable storage medium for warping points.
 * @author cppchriscpp
 */
public class WarpPoint implements java.io.Serializable {
    /**
     * Used to store the position of the warp.
     */
    private int x,y,z;
    /**
     * Stores the name of the warp.
     */
    private String name;
    /**
     * Stores the destination of the warp.
     */
    private String destination;

    /**
     * The time this was last used.
     */
    private transient long lastused;

    /**
     * The amount of time it takes for this to cool down so it can be
     * used again.
     */
    private static final transient /* enough keywords? */ int cooldown = 5000;

    /**
     * Default constructor. I suggest against using this.
     */
    public WarpPoint() {
    	x=0; y=0; z=0; name = "";
    	lastused = 0;
    }

    /**
     * Creates a warp point at a position. This is the most likely constructor you'll use.
     * @param _x The X coordinate of the warp point's position.
     * @param _y The Y coordinate of the warp point's position.
     * @param _z The Z coordinate of the warp point's position.
     */
    public WarpPoint(int _x, int _y, int _z)
    {
    	x = _x; y = _y; z = _z; name = ""; destination = "";
    	lastused = 0;
    }

    /**
     * Creates a warp point with the name given. May be useful in the future.
     * @param _x The X coordinate of the warp point's position.
     * @param _y The Y coordinate of the warp point's position.
     * @param _z The Z coordinate of the warp point's position.
     * @param _name The name of this warp.
     */
    public WarpPoint(int _x, int _y, int _z, String _name)
    {
    	x = _x; y = _y; z = _z; name = _name; destination = "";
    	lastused = 0;
    }

    /**
     * Check to see if this is a valid warp for use. (Has a name and destination)
     * @return true if the destination and name are both set, otherwise false.
     */
    public boolean isValid()
    {
    	return (!(name.equals("") && destination.equals("")));
    }
    /**
     * Get the X coordinate of this point.
     * @return The X coordinate of this point.
     */
    public int getX()
    {
    	return x;
    }

    /**
     * Get the Y coordinate of this point.
     * @return The Y coordinate of this point.
     */
    public int getY()
    {
    	return y;
    }

    /**
     * Get the Z coordinate of this point.
     * @return The Z coordinate of this point.
     */
    public int getZ()
    {
    	return z;
    }

    /**
     * Retrieve the name of this point.
     * @return The name given to this point.
     */
    public String getName()
    {
            return name;
    }

    /**
     * Sets the name of this point.
     * @param The new name for this point.
     */
    public void setName(String n)
    {
            name = n;
    }

    /**
     * Gets the name of the destination portal for this point.
     * @return The name of the destination portal.
     */
    public String getDestination()
    {
            return destination;
    }

    /**
     * Sets the name of the destination portal.
     * @param n New name for this portal's destination.
     */
    public void setDestination(String n)
    {
            destination = n;
    }

    /**
     * Tests to see if this point has a destination set.
     * @return true if this point has a destination; false otherwise.
     */
    public boolean hasDestination()
    {
            return !destination.equals("");
    }

    /**
     * Checks to see if this point has a name.
     * @return true if this point has a name; false otherwise.
     */
    public boolean hasName()
    {
            return !name.equals("");
    }

    /**
     * Gets the last used time.
     * @return The last time it was used (ms)
     */
    public long getLastUsed()
    {
        return lastused;
    }

    /**
     * Set the lastused time to now.
     */
    public void setLastUsed()
    {
        lastused = System.currentTimeMillis();
    }

    /**
     * Set the time this was last used
     * @time the new time this was used.
     */
    public void setLastUsed(long time)
    {
        lastused = time;
    }

    /**
     * Check if this portal is usable. (Time limit)
     */
    public boolean isUsable()
    {
        return (lastused+cooldown < System.currentTimeMillis());
    }

}