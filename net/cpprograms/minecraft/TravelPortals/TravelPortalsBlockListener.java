package net.cpprograms.minecraft.TravelPortals;

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

		if (event.getBlock().getTypeId() == plugin.torchtype)
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
			if (player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.blocktype &&
					player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.blocktype)
				numwalls++;
			else if (
					(player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.doortype &&
					player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.doortype)
					||
					(player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.doortype2 &&
					player.getWorld().getBlockAt(x + 1, y + 1, z).getTypeId() == plugin.doortype2)
					)
			{
				numwalls += 10;
				doordir = 1;
			}

			// Test the area to the left of the portal
			if (player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.blocktype &&
					player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.blocktype)
				numwalls++;
			else if (
					(player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.doortype &&
					player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.doortype)
					||
					(player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.doortype2 &&
					player.getWorld().getBlockAt(x - 1, y + 1, z).getTypeId() == plugin.doortype2)
					)
			{
				numwalls += 10;
				doordir = 3;
			}

			// Test the area in front of the portal
			if (player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.blocktype &&
					player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.blocktype)
				numwalls++;
			else if (
					(player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.doortype &&
					player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.doortype)
					||
					(player.getWorld().getBlockAt(x, y, z + 1).getTypeId() == plugin.doortype2 &&
					player.getWorld().getBlockAt(x, y + 1, z + 1).getTypeId() == plugin.doortype2)
					)
			{
				numwalls += 10;
				doordir = 2;
			}

			// Test the area behind the portal
			if (player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.blocktype &&
					player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.blocktype)
				numwalls++;
			else if (
					(player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.doortype &&
					player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.doortype)
					||
					(player.getWorld().getBlockAt(x, y, z - 1).getTypeId() == plugin.doortype2 &&
					player.getWorld().getBlockAt(x, y + 1, z - 1).getTypeId() == plugin.doortype2)
					)
			{
				numwalls += 10;
				doordir = 4;
			}

			// Numwalls will be exactly 13 if there is one door alongside 3 walls of obsidian.
			// This is what we want. (x, y-1, z) is the coordinate above the torch, and needs to be empty.
			if (numwalls == 13 && player.getWorld().getBlockAt(x, y+1, z).getType() == Material.AIR)
			{
				player.getWorld().getBlockAt(x, y, z).setTypeId(plugin.portaltype);
				player.getWorld().getBlockAt(x,y,z).setData((byte)16);
				player.getWorld().getBlockAt(x, y+1, z).setTypeId(plugin.portaltype);
				player.getWorld().getBlockAt(x, y+1, z).setData((byte)16);

				player.sendMessage("§4You have created a portal! Type /portal help for help using it.");

				this.plugin.addWarp(new WarpLocation(x,y,z, doordir, player.getWorld().getName(), player.getName()));
				this.plugin.savedata();
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
		if (event.getBlock().getTypeId() == plugin.blocktype || event.getBlock().getTypeId() == plugin.doortype || event.getBlock().getTypeId() == plugin.doortype2)
		{
			Player player = event.getPlayer();
			Block block = event.getBlock();
			for (WarpLocation w : plugin.warpLocations)
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
						if (!w.getOwner().equals("") && !w.getOwner().equals(player)) {
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
					this.plugin.warpLocations.remove(plugin.warpLocations.indexOf(w));
					this.plugin.savedata();
					// Let the user know he's done a bad, bad thing. :<
					player.sendMessage("§4You just broke a portal.");
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
		
		int typeId = event.getBlock().getTypeId();
		
		if (typeId == Material.STATIONARY_WATER.getId())
		{
			if (typeId != Material.STATIONARY_WATER.getId() && typeId != Material.WATER.getId())
				return;
		} 
		else 
		{
			if (typeId != plugin.portaltype)
				return;
		}
		
		if (plugin.getWarpFromLocation(event.getBlock().getWorld().getName(), event.getBlock().getX(), event.getBlock().getY(),event.getBlock().getZ()) != -1)
			event.setCancelled(true);
	}
}
