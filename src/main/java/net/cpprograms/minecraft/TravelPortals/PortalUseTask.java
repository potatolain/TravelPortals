package net.cpprograms.minecraft.TravelPortals;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


/**
 * Checks all players for if they are currently using a portal and teleports them if necessary..
 * @author mc_dev
 *
 */
public class PortalUseTask implements Runnable {

	/**
	 * The ID of this task, or -1 if it is not set.
	 */
	int taskId = -1;

	/**
	 * The plugin that we are attached to.
	 */
	TravelPortals plugin;

	/**
	 * Prior portal destinations; cached for quick recall.
	 */
	Map<String,Location> lastDestinations = new HashMap<String, Location>();

	/**
	 * Subclass of this task which follows individual players and checks them.
	 * @author mc_dev
	 *
	 */
	class FollowTask implements Runnable
	{
		/**
		 * The PortalUseTask which owns this.
		 */
		PortalUseTask useTask;

		/**
		 * The player which is being followed by this.
		 */
		String playerName;

		/**
		 * Which warp we're checking against.
		 */
		WarpLocation w;

		/**
		 * Constructor for this.
		 * @param plugin The PortalUseTask which this is connected to.
		 * @param playerName The player we're following.
		 * @param w The warp to check for.
		 */
		public FollowTask( PortalUseTask plugin, String playerName, WarpLocation w)
		{
			this.useTask = plugin;
			this.playerName = playerName;
			this.w = w;
		}

		/**
		 * What to do when this is run.
		 */
		@Override
		public void run() 
		{
			useTask.checkPlayer(playerName, w);
		}
	}

	/**
	 * Constructor for the PortalUseTask.
	 * @param plugin The plugin we're attaching to.
	 */
	public PortalUseTask(TravelPortals plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Core repeated checks - iterate over all players and check if they are in a portal they can use.
	 * Then check for teleport directly or delay the check 
	 */
	@Override
	public void run() 
	{

		if ( this.taskId == -1 ) 
			return;

		Server server = Bukkit.getServer();
		int n = 0; // cleanup heuristic (if 0 , can clear map of lastDestinations).

		for (Player player : server.getOnlinePlayers())
		{
			WarpLocation portal = isInPortal(player);
			String playerName = player.getName();
			if (portal == null)
			{
				lastDestinations.remove(playerName);
				continue;
			}
			if (!portal.isUsable(plugin.cooldown))
			{
				n++;
				continue;
			}
			String destName = portal.getDestination();
			if (destName == null)
			{
				lastDestinations.remove(playerName);
			} 
			else 
			{
				Location lastTarget = lastDestinations.get(playerName);
				if (lastTarget != null)
				{
					if (sameLoc(lastTarget, player.getLocation()))
					{
						n++;
						continue;
					}
				}

				WarpLocation targetW = plugin.getPortalStorage().getPortal(destName);

				if (targetW == null)
				{
					lastDestinations.remove(playerName);
				} 
				else 
				{
					World world = plugin.getServer().getWorld(targetW.getWorld());
					if (world == null) 
						continue;

					lastDestinations.put(playerName, new Location(world, .5 + targetW.getX(), targetW.getY(), .5 + targetW.getZ()));
				}
			}
			n++;

			if (plugin.followTicks > 0)
			{
				// register a simple repeated Task for checking he player then
				server.getScheduler().scheduleSyncDelayedTask( plugin, new FollowTask(this, playerName, portal));
			}
			else
			{
				Location warpTo = plugin.getWarpLocationIfAllowed(player);
				if (warpTo != null)
					player.teleport(warpTo);
			}
		}
		if (n == 0) 
			lastDestinations.clear();
	}

	/**
	 * Test to see if the two locations passed in are functionally the same.
	 * @param l1 The first location.
	 * @param l2 The second location.
	 * @return True if they are the same; false otherwise.
	 */
	private boolean sameLoc(Location l1, Location l2) {
		return l1.getWorld().equals(l2.getWorld())
				&& l1.getBlockX() == l2.getBlockX()
				&& l1.getBlockY() == l2.getBlockY()
				&& l1.getBlockZ() == l2.getBlockZ();
	}

	/**
	 * Check if a player is an a portal and has permission
	 * @param player The player to test.
	 * @return The portal the player is in or null if he is in none
	 */
	private WarpLocation isInPortal(Player player)
	{ 
		// Permissions check
		if (!plugin.permissions.hasPermission(player, "travelportals.portal.use"))
			return null;
		Location playerLoc = player.getLocation();
		World world = player.getWorld();

		playerLoc.setX(playerLoc.getX() + 1.0);
		Material blockType = world.getBlockAt(playerLoc).getType();
		playerLoc.setX(playerLoc.getX() - 1.0);

		// Is the user actually in portal material?
		if (blockType == plugin.portaltype || blockType == plugin.blocktype || plugin.doortypes.contains(blockType))
		{
			return plugin.getPortalStorage().getPortal(playerLoc);
		}
		return null;
	}

	/**
	 * Check if the player is still in the same portal and start tp.
	 * @param playerName The name of the player to check.
	 * @param w The warp to test.
	 */
	void checkPlayer(String playerName, WarpLocation w)
	{
		Player player = Bukkit.getServer().getPlayer(playerName);
		if ( player == null ) 
			return;
		// check if still in same portal
		if (isInPortal(player) == w)
		{
			if (w.isUsable(plugin.cooldown))
			{
				Location loc = plugin.getWarpLocationIfAllowed(player);
				if (loc != null)
					player.teleport(loc);
			}
		}
	}

	/**
	 * Register this task (convenience)
	 * @return true if successful; false otherwise.
	 */
	public boolean register()
	{
		if (this.taskId != -1) 
			this.cancel();

		int mainTicks = plugin.mainTicks;
		int followTicks = plugin.followTicks;

		if (mainTicks <= 0) 
			mainTicks = 23;

		if (mainTicks <= followTicks) 
			mainTicks = Math.max(23, followTicks+3);

		taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, mainTicks, mainTicks);
		return (taskId != -1);
	}

	/**
	 * Unregister this task (convenience).
	 */
	public void cancel()
	{
		if (taskId != -1)
		{
			Bukkit.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}

	/**
	 * Get the ID of this task.
	 * @return The id of this task.
	 */
	public int getTaskId()
	{
		return taskId;
	}

}