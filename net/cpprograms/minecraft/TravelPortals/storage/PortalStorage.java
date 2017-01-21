package net.cpprograms.minecraft.TravelPortals.storage;

import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.util.List;

public interface PortalStorage {

    /**
     * Load the data from the storage
     * @return true if it succeeded; false if it failed
     */
    boolean load();

    /**
     * Save the data to the storage
     * @return true if it succeeded; false if it failed
     */
    boolean save();

    /**
     * Saves all portals to disk.
     * @param backup Whether to save to a backup file or not.
     * @return true if it succeeded; false if it failed
     */
    boolean save(boolean backup);

    /**
     * This gets all portals
     * @return A list with all portals TODO: This should return a map
     */
    List<WarpLocation> getPortals();

    /**
     * Add a new portal
     * @param location The info of the portal
     */
    void addPortal(WarpLocation location);

    /**
     * Get portal by it's name
     * @param name The name of the portal
     * @return The portal info or null if none was found
     */
    WarpLocation getPortal(String name);

    /**
     * Get a nearby portal
     * @param location
     * @return
     */
    WarpLocation getPortal(Location location);

    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    void removePortal(WarpLocation portal);

    /**
     * Rename a world for all existing portals.
     * @param oldWorld The name of the old world.
     * @param newWorld The name of the new world.
     */
    void renameWorld(String oldWorld, String newWorld);

    /**
     * Delete all portals linking to a world.
     * @param oldWorld The world to delete all portals to.
     */
    void deleteWorld(String oldWorld);

    /**
     * This deletes all portals from the cache
     */
    void clearCache();

}
