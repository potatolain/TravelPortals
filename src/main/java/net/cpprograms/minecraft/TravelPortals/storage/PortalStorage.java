package net.cpprograms.minecraft.TravelPortals.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class PortalStorage {

    private Map<String, WarpLocation> portals = new LinkedHashMap<>();
    private Cache<String, String> locationCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build();

    /**
     * Load the data from the storage
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean load();

    /**
     * Save data of a portal to the storage
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean save(WarpLocation portal);

    /**
     * Save data of a all portals in a world
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean save(String worldName);

    /**
     * Save all data to the storage
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean save();

    /**
     * Get the type of this storage
     * @return The type of this storage
     */
    public abstract StorageType getType();

    /**
     * This gets all portals
     * @return A list with all portals
     */
    public Map<String, WarpLocation> getPortals() {
        return portals;
    }

    /**
     * Add a new portal
     * @param portal The info of the portal
     */
    public void addPortal(WarpLocation portal) {
        portals.put(portal.getName().toLowerCase(), portal);
    }

    /**
     * Get portal by it's name
     * @param name The name of the portal
     * @return The portal info or null if none was found
     */
    public WarpLocation getPortal(String name) {
        return portals.get(name.toLowerCase());
    }

    /**
     * Get a nearby portal
     * @param location
     * @return
     */
    public WarpLocation getPortal(Location location) {
        String locStr = location.getWorld() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        String portalName = locationCache.getIfPresent(locStr);
        if (portalName != null && !portals.containsKey(portalName)) {
            locationCache.invalidate(locStr);
            portalName = null;
        }
        if (portalName == null) {
            for (WarpLocation p : getPortals().values()) {
                if (p.getWorld().isEmpty() || location.getWorld().getName().equals(p.getWorld())) {
                    if (Math.abs(p.getX() - location.getBlockX()) <= 1 && Math.abs(p.getZ() - location.getBlockZ()) <= 1) {
                        locationCache.put(locStr, p.getName());
                        return p;
                    }
                }
            }
            locationCache.put(locStr, "");
        }
        return null;
    }

    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    public void namePortal(WarpLocation portal, String name) {
        portals.remove(portal.getName().toLowerCase());
        portal.setName(name);
        addPortal(portal);
        save(portal);
    }

    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    public void removePortal(WarpLocation portal) {
        portals.remove(portal.getName().toLowerCase());
        locationCache.invalidate(portal.getWorld() + "," + portal.getX() + "," + portal.getY() + "," + portal.getZ());
    }

    /**
     * Rename a world for all existing portals.
     * @param oldWorld The name of the old world.
     * @param newWorld The name of the new world.
     * @return true if it succeeded; false if it failed
     */
    public boolean renameWorld(String oldWorld, String newWorld) {
        for (WarpLocation portal : portals.values()) {
            if (oldWorld.equalsIgnoreCase(portal.getWorld())) {
                portal.setWorld(newWorld);
            }
        }
        return save(newWorld);
    }

    /**
     * Delete all portals linking to a world.
     * @param world The world to delete all portals to.
     * @return true if it succeeded; false if it failed
     */
    public boolean deleteWorld(String world) {
        for (WarpLocation portal : portals.values()) {
            if (world.equalsIgnoreCase(portal.getWorld())) {
                removePortal(portal);
            }
        }
        return save(world);
    }

    /**
     * This deletes all portals from the cache
     */
    public void clearCache() {
        portals.clear();
        locationCache.invalidateAll();
    }
}
