package net.cpprograms.minecraft.TravelPortals;

import io.papermc.lib.PaperLib;
import net.cpprograms.minecraft.General.CommandHandler;
import net.cpprograms.minecraft.General.PermissionsHandler;
import net.cpprograms.minecraft.General.PluginBase;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.cpprograms.minecraft.TravelPortals.storage.LegacyStorage;
import net.cpprograms.minecraft.TravelPortals.storage.PortalStorage;
import net.cpprograms.minecraft.TravelPortals.storage.StorageType;
import net.cpprograms.minecraft.TravelPortals.storage.YamlStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * TravelPortals Bukkit port.
 *
 * @author cppchriscpp
 */
public class TravelPortals extends PluginBase {
	/**
	 * A Player listener
	 */
	private TravelPortalsPlayerListener playerListener = null;

	/**
	 * A Block listener.
	 */
	private final TravelPortalsBlockListener blockListener = new TravelPortalsBlockListener(this);

	/**
	 * A Block listener.
	 */
	private final TravelPortalsDrownListener drownListener = new TravelPortalsDrownListener(this);

	/**
	 * Whether or not we should use UUIDs to resolve portal permissions
	 */
	private boolean useUuid = false;

	/**
	 * Here we store the portals
	 */
	protected PortalStorage portalStorage;

	/**
	 * The type of block portals must be made from. (Default is 49 (Obsidian))
	 */
	protected Material blocktype = Material.OBSIDIAN;

	/**
	 * A friendly name for the construction block type.
	 */
	protected String strBlocktype = "obsidian";

	/**
	 * Whether to automatically export the list of portals to travelportals.txt.
	 */
	private boolean autoExport = false;

	/**
	 * Server access!
	 */
	public static Server server;

	/**
	 * The numerical type of block used inside the portal. This would be 90, but portal blocks stink.
	 */
	protected Material portaltype = Material.WATER;

	/**
	 * The list of types of blocks used for the doorway
	 */
	protected Set<Material> doortypes = EnumSet.of(
			Material.OAK_DOOR,
			Material.IRON_DOOR,
			Material.ACACIA_DOOR,
			Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR,
			Material.JUNGLE_DOOR,
			Material.SPRUCE_DOOR
	);

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
	protected Material torchtype = Material.REDSTONE_TORCH;

	/**
	 * Portal create sound when a player creates a new portal
	 */
	protected Sound portalCreateSound = Sound.BLOCK_PORTAL_TRIGGER;

	/**
	 * Portal ambient sound, currently only gets played for everyone at the start and target portal when a player travels through it
	 */
	protected Sound portalAmbientSound = Sound.BLOCK_PORTAL_AMBIENT;

	/**
	 * Sound playing to the player travelling through a portal
	 */
	protected Sound portalTravelSound = Sound.BLOCK_PORTAL_TRAVEL;

	/**
	 * Do we want to use the permissions plugin?
	 */
	protected boolean usepermissions = false;

	/**
	 * Do we want to allow portals to teleport between worlds?
	 */
	protected boolean crossWorldPortals = true;

	/**
	 * How long until a portal reactivates?
	 */
	protected int cooldown = 5000;

	/**
	 * How many backup saves should there be? (3)
	 */
	private int numsaves = 3;

	/**
	 * Ticks for a sync repeated task to check all players for if they are in a portal.
	 */
	protected int mainTicks = 17;

	/**
	 * Ticks for checking for the target desination and the teleport after the player was found in a portal.
	 * Set to <= 0 for immediate teleport.
	 */
	protected int followTicks = 7;

	/**
	 * Flag indicating if playerMove should be used or not.
	 */
	protected boolean usePlayerMove = true;

	/**
	 * The task for the alternate checking method for if players are in a portal etc.
	 */
	private PortalUseTask useTask = null;


