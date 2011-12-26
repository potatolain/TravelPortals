package net.cpprograms.minecraft.TravelPortals;

import net.cpprograms.minecraft.General.CommandHandler;
import net.cpprograms.minecraft.General.PermissionsHandler;
import net.cpprograms.minecraft.General.PluginBase;
import java.io.*;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

/**
 * TravelPortals Bukkit port.
 *
 * @author cppchriscpp
 */
public class TravelPortals extends PluginBase {
	/**
	 * A Player listener
	 */
    private final TravelPortalsPlayerListener playerListener = new TravelPortalsPlayerListener(this);

    /**
     * A Block listener.
     */
    private final TravelPortalsBlockListener blockListener = new TravelPortalsBlockListener(this);

	/**
	 * All user warp points
	 */
	public ArrayList<WarpLocation> warpLocations = new ArrayList<WarpLocation>();

	/**
	 * The type of block portals must be made from. (Default is 49 (Obsidian))
	 */
	protected int blocktype = 49;

	/**
	 * A friendly name for the construction block type.
	 */
	protected String strBlocktype = "obsidian";

	/**
	 * Whether to automatically export the list of portals to travelportals.txt.
	 */
	protected boolean autoExport = false;

	/**
	 * Server access!
	 */
	public static Server server;

	/**
	 * The numerical type of block used inside the portal. This would be 90, but portal blocks stink.
	 */
	protected int portaltype = 8;

	/**
	 * The type of block used for the doorway
	 */
	protected int doortype = 64;

	/**
	 * The second type of block to be used for the doorway
	 */
	protected int doortype2 = 71;

	/**
	 *  A friendly name for the door block type.
	 */
	protected String strDoortype = "door";

	/**
	 * A friendly name for the type of block used for the torch.
	 */
	protected String strTorchtype = "redstone torch";

	/**
	 * The type of block used for the torch at the bottom.
	 */
	protected int torchtype = 76;

	/**
	 * Do we want to use the permissions plugin?
	 */
	protected boolean usepermissions = false;

	/**
	 * How long until a portal reactivates?
	 */
	protected int cooldown = 5000;

	/**
	 * How many backup saves should there be? (3)
	 */
	protected int numsaves = 3;


