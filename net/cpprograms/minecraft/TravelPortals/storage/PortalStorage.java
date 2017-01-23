package net.cpprograms.minecraft.TravelPortals.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class PortalStorage {

    private Map<String, WarpLocation> portals = new LinkedHashMap<>();
    private Cache<String, WarpLocation> locationCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .weakValues()
            .build();

    /**
     * Load the data from the storage
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean load();

    /**
     * Save the data to the storage
     * @return true if it succeeded; false if it failed
     */
    public abstract boolean save();

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
        WarpLocation portal = locationCache.getIfPresent(locStr);
        if (portal != null && !portals.containsValue(portal)) {
            locationCache.invalidate(locStr);
            portal = null;
        }
        if (portal == null) {
            for (WarpLocation p : getPortals().values()) {
                if (p.getWorld().isEmpty() || location.getWorld().getName().equals(p.getWorld())) {
                    if (Math.abs(p.getX() - location.getBlockX()) <= 1 && Math.abs(p.getZ() - location.getBlockZ()) <= 1) {
                        portal = p;
                        locationCache.put(locStr, portal);
                        break;
                    }
                }
            }
        }
        return portal;
    }

    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    public void namePortal(WarpLocation portal, String name) {
        portals.remove(portal.getName().toLowerCase());
        portal.setName(name);
        addPortal(portal);
        save();
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
     */
    public void renameWorld(String oldWorld, String newWorld) {
        for (WarpLocation portal : portals.values()) {
            if (oldWorld.equalsIgnoreCase(portal.getWorld())) {
                portal.setWorld(newWorld);
            }
        }
    }

    /**
     * Delete all portals linking to a world.
     * @param oldWorld The world to delete all portals to.
     */
    public void deleteWorld(String oldWorld) {
        Iterator<WarpLocation> portalIt = portals.values().iterator();
        while (portalIt.hasNext()) {
            if (oldWorld.equalsIgnoreCase(portalIt.next().getWorld())) {
                portalIt.remove();
            }
        }
    }

    /**
     * This deletes all portals from the cache
     */
    public void clearCache() {
        portals.clear();
        locationCache.invalidateAll();
    }

}
