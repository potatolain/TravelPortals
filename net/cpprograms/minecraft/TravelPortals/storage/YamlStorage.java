package net.cpprograms.minecraft.TravelPortals.storage;

import net.cpprograms.minecraft.TravelPortals.TravelPortals;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.util.List;

public class YamlStorage implements PortalStorage {

    private final TravelPortals plugin;

    public YamlStorage(TravelPortals plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean load()
    {
        return false;
    }

    @Override
    public boolean save()
    {
        return false;
    }

    @Override
    public boolean save(boolean backup)
    {
        return false;
    }

    @Override
    public List<WarpLocation> getPortals()
    {
        return null;
    }

    @Override
    public void addPortal(WarpLocation location)
    {

    }

    @Override
    public WarpLocation getPortal(String name)
    {
        return null;
    }

    @Override
    public WarpLocation getPortal(Location location)
    {
        return null;
    }

    @Override
    public void removePortal(WarpLocation portal)
    {

    }

    @Override
    public void renameWorld(String oldWorld, String newWorld)
    {

    }

    @Override
    public void deleteWorld(String oldWorld)
    {

    }

    @Override
    public void clearCache()
    {

    }
}