	/**
	 * Called upon enabling the plugin
	 */
    @SuppressWarnings({ "unchecked", "deprecation" })
	public void onEnable() {

		server = getServer();

		// Read in the YAML config stuff
		try
		{
			FileConfiguration conf = getConfig();
			if (conf.contains("frame"))
				blocktype = conf.getInt("frame");
			if (conf.contains("framename"))
				strBlocktype = conf.getString("framename");
			if (conf.contains("fill"))
				portaltype = conf.getInt("fill");
			if (conf.contains("door"))
				doortype = conf.getInt("door");
			if (conf.contains("door2"))
			    doortype2 = conf.getInt("door2");
			if (conf.contains("doorname"))
				strDoortype = conf.getString("doorname");
			if (conf.contains("torch"))
				torchtype = conf.getInt("torch");
			if (conf.contains("torchname"))
				strTorchtype = conf.getString("torchname");
			if (conf.contains("permissions"))
				usepermissions = conf.getBoolean("permissions");
            if (conf.contains("autoexport"))
                autoExport = conf.getBoolean("autoexport");
            if (conf.contains("cooldown"))
                cooldown = 1000 * conf.getInt("cooldown");
            if (conf.contains("numsaves"))
                numsaves = conf.getInt("numsaves");


		}
		catch (java.lang.NumberFormatException i)
		{
			logSevere("An exception occurred when trying to read your config file.");
			logSevere("Check your config.yml!");
			return;
		}

		if (!this.getDataFolder().exists())
		{
			logSevere("Could not read plugin's data folder! Please put the TravelPortals folder in the plugins folder with the plugin!");
			logSevere("Aborting plugin load");
			return;
		}
		
        try
        {

            // Save backup directory?
            if (!(new File(this.getDataFolder() + "/backups")).exists())
                (new File(this.getDataFolder() + "/backups")).mkdir();

    	    // Move the save file to where it belongs.
    	    // if it's done the old way.
            if (!(new File(this.getDataFolder(), "TravelPortals.ser").exists()))
            {
                // Moving time. Otherwise we just need a new file.
                if ((new File("TravelPortals.ser")).exists())
                {
                    (new File("TravelPortals.ser")).renameTo(new File(this.getDataFolder(), "TravelPortals.ser"));
                }
            }
        }
        catch (SecurityException i)
        {
            logSevere("Could not read/write TravelPortals data folder! Aborting.");
            return;
        }



        // Attempt to read in the current version's save data.
		try
		{
			FileInputStream fIn = new FileInputStream(new File(this.getDataFolder(), "TravelPortals.ser"));
			ObjectInputStream oIn = new ObjectInputStream(fIn);
			warpLocations = (ArrayList<net.cpprograms.minecraft.TravelPortals.WarpLocation>)oIn.readObject();
			oIn.close();
			fIn.close();

			doBackup();
		}
        catch (IOException i)
        {
	    	logWarning("Could not load TravelPortals location file!");
	    	logWarning("If this is your first time running the plugin, you can ignore this message.");
	    	logWarning("If this is not your first run, STOP YOUR SERVER NOW! You could lose your portals!");
	    	logWarning("The file plugins/TravelPortals/TravelPortals.ser is missing or unreadable.");
	    	logWarning("Please check that this file exists and is in the right directory.");
	    	logWarning("If it is not, there should be a backup in the plugins/TravelPortals/backups folder.");
	    	logWarning("Copy this, place it in the plugins/TravelPortals folder, and rename it to TravelPortals.ser and restart the server.");
	    	logWarning("If this does not fix the problem, or if something strange went wrong, please report this issue.");
        }
        catch (java.lang.ClassNotFoundException i) 
        {
        	logSevere("TravelPortals: Something has gone very wrong. Please contact owner@cpprograms.net!");
        	return;
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
        		logInfo("Importing old pre-2.0 portals...");
				try 
				{
					warpLocations = new ArrayList<net.cpprograms.minecraft.TravelPortals.WarpLocation>();
		        	FileInputStream fIn = new FileInputStream(new File(this.getDataFolder(), "TravelPortals.ser"));
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
					
					this.savedata();
					
					this.doBackup();
					logInfo("Imported old portals sucecessfully!");
				} 
				catch (IOException e) {
					logWarning("Importing old portals failed.");
					return;
				}
				catch (ClassNotFoundException e) 
				{
					logSevere("Something has gone horribly wrong. Contact owner@cpprograms.net!");
					return;
				}
        	}
        }

        // Register our events
        PluginManager pm = getServer().getPluginManager();
		// pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		// pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.High, this);
        
