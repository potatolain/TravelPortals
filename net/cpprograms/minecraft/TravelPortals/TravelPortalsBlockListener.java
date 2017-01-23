package net.cpprograms.minecraft.TravelPortals;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * TravelPortals block listener
 * @author cppchriscpp
 */
public class TravelPortalsBlockListener implements Listener {
	private final TravelPortals plugin;

	/**
	 * Constructor
	 * @param plugin The plugin to attach to.
	 */
	public TravelPortalsBlockListener(final TravelPortals plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a block is placed; let us know if a portal is being created.
	 * @param event The event related to the block placement.
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled()) 
		{
			plugin.logDebug("BlockPlace blocked because event was cancelled.");
			return;
		}

		if (event.getBlock().getType() == plugin.torchtype)
		{
			if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.portal.create"))
				return;
			Player player = event.getPlayer();
			int numwalls = 0;
			int x = event.getBlock().getX();
			int y = event.getBlock().getY();
			int z = event.getBlock().getZ();
			int doordir = 0;

			// Test the area to the right of the portal
			if (player.getWorld().getBlockAt(x + 1, y, z).getType() == plugin.blocktype &&
					player.getWorld().getBlockAt(x + 1, y + 1, z).getType() == plugin.blocktype)
				numwalls++;
			else if (
					plugin.doortypes.contains(player.getWorld().getBlockAt(x + 1, y, z).getType())
							&& plugin.doortypes.contains(player.getWorld().getBlockAt(x + 1, y + 1, z).getType())
					)
			{
				numwalls += 10;
				doordir = 1;
			}

			// Test the area to the left of the portal
			if (player.getWorld().getBlockAt(x - 1, y, z).getType() == plugin.blocktype &&
					player.getWorld().getBlockAt(x - 1, y + 1, z).getType() == plugin.blocktype)
				numwalls++;
			else if (
					plugin.doortypes.contains(player.getWorld().getBlockAt(x - 1, y, z).getType()) &&
							plugin.doortypes.contains(player.getWorld().getBlockAt(x - 1, y + 1, z).getType())
					)
			{
				numwalls += 10;
				doordir = 3;
			}

			// Test the area in front of the portal
			if (player.getWorld().getBlockAt(x, y, z + 1).getType() == plugin.blocktype &&
					player.getWorld().getBlockAt(x, y + 1, z + 1).getType() == plugin.blocktype)
				numwalls++;
			else if (
					plugin.doortypes.contains(player.getWorld().getBlockAt(x, y, z + 1).getType()) &&
									plugin.doortypes.contains(player.getWorld().getBlockAt(x, y + 1, z + 1).getType())
					)
			{
				numwalls += 10;
				doordir = 2;
			}

			// Test the area behind the portal
			if (player.getWorld().getBlockAt(x, y, z - 1).getType() == plugin.blocktype &&
					player.getWorld().getBlockAt(x, y + 1, z - 1).getType() == plugin.blocktype)
				numwalls++;
			else if (
					plugin.doortypes.contains(player.getWorld().getBlockAt(x, y, z - 1).getType()) &&
									plugin.doortypes.contains(player.getWorld().getBlockAt(x, y + 1, z - 1).getType())
					)
			{
				numwalls += 10;
				doordir = 4;
			}

			// Numwalls will be exactly 13 if there is one door alongside 3 walls of obsidian.
			// This is what we want. (x, y-1, z) is the coordinate above the torch, and needs to be empty.
			if (numwalls == 13 && player.getWorld().getBlockAt(x, y+1, z).getType() == Material.AIR)
			{
				player.getWorld().getBlockAt(x, y, z).setType(plugin.portaltype);
				player.getWorld().getBlockAt(x,y,z).setData((byte)0);
				player.getWorld().getBlockAt(x, y+1, z).setType(plugin.portaltype);
				player.getWorld().getBlockAt(x, y+1, z).setData((byte)0);

				player.sendMessage(ChatColor.DARK_RED + "You have created a portal! Type /portal help for help using it.");

				plugin.getPortalStorage().addPortal(new WarpLocation(x,y,z, doordir, player.getWorld().getName(), player.getName()));
				if (!plugin.getPortalStorage().save()) {
					plugin.logWarning("Error while saving the portal storage!");
				}
			}

		}
	}

	/**
	 * Called when a block is broken; let us know if a portal is being destroyed.
	 * @param event The event related to block breakage.
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{

		if (event.isCancelled())
		{
			plugin.logDebug("BlockBreak blocked due to event cancellation.");
			return;
		}

		// Is this block important to us?
		if (event.getBlock().getType() == plugin.blocktype || plugin.doortypes.contains(event.getBlock().getType()))
		{
			Player player = event.getPlayer();
			Block block = event.getBlock();
			for (WarpLocation w : plugin.getPortalStorage().getPortals().values())
			{
				// Check if the user actually hit one of them.
				if (((Math.abs(w.getX() - block.getX()) < 2 && Math.abs(w.getZ() - block.getZ()) < 1) || (Math.abs(w.getZ() - block.getZ()) < 2 && Math.abs(w.getX() - block.getX()) < 1)) && (block.getY() - w.getY() < 2 && block.getY() - w.getY() >= 0))
				{
					// Permissions test
					if (plugin.usepermissions) {
						if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.portal.destroy")) {
							event.setCancelled(true);
							return;
						}
						if (!w.getOwner().equals("") && !w.getOwner().equals(player.getName())) {
							if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.admin.portal.destroy")) {
								event.setCancelled(true);
								return;
							}
						}
					}
					// They hit an important warping block..destroy the warp point.
					// Remove the blocks
					player.getWorld().getBlockAt(w.getX(), w.getY(), w.getZ()).setType(Material.AIR);
					player.getWorld().getBlockAt(w.getX(), w.getY() + 1, w.getZ()).setType(Material.AIR);
					// Remove it from the list of warps
					this.plugin.getPortalStorage().removePortal(w);
					this.plugin.getPortalStorage().save();
					// Let the user know he's done a bad, bad thing. :<
					player.sendMessage(ChatColor.DARK_RED + "You just broke a portal.");
					break;
				}
			}

		}
	}
	
	/**
	 * Handle when water tries to flow from a portal.
	 * @param event The BlockFromTo event we want to prevent. 
	 */
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) 
	{
		if (event.isCancelled() || event.getFace() != BlockFace.DOWN)
			return;
		
		Material type = event.getBlock().getType();
		
		if (type == Material.STATIONARY_WATER)
		{
			if (type != Material.STATIONARY_WATER && type != Material.WATER)
				return;
		} 
		else 
		{
			if (type != plugin.portaltype)
				return;
		}
		
		if (plugin.getPortalStorage().getPortal(event.getBlock().getLocation()) != null)
			event.setCancelled(true);
	}
}
