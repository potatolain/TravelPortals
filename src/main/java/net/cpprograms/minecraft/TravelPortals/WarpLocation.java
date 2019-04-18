package net.cpprograms.minecraft.TravelPortals;

/**
 * @(#)WarpLocation.java
 *
 * Stores warp locations for users.
 *
 * @author cppchriscpp
 * @version 1.10
 */

import net.cpprograms.minecraft.TravelPortals.storage.PortalStorage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A quick serializable storage medium for warping points.
 * REPLACES WarpPoint
 * @author cppchriscpp
 */
public class WarpLocation implements java.io.Serializable {

	static final long serialVersionUID = 4523543646L;
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
	 * Is this portal hidden?
	 */
	private boolean hidden = false;

	/**
	 * Portal owner.
	 */
	private String owner = "";

	/**
	 * Where is the door?
	 * 0: Unknown
	 * 1: X-1 Y-1
	 * 2: X+1 Y-1
	 * 3: X-1 Y+1
	 * 4: X+1 Y+1
	 */
	private int doorpos = 0;


	/**
	 * The amount of time it takes for this to cool down so it can be
	 * used again.
	 */
	// private static final transient /* enough keywords? */ int cooldown = 5000;

	/**
	 * What world is this?
	 */
	private String world = "";

	/**
	 * Default constructor. I suggest against using this.
	 */
	public WarpLocation() {
		x=0; y=0; z=0; name = "";
		lastused = 0;
		world = "";
	}

	/**
	 * Creates a warp point at a position. This is the most likely constructor you'll use.
	 * @param _x The X coordinate of the warp point's position.
	 * @param _y The Y coordinate of the warp point's position.
	 * @param _z The Z coordinate of the warp point's position.
	 * @param _world The world to put this portal into.
	 */
	public WarpLocation(int _x, int _y, int _z, String _world)
	{
		x = _x; y = _y; z = _z; name = ""; destination = "";
		lastused = 0;
		world = _world;
	}

	/**
	 * Creates a warp point at a position.
	 * @param _x The X coordinate of the warp point's position.
	 * @param _y The Y coordinate of the warp point's position.
	 * @param _z The Z coordinate of the warp point's position.
	 * @param _doorpos The position of the door.
	 * @param _world The world to put this WarpLocation into.
	 * @Deprecated
	 */
	public WarpLocation(int _x, int _y, int _z, int _doorpos, String _world)
	{
		x = _x; y = _y; z = _z; name = ""; destination = "";
		lastused = 0;
		doorpos = _doorpos;
		world = _world;
	}

	/**
	 * Creates a warp point at a position. This is the most likely constructor you'll use.
	 * @param _x The X coordinate of the warp point's position.
	 * @param _y The Y coordinate of the warp point's position.
	 * @param _z The Z coordinate of the warp point's position.
	 * @param _doorpos The position of the door.
	 * @param _world The world to put this WarpLocation into.
	 * @param _owner The person who owns this.
	 */
	public WarpLocation(int _x, int _y, int _z, int _doorpos, String _world, String _owner)
	{
		x = _x; y = _y; z = _z; name = ""; destination = "";
		lastused = 0;
		doorpos = _doorpos;
		world = _world;
		owner = _owner;
	}