	/**
	 * Called upon enabling the plugin
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public void onEnable() {

		server = getServer();

		if (!load())
		{
			logSevere("Aborting plugin load");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		portalStorage.update();

		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(drownListener, this);

		super.onEnable();

		// Override PermissionsHandler with our variable. Have to do this after the parent method
		permissions = new PermissionsHandler(usepermissions);
		commandHandler = new CommandHandler(this, PortalCommandSet.class);
	}

	/**
	 * Load the config
	 * @return true if it was successfully loaded; false if an error occured
	 */
	public boolean load() {
		// Read in the YAML config stuff
		try {
			FileConfiguration conf = getConfig();
			StorageType storageType = StorageType.LEGACY;
			if (conf.get("storagetype", null) != null) {
				try {
					storageType = StorageType.valueOf(conf.getString("storagetype").toUpperCase());
				} catch (IllegalArgumentException e) {
					logSevere(conf.getString("storagetype") + " is not a valid storage type? Valid types are " + Arrays.toString(StorageType.values()) + ".");
					return false;
				}
			}

			logDebug("Storage type is " + storageType);

			useUuid = conf.getBoolean("use-uuids", false);

			try {
				portalStorage = createStorage(storageType);
			} catch (IllegalArgumentException e) {
				logSevere(e.getMessage());
			}

			if (conf.get("frame", null) != null) {
				blocktype = Material.valueOf(conf.getString("frame").toUpperCase());
			}
			if (conf.get("framename", null) != null)
				strBlocktype = conf.getString("framename");
			if (conf.get("fill", null) != null) {
				portaltype = Material.valueOf(conf.getString("fill").toUpperCase());
			}
			if (conf.get("doorlist", null) != null) {
				List<Material> doorList = new ArrayList<Material>();
				for (String doorType : conf.getStringList("doorlist")) {
					doorList.add(Material.valueOf(doorType.toUpperCase()));
				}
				if (!doorList.isEmpty()) {
					doortypes = EnumSet.copyOf(doorList);
				}
			} else {
				if (conf.get("door", null) != null) {
					doortypes.add(Material.valueOf(conf.getString("door").toUpperCase()));
				}
				if (conf.get("door2", null) != null) {
					doortypes.add(Material.valueOf(conf.getString("door2").toUpperCase()));
				}
				// Yes, this is a bit lame. I'd like to save updated config, but at the same time I don't want to wipe out comments and make
				// the file extremely unclear.
				logWarning("Old style door configuration found. Config loaded correctly, but you may want to update it.");
				logWarning("The plugin now supports a list \"doorlist\", to allow use of the new wooden door types.");
				logWarning("Example configuration here: https://github.com/cppchriscpp/TravelPortals/blob/master/config.yml");
			}
			if (conf.get("doorname", null) != null)
				strDoortype = conf.getString("doorname");
			if (conf.get("torch", null) != null) {
				torchtype = Material.valueOf(conf.getString("torch").toUpperCase());
			}
			if (conf.get("torchname", null) != null)
				strTorchtype = conf.getString("torchname");

			if (conf.isConfigurationSection("sounds"))
			{
				portalCreateSound = getSound(conf.getString("sounds.create"));
				portalAmbientSound = getSound(conf.getString("sounds.ambient"));
				portalTravelSound = getSound(conf.getString("sounds.travel"));
			}

			if (conf.get("permissions", null) != null)
				usepermissions = conf.getBoolean("permissions");
			if (conf.get("cross-world-portals", null) != null)
				crossWorldPortals = conf.getBoolean("cross-world-portals");
			if (conf.get("autoexport", null) != null)
				autoExport = conf.getBoolean("autoexport");
			if (conf.get("cooldown", null) != null)
				cooldown = 1000 * conf.getInt("cooldown");
			if (conf.get("numsaves", null) != null)
				numsaves = conf.getInt("numsaves");

			if (conf.get("useplayermove", null) != null)
				usePlayerMove = conf.getBoolean("useplayermove");
			if (conf.get("polling-mainticks", null) != null)
				mainTicks = conf.getInt("polling-mainticks");
			if (conf.get("polling-followticks", null) != null)
				followTicks = conf.getInt("polling-followticks");

		} catch (NumberFormatException i) {
			logSevere("An exception occurred when trying to read your config file. " + i.getMessage());
			logSevere("Check your config.yml!");
		} catch (IllegalArgumentException e) {
			logSevere("An exception occurred when trying to read a block type from your config file. " + e.getMessage());
			logSevere("Check your config.yml!");
		}

		if (!portalStorage.load()) {
			return false;
		}

		logInfo("Loaded " + portalStorage.getPortals().size() + " portals!");

		// Stop old move listener/task if there are some

		if (playerListener != null)
		{
			playerListener.unregister();
			playerListener = null;
		}
		if (useTask != null)
		{
			useTask.cancel();
			useTask = null;
		}

		// Register our events

		if (usePlayerMove)
		{
			logDebug("Using player move event listener");
			playerListener = new TravelPortalsPlayerListener(this);
			getServer().getPluginManager().registerEvents(playerListener, this);
		}
		else
		{
			logDebug("Using portal use task");
			useTask = new PortalUseTask(this);
			if (!useTask.register())
			{
				logSevere("Failed to register portal use task. Falling back to old PlayerMove style.");
				playerListener = new TravelPortalsPlayerListener(this);
				getServer().getPluginManager().registerEvents(playerListener, this);
				useTask = null;
			}

		}
		permissions = new PermissionsHandler(usepermissions);
		return true;
	}

