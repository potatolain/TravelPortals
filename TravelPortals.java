package com.bukkit.cppchriscpp.TravelPortals;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.*;

/**
 * TravelPortals Bukkit port.
 *
 * @author cppchriscpp
 */
public class TravelPortals extends JavaPlugin {
	/**
	 * A Player listener
	 */
    private final TravelPortalsPlayerListener playerListener = new TravelPortalsPlayerListener(this);

    /**
     * A Block listener.
     */
    private final TravelPortalsBlockListener blockListener = new TravelPortalsBlockListener(this);

    /**
     * Something related to debugging
     */
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	/*
	 * All user warp points
	 */
	public ArrayList<WarpLocation> warpLocations = new ArrayList<WarpLocation>();

	/**
	 * YAML config reader
	 */
	private final Yaml yaml = new Yaml(new SafeConstructor());

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
	 * Permissions handler
	 */
	public static PermissionHandler Permissions = null;


	/**
	 * Called upon enabling the plugin
	 */
    public void onEnable() {

		server = getServer();
        // Register our events
        PluginManager pm = getServer().getPluginManager();
		// pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		// pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.High, this);

		// Read in the YAML config stuff
		try
		{
			FileInputStream fIn = new FileInputStream(new File(this.getDataFolder(), "config.yml"));
			Map<String, Object> data = (Map<String, Object>)yaml.load(new UnicodeReader(fIn));
			if (data.containsKey("frame"))
				blocktype = Integer.parseInt(data.get("frame").toString());
			if (data.containsKey("framename"))
				strBlocktype = (String)data.get("framename");
			if (data.containsKey("fill"))
				portaltype = Integer.parseInt(data.get("fill").toString());
			if (data.containsKey("door"))
				doortype = Integer.parseInt(data.get("door").toString());
			if (data.containsKey("door2"))
			    doortype2 = Integer.parseInt(data.get("door2").toString());
			if (data.containsKey("doorname"))
				strDoortype = data.get("doorname").toString();
			if (data.containsKey("torch"))
				torchtype = Integer.parseInt(data.get("torch").toString());
			if (data.containsKey("torchname"))
				strTorchtype = data.get("torchname").toString();
			if (data.containsKey("permissions"))
				usepermissions = Boolean.parseBoolean(data.get("permissions").toString());
            if (data.containsKey("autoexport"))
                autoExport = Boolean.parseBoolean(data.get("autoexport").toString());
            if (data.containsKey("cooldown"))
                cooldown = 1000 * Integer.parseInt(data.get("cooldown").toString());
            if (data.containsKey("numsaves"))
                numsaves = Integer.parseInt(data.get("numsaves").toString());


		}
		catch (IOException i)
		{
			System.out.println("Could not load the configuration file! Using default values.");
		}
		catch (java.lang.NumberFormatException i)
		{
			System.out.println("TravelPortals: An exception occurred when trying to read your config file.");
			System.out.println("TravelPortals: Check your config.yml!");
		}

        // Basic permissions setup
		if (usepermissions)
			setupPermissions();

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
            System.out.println("Could not read/write TravelPortals data folder! Aborting.");
            return;
        }



        // Attempt to read in the current version's save data.
		boolean successfulRead = false;
		try
		{
			FileInputStream fIn = new FileInputStream(new File(this.getDataFolder(), "TravelPortals.ser"));
			ObjectInputStream oIn = new ObjectInputStream(fIn);
			warpLocations = (ArrayList<WarpLocation>)oIn.readObject();
			oIn.close();
			fIn.close();
			successfulRead = true;

			doBackup();
		}
        catch (IOException i)
        {
	    	System.out.println("Could not load TravelPortals location file!");
        }
        catch (java.lang.ClassNotFoundException i)
        {
        }

		// If we could not read the config, try to read in the old format.
		if (!successfulRead)
		{
	        try
	        {
	            FileInputStream fIn = new FileInputStream("doorwarp.ser");
	            ObjectInputStream oIn = new ObjectInputStream(fIn);
	            ArrayList<WarpPoint> wps = new ArrayList<WarpPoint>();

	            wps = (ArrayList<WarpPoint>)oIn.readObject();

	            for (WarpPoint w : wps)
	            {
	            	warpLocations.add(new WarpLocation(w));
	            }
	            oIn.close();
	            fIn.close();
	           	System.out.println("Imported old TravelPortals data. Not to worry!");
	           	savedata();
	           	File oldversion = new File("doorwarp.ser");
	           	oldversion.renameTo(new File("doorwarp.ser.bak"));
	        }
	        catch (IOException i)
	        {
	        }
	        catch (java.lang.ClassNotFoundException i)
	        {
	        }
        }

        // Hackish helper for portals to the nether on my server.
        // Use it if you need it.
        // REMOVE THIS FOR REAL BUILDS
        /*for (WarpLocation w : warpLocations)
        {
           if (w.getWorld().equals("netherworld"))
           {
               w.setWorld("nether");
               System.out.println("Updating warp " + w.getName() + " from netherworld to nether");
           }
        }*/

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    /**
     * Implement commands sent from all over! (Well, only from players as it stands.
     * @param sender The person sending the command.
     * @param command The actual command used.
     * @param label The label that caused command to be used.
     * @param args The arguments provided with the command.
     * @return true to override the default behavior, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
        	// Very ugly. Some day this needs to be fixed.
            String[] aags = new String[args.length + 1];
            aags[0] = "/" + label;
            for (int i = 0; i < args.length; i++)
                aags[i+1] = args[i];
            return playerListener.onPlayerCommand((Player)sender, aags);
        }
        return false;
    }

    /*
     * Called upon disabling the plugin.
     */
    public void onDisable() {
		savedata();
    }

    // Generic Permission setup. (Provided in their code.)
    public void setupPermissions() {
	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");


	if(this.Permissions == null) {
	    if(test != null) {
		this.Permissions = ((Permissions)test).getHandler();
	    } else {
		this.getServer().getPluginManager().disablePlugin(this);
	    }
	}
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
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
            // i.printStackTrace();
            System.out.println("Could not save TravelPortals data!");
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
                System.out.println("Cannot save backups!");
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
            System.out.println("Warning: Saving a backup of the TravelPortals file failed.");
        }
        savedata(true);
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
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
     * @param x X coordinate to search near.
     * @param y Y coordinate to search near.
     * @param z Z coordinate to search near.
     * @return The index of a nearby portal in plugin.warpLocations, or -1 if it is not found.
     */
    public int getWarpFromLocation(int x, int y, int z)
    {
        // Iterate through all warps and check how close they are
        for (int i = 0; i < this.warpLocations.size(); i++)
        {
            WarpLocation wd = this.warpLocations.get(i);
            if (wd.getY() == y)
            {
                // We found one!!
                if (Math.abs(wd.getX() - x) <= 1 && Math.abs(wd.getZ() - z) <= 1)
                    return i;
            }
        }
        return -1;
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
                pOut.println(w.getX() + "," + w.getY() + "," + w.getZ() + "," + w.getName() + "," + w.getDestination() + "," + w.getHidden());

            pOut.close();
            fOut.close();
        }
        catch (Exception e)
        {
        }
    }
}