	/**
	 * Check to see if this is a valid warp for use. (Has a name and destination)
	 * @return true if the destination and name are both set, otherwise false.
	 */
	public boolean isValid()
	{
		return (!(name != null && name.isEmpty() && destination != null && destination.isEmpty()));
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
	 * Sets the name of this point.<br />
	 * <strong>Do not use this to change the name of an existing portal! Use {@link PortalStorage#namePortal(WarpLocation, String)}</strong>
	 * @param n The new name for this point.
	 */
	public void setName(String n)
	{
		name = n;
	}

	/**
	 * Check whether or not a certain player is the owner of a portal
	 * @param player The player to check
	 * @return Whether or not the player is the owner
	 */
	public boolean isOwner(Player player) {
		return hasOwnerId() ? player.getUniqueId().equals(getOwnerId()) : player.getName().equals(getOwnerName());
	}

	/**
	 * Gets the current owner of the portal.
	 * @return The owner of the portal.
	 */
	public String getOwner()
	{
		if (owner == null)
			owner = "";
		return owner;
	}

	/**
	 * Gets the name of the current owner of the portal.
	 * @return The name of the owner of the portal.
	 */
	public String getOwnerName()
	{
		return getOwner().split(",")[0];
	}

	/**
	 * Sets the current owner of the portal.
	 * @param player The new owner.
	 */
	public void setOwner(Player player) 
	{
		if (player == null)
			owner = null;
		else
			owner = player.getName() + "," + player.getUniqueId();
	}

	/**
	 * Sets the current owner of the portal. Where possible use {@link #setOwner(Player)}
	 * @param _owner The new owner.
	 */
	public void setOwner(String _owner)
	{
		owner = _owner;
	}

	/**
	 * Checks if this portal has an owner.
	 */
	public boolean hasOwner()
	{
		return owner != null && !owner.isEmpty();
	}

	/**
	 * Checks whether or not we have an UUID stored for the owner
	 */
	public boolean hasOwnerId() {
		return hasOwner() && owner.split(",").length > 1;
	}

	/**
	 * Get the UUID of the owner of the portal
	 * @return The UUID of the owner
	 */
	public UUID getOwnerId() {
		if (!hasOwnerId()) {
			return null;
		}
		try {
			return UUID.fromString(owner.split(",")[1]);
		} catch (IllegalArgumentException e) {
			return null;
		}
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
		return destination != null && !destination.isEmpty();
	}

	/**
	 * Checks to see if this point has a name.
	 * @return true if this point has a name; false otherwise.
	 */
	public boolean hasName()
	{
		return name != null && !name.isEmpty();
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
	 * @param time the new time this was used.
	 */
	public void setLastUsed(long time)
	{
		lastused = time;
	}

	/**
	 * Get the position of the door.
	 * @return The position of the door.
	 */
	public int getDoorPosition()
	{
		return doorpos;
	}

	/**
	 * Set the position of the door
	 * @param dp The position of the door
	 */
	public void setDoorPosition(int dp)
	{
		doorpos = dp;
	}

	/**
	 * Set whether the warp's name is hidden.
	 * @param dp whether to show the warp.
	 */

	public void setHidden(boolean dp)
	{
		hidden = dp;
	}

	/**
	 * Figure out whether this warp is hidden.
	 * @return true to suppress name, false otherwise.
	 */
	public boolean isHidden()
	{
		return hidden;
	}

	/**
	 * What world is this in?
	 * @return The world that this location is in.
	 */
	public String getWorld()
	{
		return world;
	}

	/**
	 * Set the world that a portal is in.
	 * @param w The world that this portal is in.
	 */
	public void setWorld(String w)
	{
		world = w;
	}

	/**
	 * Returns a string that can uniquely identify this portal amongst other portals. 
	 * Contains the world, and its location in space.
	 * @return A string that can be used to uniquely identify this portal.
	 */
	public String getIdentifierString() {
		return world + "," + x + "," + y + "," + z;
	}

	/**
	 * Check if this portal is usable. (Time limit)
	 * @param cooldown How long this takes to cool down.
	 * @return true if the portal can be used; false if it cannot.
	 */
	public boolean isUsable(int cooldown)
	{
		return (lastused+cooldown < System.currentTimeMillis());
	}

	/**
	 * Checks whether or not someone can see this portal's information.
	 * @param sender The sender
	 * @return true if the sender has access to the portal (like the owner); false if not
	 */
	public boolean canSee(CommandSender sender)
	{
		return !hidden || canAccess(sender);
	}

	/**
	 * Checks whether or not someone has administrative access to the portal
	 * @param sender The sender
	 * @return true if the sender has access to the portal (like the owner); false if not
	 */
	public boolean canAccess(CommandSender sender)
	{
		return !hasOwner()
				|| !(sender instanceof Player)
				|| isOwner((Player) sender);
	}

	public static WarpLocation deserialize(Map<?, ?> map)
	{
		WarpLocation portal = new WarpLocation(
				(Integer) map.get("x"),
				(Integer) map.get("y"),
				(Integer) map.get("z"),
				(String) map.get("world")
		);
		if (map.containsKey("name"))
			portal.setName((String) map.get("name"));
		if (map.containsKey("owner"))
			portal.setOwner((String) map.get("owner"));
		if (map.containsKey("destination"))
			portal.setDestination((String) map.get("destination"));
		if (map.containsKey("hidden"))
			portal.setHidden((Boolean) map.get("hidden"));
		if (map.containsKey("direction"))
			portal.setDoorPosition((Integer) map.get("direction"));
		return portal;
	}

	public Map<String, Object> serialize()
	{
		Map<String, Object> map  = new LinkedHashMap<>();
		map.put("x", getX());
		map.put("y", getY());
		map.put("z", getZ());
		map.put("world", getWorld());
		if (hasName())
			map.put("name", getName());
		if (hasOwner())
			map.put("owner", getOwner());
		if (hasDestination())
			map.put("destination", getDestination());
		if (isHidden())
			map.put("hidden", isHidden());
		if (getDoorPosition() != 0)
			map.put("direction", getDoorPosition());
		return map;
	}

	@Override
	public String toString() {
		return "WarpLocation{" +
				"name=" + getName() + "," +
				"world=" + getWorld() + "," +
				"x=" + getX() + "," +
				"y=" + getY() + "," +
				"z=" + getZ() + "," +
				"destination=" + getDestination() + "," +
				"owner=" + getOwner() + "," +
				"hidden=" + isHidden() + "," +
				"direction=" + getDoorPosition();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}