package net.cpprograms.minecraft.TravelPortals.storage;

import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class PortalStorage {

    private Map<String, WarpLocation> portals = new LinkedHashMap<>();
    private Map<String, String> portalNames = new LinkedHashMap<>();
    private Map<String, String> portalLocations = new LinkedHashMap<>();

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
        portals.put(portal.getIdentifierString(), portal);
        if (portal.hasName()) {
            portalNames.put(portal.getName(), portal.getIdentifierString());
        }
        portalLocations.put(portal.getWorld() + "," + portal.getX() + "," + portal.getY() + "," + portal.getZ(), portal.getIdentifierString());
        portalLocations.put(portal.getWorld() + "," + portal.getX() + "," + portal.getY() + 1 + "," + portal.getZ(), portal.getIdentifierString());
    }

    /**
     * Get portal by it's name
     * @param name The name of the portal
     * @return The portal info or null if none was found
     */
    public WarpLocation getPortal(String name) {
        String identifier = portalNames.get(name);
        if (identifier == null) {
            return null;
        }
        return portals.get(identifier);
    }

    /**
     * Get a portal at that location
     * @param location  The location to get a portal at
     * @return          The Portal or null if none was found
     */
    public WarpLocation getPortal(Location location) {
        String portalIdentifier = portalLocations.get(location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        if (portalIdentifier == null) {
            portalIdentifier = portalLocations.get("," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        }
        if (portalIdentifier != null) {
            if (portals.containsKey(portalIdentifier)) {
                return portals.get(portalIdentifier);
            } else {
                portalLocations.remove(portalIdentifier);
            }
        }
        return null;
    }

    /**
     * Get the closest nearby portal
     * @param location  The location to get a portal at
     * @param radius    The radius in blocks to search in
     * @return          The Portal or null if none was found
     */
    public WarpLocation getNearbyPortal(Location location, int radius) {
        WarpLocation exact = getPortal(location);
        if (exact != null) {
            return exact;
        }

        Set<WarpLocation> nearbyPortals = new HashSet<>();
        Location loopLocation = location.clone();
        loopLocation.subtract(radius, radius, radius);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    WarpLocation portal = getPortal(loopLocation);
                    if (portal != null && !nearbyPortals.contains(portal)) {
                        nearbyPortals.add(portal);
                    }
                    loopLocation.add(0, 1, 0);
                }
                loopLocation.add(0, 0, 1);
            }
            loopLocation.add(1, 0, 0);
        }

        if (nearbyPortals.size() == 1) {
            return nearbyPortals.iterator().next();
        } else if (!nearbyPortals.isEmpty()){
            WarpLocation closest = null;
            int closestDist = -1;
            for (WarpLocation portal : nearbyPortals) {
                int distance = Math.abs((location.getBlockX() - portal.getX()) * (location.getBlockY() - portal.getY()) * (location.getBlockZ() - portal.getZ()));
                if (closest == null || closestDist > distance) {
                    closest = portal;
                    closestDist = distance;
                }
            }
            return closest;
        }
        return null;
    }


    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    public void namePortal(WarpLocation portal, String name) {
        removePortal(portal);
        portal.setName(name);
        addPortal(portal);
        save(portal);
    }

    /**
     * Remove a portal
     * @param portal The portal to remove
     */
    public void removePortal(WarpLocation portal) {
        portals.remove(portal.getIdentifierString());
        portalNames.remove(portal.getName().toLowerCase());
        portalLocations.remove(portal.getWorld() + "," + portal.getX() + "," + portal.getY() + "," + portal.getZ());
        portalLocations.remove(portal.getWorld() + "," + portal.getX() + "," + portal.getY() + 1 + "," + portal.getZ());
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
        portalLocations.clear();
    }
}