	private Sound getSound(String name) {
		try {
			return Sound.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			if (!name.isEmpty()) {
				logWarning(name + " is not a valid sound!");
			}
			return null;
		}
	}

	/**
	 * Called upon disabling the plugin.
	 */
	@Override
	public void onDisable() {
		savedata();
		super.onDisable();
	}

	/**
	 * Saves all door warp stuff to disk.
	 * @deprecated Use {@link PortalStorage#save()}
	 */
	@Deprecated
	public void savedata()
	{
		portalStorage.save();
	}

	/**
	 * Get the index of a warp object from its name.
	 * @param name The name of the portal to find.
	 * @return The portal info or null if none was found
	 * @deprecated Use {@link PortalStorage#getPortal(String)}
	 */
	@Deprecated
	public WarpLocation getWarp(String name)
	{
		return portalStorage.getPortal(name);
	}

	/**
	 * Find a warp point from a relative location. (Within 1 block)
	 * @param location The location that is near the portal
	 * @return The index of a nearby portal in plugin.warpLocations, or -1 if it is not found.
	 * @deprecated Use {@link PortalStorage#getNearbyPortal(Location, int)}
	 */
	@Deprecated
	public WarpLocation getWarpFromLocation(Location location)
	{
		return portalStorage.getNearbyPortal(location, 1);
	}

	/**
	 * Rename a world for all existing portals.
	 * @param oldWorld The name of the old world.
	 * @param newWorld The name of the new world.
	 * @deprecated Use {@link PortalStorage#renameWorld(String, String)}
	 */
	@Deprecated
	public void renameWorld(String oldWorld, String newWorld)
	{
		portalStorage.renameWorld(oldWorld, newWorld);
	}

	/**
	 * Delete all portals linking to a world.
	 * @param oldWorld The world to delete all portals to.
	 * @deprecated Use {@link PortalStorage#deleteWorld}
	 */
	@Deprecated
	public void deleteWorld(String oldWorld)
	{
		portalStorage.deleteWorld(oldWorld);
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
			for (WarpLocation w : portalStorage.getPortals().values())
				pOut.println(w.getX() + "," + w.getY() + "," + w.getZ() + "," + w.getName() + "," + w.getDestination() + "," + w.isHidden() +"," + w.getWorld() + "," + w.getOwner());

			pOut.close();
			fOut.close();
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Import existing portals from a given txt file into the world. THIS WIPES OUT EXISTING PORTALS. You have been warned.
	 * @param file The file to import from.
	 */
	public void importPortalList(File file)
	{
		try
		{
			portalStorage.clearCache();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
			{
				String[] data = line.split(",", 8);
				if (data.length != 8)
					continue;
				WarpLocation warp = new WarpLocation(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), 0, data[6], data[7]);
				warp.setHidden("1".equals(data[5]));
				warp.setDestination(data[4]);
				warp.setName(data[3]);
				portalStorage.addPortal(warp);
			}
			portalStorage.save();
			br.close();
		}
		catch (Exception e)
		{
			this.logSevere("Unable to load portal list from "+file.getName()+"!");
			this.logDebug(e.getMessage());
		}
	}

	/**
	 * Convert from one storage type to another
	 * @param from The type to convert from
	 * @param to The type to convert to
	 * @return true if the conversation succeeded; false if it failed
	 */
	public boolean convertStorage(PortalStorage from, PortalStorage to)	{
		if (from.load()) {
			for (WarpLocation portal : from.getPortals().values()) {
				to.addPortal(portal);
			}
			return to.save();
		}
		return false;
	}