        super.onEnable();
        // Override PermissionsHandler with our variable. Have to do this after the parent method
        permissions = new PermissionsHandler(usepermissions);
        commandHandler = new CommandHandler(this, PortalCommandSet.class);
    }

    @Override
    /*public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
        	// Very ugly. Some day this needs to be fixed.
            String[] aags = new String[args.length + 1];
            aags[0] = "/" + label;
            for (int i = 0; i < args.length; i++)
                aags[i+1] = args[i];
            return playerListener.onPlayerCommand((Player)sender, aags);
        } else {
        	// FIXME: Code from the 2.2-pre1 patch release! Very ugly!
        	// Also, move this code into its own real method. Probably a good time to move the stuff from the player
        	// listener.. perhaps make a new class for this to keep TravelPortals.java clean.
        	if (label.equals("portal"))
        	{
        		if (args[0].equals("renameworld") && args.length == 3) 
        		{
        			this.renameWorld(args[1], args[2]);
        			this.logInfo("Renamed world " + args[1] + " to " + args[2]);
        		}
        		else if (args[0].equals("fixblankworld") && args.length == 2)
        		{
        			this.renameWorld("", args[1]);
        			this.logInfo("Corrected all unnamed worlds to " + args[1]);
        		}
        		else 
        		{
        			this.logInfo("Unrecognized command");
        		}
        		return true;
        	}
        }
        return false;
    }*/

    /**
     * Called upon disabling the plugin.
     */
    public void onDisable() {
		savedata();
		super.onDisable();
    }

    /**
     * Saves all door warp stuff to disk.
     */
    public void savedata()
    {
        savedata(false);
    }

    /**
     * Saves all portals to disk.
     * @param backup Whether to save to a backup file or not.
     */
    public void savedata(boolean backup)
    {
        try
        {
            String name = this.getDataFolder() + "/TravelPortals.ser";
            if (backup)
            {
                name = this.getDataFolder() + "/backups/TravelPortals--";
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd--kk_mm");
                name = name + df.format(Calendar.getInstance().getTime());

            }
            FileOutputStream fOut = new FileOutputStream(name);
            ObjectOutputStream oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(warpLocations);
            oOut.close();
            fOut.close();
        }
        catch (IOException i)
        {
            logWarning("Could not save TravelPortals data!");
        }

        if (autoExport)
            dumpPortalList();
        if (!backup)
            doBackup();
    }

    /**
     * Makes a backup of the TravelPortals file in case something goes wrong.
     * - This is for the paranoid, though there are a few isolated cases of files disappearing.
     *   I can't find a source, so this is at least a quick fix.
     */
    public void doBackup()
    {
        try
        {
            File dir = new File(this.getDataFolder() + "/backups");
            if (!dir.isDirectory())
            {
                logSevere("Cannot save backups!");
            }
            String[] list = dir.list();
            if (numsaves > 0 && list.length+1 > numsaves)
            {
                java.util.Arrays.sort(list);
                for (int i = 0; i < list.length+1-numsaves; i++)
                    (new File(this.getDataFolder() + "/backups", list[i])).delete();
            }

        }
        catch (SecurityException i)
        {
            logWarning("Saving a backup of the TravelPortals file failed.");
        }
        savedata(true);
    }

   	/**
     * Quick helper function to add warp points.
     * @param w The warp point to add to warpLocations.
     */
    public void addWarp(WarpLocation w)
    {
        warpLocations.add(w);
    }

    /**
     * Get the index of a warp object from its name.
     * @param name The name of the portal to find.
     * @return The index of the portal in plugin.warpLocations, or -1 if it is not found.
     */
    public int getWarp(String name)
    {
        // Test all warps to see if they have the name we're looking for.
        for (int i = 0; i < this.warpLocations.size(); i++)
        {
            if (this.warpLocations.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Find a warp point from a relative location. (Within 1 block)
     * @param worldname The world name to get the warp from.
     * @param x X coordinate to search near.
     * @param y Y coordinate to search near.
     * @param z Z coordinate to search near.
     * @return The index of a nearby portal in plugin.warpLocations, or -1 if it is not found.
     */
    public int getWarpFromLocation(String worldname, int x, int y, int z)
    {
        // Iterate through all warps and check how close they are
        for (int i = 0; i < this.warpLocations.size(); i++)
        {
            WarpLocation wd = this.warpLocations.get(i);
            if (wd.getY() == y && wd.getWorld().equals(worldname))
            {
                // We found one!!
                if (Math.abs(wd.getX() - x) <= 1 && Math.abs(wd.getZ() - z) <= 1)
                    return i;
            }
        }
        return -1;
    }
    
    /**
     * This is a function to rename a world for all existing portals. 
     * @param oldworld The name of the old world.
     * @param newworld The name of the new world.
     */
    public void renameWorld(String oldworld, String newworld)
    {
    	for (int i = 0; i < this.warpLocations.size(); i++) 
    	{
    		if (this.warpLocations.get(i).getWorld().equals(oldworld))
    			this.warpLocations.get(i).setWorld(newworld);
    	}
    }

    /**
     * Dump the portal list to a text file for parsing.
     */
    public void dumpPortalList()
    {
        try
        {
            FileOutputStream fOut = new FileOutputStream(new File(this.getDataFolder(), "travelportals.txt"));
            PrintStream pOut = new PrintStream(fOut);
            for (WarpLocation w : this.warpLocations)
                pOut.println(w.getX() + "," + w.getY() + "," + w.getZ() + "," + w.getName() + "," + w.getDestination() + "," + w.getHidden() +"," + w.getWorld() + "," + w.getOwner());

            pOut.close();
            fOut.close();
        }
        catch (Exception e)
        {
        }
    }
}

