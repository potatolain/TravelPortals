package net.cpprograms.minecraft.TravelPortals.storage;

import net.cpprograms.minecraft.TravelPortals.TravelPortals;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;

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
import java.util.List;

public class LegacyStorage extends PortalStorage {

    public LegacyStorage(TravelPortals plugin)
    {
        super(plugin);
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

        List<WarpLocation> warpLocations = new ArrayList<>();

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

                    plugin.getPortalStorage().save();

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

        for (WarpLocation portal : warpLocations) {
            addPortal(portal);
        }
        return true;
    }

    @Override
    public boolean save(WarpLocation portal) {
        return save();
    }

    @Override
    public boolean save(String worldName) {
        return save();
    }

    @Override
    public boolean save()
    {
        return save(false);
    }

    @Override
    public StorageType getType() {
        return StorageType.LEGACY;
    }

    private boolean save(boolean backup)
    {
        boolean error = false;
        try
        {
            List<WarpLocation> warpLocations = new ArrayList<>(getPortals().values());

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