	/**
	 * Gets the destination of this teleport, but only if the user can use it.
	 * @param player The player in question.
	 * @param disablePortal Disable this portal (set its cooldown).
	 * @return The location to warp to, or null if there is no warping to be done.
	 */
	public Location getWarpLocationIfAllowed(Player player, boolean disablePortal)
	{
		if (!permissions.hasPermission(player, "travelportals.portal.use"))
			return null;

		Location playerLoc = player.getLocation();
		Material blockType = player.getWorld().getBlockAt(playerLoc).getType();
		// Is the user actually in portal material?
		if (blockType == portaltype || blockType == blocktype || doortypes.contains(blockType))
		{
			WarpLocation portal = portalStorage.getPortal(player.getLocation());
			if (portal == null)
				return null;

			if (!portal.isUsable(cooldown))
			{
				return null;
			}

			// Ownership check
			if (usepermissions) 
			{
				if (!permissions.hasPermission(player, "travelportals.portal.use")) 
				{
					player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use portals.");
					return null;
				}

				if (!portal.isOwner(player))
				{
					if (!permissions.hasPermission(player, "travelportals.admin.portal.use")) 
					{
						player.sendMessage(ChatColor.DARK_RED + "You do not own this portal, so you cannot use it.");
						return null;
					}
				}
			}

			// Complain if this isn't usable
			if (!portal.hasDestination())
			{
				if (!permissions.hasPermission(player, "travelportals.command.warp") || !portal.isOwner(player))
				{
					player.sendMessage(ChatColor.DARK_RED + "This portal has no destination.");
				}
				else
				{
					player.sendMessage(ChatColor.DARK_RED + "You need to set this portal's destination first!");
					player.sendMessage(ChatColor.DARK_GREEN + "See /portal help for more information.");
				}
				portal.setLastUsed();
				return null;
			}
			else // send the user on his way!
			{
				// Find the warp this one points to
				WarpLocation destination = portalStorage.getPortal(portal.getDestination());

				if (destination == null)
				{
					player.sendMessage(ChatColor.DARK_RED + "This portal's destination (" + portal.getDestination() + ") does not exist.");
					if (!(permissions.hasPermission(player, "travelportals.command.warp")))
						player.sendMessage(ChatColor.DARK_GREEN + "See /portal help for more information.");

					portal.setLastUsed();
					return null;
				}
				else
				{

					// Another permissions check...
					if (!permissions.hasPermission(player, "travelportals.admin.portal.use")) 
					{
						if (!crossWorldPortals
								&& !destination.getWorld().isEmpty() && !player.getWorld().getName().equals(destination.getWorld())
								&& !permissions.hasPermission(player, "travelportals.portal.use.crossworld")
						)
						{
							player.sendMessage(ChatColor.DARK_RED + "You cannot travel between worlds.");
							return null;
						}

						if (!destination.isOwner(player))
						{
							player.sendMessage(ChatColor.DARK_RED + "You do not own the destination portal, and do not have permission to use it.");
							return null;
						}
					}
					int x = destination.getX();
					int y = destination.getY();
					int z = destination.getZ();
					float rotation = 180.0f; // c

					// Use rotation to place the player correctly.
					int d = destination.getDoorPosition();

					if (d > 0)
					{
						if (d == 1)
							rotation = 270.0f;
						else if (d == 2)
							rotation = 0.0f;
						else if (d == 3)
							rotation = 90.0f;
						else
							rotation = 180.0f;
					}
					// Guess.
					else if (doortypes.contains(player.getWorld().getBlockAt(x + 1, y, z).getType()))
					{
						rotation = 270.0f;
						destination.setDoorPosition(1);
					}
					else if (doortypes.contains(player.getWorld().getBlockAt(x, y, z+1).getType()))
					{
						rotation = 0.0f;
						destination.setDoorPosition(2);
					}
					else if (doortypes.contains(player.getWorld().getBlockAt(x - 1, y, z).getType()))
					{
						rotation = 90.0f;
						destination.setDoorPosition(3);
					}
					else if (doortypes.contains(player.getWorld().getBlockAt(x, y, z-1).getType()))
					{
						rotation = 180.0f;
						destination.setDoorPosition(4);
					}
					else
					{
						// oh noes :<
					}
					// Create the location for the user to warp to
					Location locy = new Location(player.getWorld(), x + 0.50, y + 0.1, z + 0.50, rotation, 0);
					if (destination.getWorld() != null && !destination.getWorld().equals(""))
					{
						World wo = getServer().getWorld(destination.getWorld());
						if (wo != null) {
							locy.setWorld(wo);
						} else {
							logWarning("World " + destination.getWorld() + " for portal " + destination.getName() + " was not found! - consider running the following command from the console:");
							logWarning("portal fixworld " + TravelPortals.server.getWorlds().get(0).getName());
							logWarning("Replacing the world name with the world this portal should link to, if it is incorrect.");
							return null;
						}
					}
					else
					{
						logWarning("World name not set for portal " + destination.getName() + " - consider running the following command from the console:");
						logWarning("portal fixworld " + TravelPortals.server.getWorlds().get(0).getName());
						logWarning("Replacing the world name with the world this portal should link to, if it is incorrect.");
						locy.setWorld(TravelPortals.server.getWorlds().get(0));
					}

					if (disablePortal)
					{
						destination.setLastUsed();
						portal.setLastUsed();
					}

					logDebug(player.getName() + " entered portal " + portal.getName() + " at " + portal.getIdentifierString() + " which warps to " + destination.getName() + " at " + destination.getIdentifierString());

					return locy;
				}
			}
		}
		return null;
	}

