package net.cpprograms.minecraft.TravelPortals.storage;

import net.cpprograms.minecraft.TravelPortals.TravelPortals;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class LegacyStorage implements PortalStorage {

    private final TravelPortals plugin;

    /**
     * All user warp points TODO: Use a name -> portal map
     */
    public ArrayList<WarpLocation> warpLocations = new ArrayList<WarpLocation>();

    public LegacyStorage(TravelPortals plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean load()
    {
        if (!plugin.getDataFolder().exists())
        {
            plugin.logSevere("Could not read plugin's data folder! Please put the TravelPortals folder in the plugins folder with the plugin!");
            return false;
        }

        try
        {

            // Save backup directory?
            if (!(new File(plugin.getDataFolder(), "backups")).exists())
                (new File(plugin.getDataFolder(), "backups")).mkdir();

            // Move the save file to where it belongs.
            // if it's done the old way.
            if (!(new File(plugin.getDataFolder(), "TravelPortals.ser").exists()))
            {
                // Moving time. Otherwise we just need a new file.
                if ((new File("TravelPortals.ser")).exists())
                {
                    (new File("TravelPortals.ser")).renameTo(new File(plugin.getDataFolder(), "TravelPortals.ser"));
                }
            }
        }
        catch (SecurityException i)
        {
            plugin.logSevere("Could not read/write TravelPortals data folder! Aborting.");
            return false;
        }



        // Attempt to read in the current version's save data.
        try
        {
            FileInputStream fIn = new FileInputStream(new File(plugin.getDataFolder(), "TravelPortals.ser"));
            ObjectInputStream oIn = new ObjectInputStream(fIn);
            warpLocations = (ArrayList<WarpLocation>)oIn.readObject();
            oIn.close();
            fIn.close();

            doBackup();
        }
        catch (IOException i)
        {
            plugin.logWarning("Could not load TravelPortals location file!");
            plugin.logWarning("If this is your first time running the plugin, you can ignore this message.");
            plugin.logWarning("If this is not your first run, STOP YOUR SERVER NOW! You could lose your portals!");
            plugin.logWarning("The file plugins/TravelPortals/TravelPortals.ser is missing or unreadable.");
            plugin.logWarning("Please check that this file exists and is in the right directory.");
            plugin.logWarning("If it is not, there should be a backup in the plugins/TravelPortals/backups folder.");
            plugin.logWarning("Copy this, place it in the plugins/TravelPortals folder, and rename it to TravelPortals.ser and restart the server.");
            plugin.logWarning("If this does not fix the problem, or if something strange went wrong, please report this issue.");
        }
        catch (java.lang.ClassNotFoundException i)
        {
            plugin.logSevere("TravelPortals: Something has gone very wrong. Please contact admin@cpprograms.net!");
            return false;
        }

        // Test to see if we need to do conversion.
        try
        {
            if (!warpLocations.isEmpty()) {
                WarpLocation w = warpLocations.get(0);
                w.getName();
            }
        }
        catch (java.lang.ClassCastException i)
        {
            {
                plugin.logInfo("Importing old pre-2.0 portals...");
                try
                {
                    warpLocations = new ArrayList<net.cpprograms.minecraft.TravelPortals.WarpLocation>();
                    FileInputStream fIn = new FileInputStream(new File(plugin.getDataFolder(), "TravelPortals.ser"));
                    ObjectInputStream oIn = new ObjectInputStream(fIn);
                    ArrayList<com.bukkit.cppchriscpp.TravelPortals.WarpLocation> oldloc = (ArrayList<com.bukkit.cppchriscpp.TravelPortals.WarpLocation>)oIn.readObject();
                    for (com.bukkit.cppchriscpp.TravelPortals.WarpLocation wl : oldloc)
                    {
                        // Yes, this blows.
                        net.cpprograms.minecraft.TravelPortals.WarpLocation temp = new WarpLocation(wl.getX(), wl.getY(), wl.getZ(), wl.getDoorPosition(), wl.getWorld(), wl.getOwner());
                        temp.setName(wl.getName());
                        temp.setDestination(wl.getDestination());
                        temp.setHidden(wl.getHidden());
                        warpLocations.add(temp);
                    }
                    oIn.close();
                    fIn.close();

                    plugin.savedata();

                    doBackup();
                    plugin.logInfo("Imported old portals sucecessfully!");
                }
                catch (IOException e) {
                    plugin.logWarning("Importing old portals failed.");
                    return false;
                }
                catch (ClassNotFoundException e)
                {
                    plugin.logSevere("Something has gone horribly wrong. Contact owner@cpprograms.net!");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean save()
    {
        return save(false);
    }

    @Override
    public boolean save(boolean backup)
    {
        boolean error = false;
        try
        {
            File file = new File(plugin.getDataFolder(), "TravelPortals.ser");
            if (backup)
            {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd--kk_mm");
                file = new File(plugin.getDataFolder(), "backups/TravelPortals--" + df.format(Calendar.getInstance().getTime()));
            }
            FileOutputStream fOut = new FileOutputStream(file);
            ObjectOutputStream oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(warpLocations);
            oOut.close();
            fOut.close();
        }
        catch (IOException i)
        {
            plugin.logWarning("Could not save TravelPortals data!");
            error = true;
        }

        if (plugin.isAutoExport())
            plugin.dumpPortalList();
        if (!backup)
            doBackup();

        return !error;
    }

    @Override
    public List<WarpLocation> getPortals()
    {
        return warpLocations;
    }

    @Override
    public void addPortal(WarpLocation location)
    {
        warpLocations.add(location);
    }

    @Override
    public WarpLocation getPortal(String name)
    {
        // Test all warps to see if they have the name we're looking for.
        for (WarpLocation warpLocation : warpLocations)
            if (warpLocation.getName().equalsIgnoreCase(name))
                return warpLocation;
        // No portal with that name found
        return null;
    }

    @Override
    public WarpLocation getPortal(Location location)
    {
        // Iterate through all warps and check how close they are
        for (WarpLocation warpLocation : warpLocations)
            if (warpLocation.getY() == location.getBlockY() && warpLocation.getWorld().equals(location.getWorld().getName()))
                if (Math.abs(warpLocation.getX() - location.getBlockX()) <= 1 && Math.abs(warpLocation.getZ() - location.getBlockZ()) <= 1)
                    return warpLocation;
        // No portal with that name found
        return null;
    }

    @Override
    public void removePortal(WarpLocation portal) {
        warpLocations.remove(portal);
    }

    @Override
    public void renameWorld(String oldWorld, String newWorld)
    {
        for (WarpLocation warpLocation : warpLocations)
            if (warpLocation.getWorld().equalsIgnoreCase(oldWorld))
                warpLocation.setWorld(newWorld);
    }

    @Override
    public void deleteWorld(String oldWorld)
    {
        Iterator<WarpLocation> locIt = warpLocations.iterator();
        while (locIt.hasNext())
            if (locIt.next().getWorld().equalsIgnoreCase(oldWorld))
                locIt.remove();
    }

    @Override
    public void clearCache() {
        warpLocations = new ArrayList<WarpLocation>();
    }

    /**
     * Makes a backup of the TravelPortals file in case something goes wrong.
     * - This is for the paranoid, though there are a few isolated cases of files disappearing.
     *   I can't find a source, so this is at least a quick fix.
     * @return true if it succeeded; false if it failed
     */
    private boolean doBackup()
    {
        boolean error = false;
        try
        {
            File dir = new File(plugin.getDataFolder(), "backups");
            if (!dir.isDirectory())
            {
                plugin.logSevere("Cannot save backups!");
                return false;
            }
            String[] list = dir.list();
            if (plugin.getNumSaves() > 0 && list.length+1 > plugin.getNumSaves())
            {
                java.util.Arrays.sort(list);
                for (int i = 0; i < list.length + 1 - plugin.getNumSaves(); i++)
                    (new File(dir, list[i])).delete();
            }

        }
        catch (SecurityException i)
        {
            plugin.logWarning("Saving a backup of the TravelPortals file failed.");
            error = true;
        }
        return save(true) && !error;
    }
}