	/**
	 * Teleport a player to a portal warp location
	 * @param player The player to teleport
	 * @param warp The warp
	 */
	public void teleportToWarp(Player player, Location warp) {
		if (warp.getWorld() == null) {
			player.sendMessage(ChatColor.RED + "Target portal does not have a world set!");
			return;
		}

		if (!warp.getWorld().getWorldBorder().isInside(warp)) {
			player.sendMessage(ChatColor.RED + "Target portal is outside of the world border!");
			return;
		}

		if (portalAmbientSound != null)
			player.getWorld().playSound(player.getLocation(), portalAmbientSound, SoundCategory.AMBIENT, 1f, 1f);

		Location to = warp.clone();
		PaperLib.getChunkAtAsync(warp, false).thenAccept(chunk -> {
			Block block = to.getBlock();
			if (block.getType().isSolid() || block.getRelative(BlockFace.UP).getType().isSolid()) {
				player.sendMessage(ChatColor.RED + "Target portal is obstructed!");
				return;
			}

			if (portalAmbientSound != null)
				to.getWorld().playSound(to, portalAmbientSound, SoundCategory.AMBIENT, 1f, 1f);
			// Shove a block under them.
			Block below = block.getRelative(BlockFace.DOWN);
			player.sendBlockChange(below.getLocation(), below.getType().isSolid() ? below.getBlockData() : Material.BEDROCK.createBlockData());

			// Warp the user!
			player.teleport(to);
			if (portalTravelSound != null)
				player.playSound(to, portalTravelSound, SoundCategory.BLOCKS, 1f, 1f);
			logDebug("Teleported " + player.getName() + " to " + to);
		}).exceptionally(ex -> {
			player.sendMessage(ChatColor.RED + "Error while teleporting: " + ex.getMessage());
			getLogger().log(Level.SEVERE, "Error while loading chunk to teleport " + player.getName() + " to " + to, ex);
			return null;
		});
	}

	/**
	 * Get a location for the user to warp to, if they are allowed to use it.
	 * @param player The player to warp.
	 * @return A location, or null if the user does not need to be teleported.
	 */
	public Location getWarpLocationIfAllowed(Player player) 
	{ 
		return getWarpLocationIfAllowed(player, true); 
	}

	/**
	 * Whether to automatically export the list of portals to travelportals.txt.
	 */
	public boolean isAutoExport() {
		return autoExport;
	}

	/**
	 * Whether or not UUIDs should be used for portal access
	 */
	public boolean shouldUseUuid() {
		return useUuid;
	}

	/**
	 * How many backup saves should there be? (Default is 3)
	 */
	public int getNumSaves() {
		return numsaves;
	}

	/**
	 * Get the portal storage used by this plugin
	 */
	public PortalStorage getPortalStorage() {
		return portalStorage;
	}

	/**
	 * Create a new PortalStorage from of a certain type
	 * @param type The type of storage
	 * @return A new PortalStorage object
	 * @throws IllegalArgumentException If the type is not supported
	 */
	public PortalStorage createStorage(StorageType type) throws IllegalArgumentException {
		switch (type) {
			case LEGACY:
				return new LegacyStorage(this);
			case YAML:
				return new YamlStorage(this);
			default:
				throw new IllegalArgumentException("The storage type " + type + " is not properly supported yet! Please choose a different one!");
		}
	}
}

