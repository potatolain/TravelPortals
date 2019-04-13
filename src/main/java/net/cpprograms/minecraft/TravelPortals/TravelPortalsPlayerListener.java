package net.cpprograms.minecraft.TravelPortals;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author cppchriscpp
 */
public class TravelPortalsPlayerListener implements Listener {
	private final TravelPortals plugin;

	/**
	 * Constructor.
	 * @param instance The plugin to attach to.
	 */
	public TravelPortalsPlayerListener(TravelPortals instance) {
		plugin = instance;
	}

	/**
	 * Runs when a player moves. Acts if the player is in a portal block.
	 * @param event The event related to this.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (sameBlock(event.getFrom(), event.getTo()))
			return;

		// Permissions check
		if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.portal.use"))
			return;

		Location locy = plugin.getWarpLocationIfAllowed(event.getPlayer());
		if (locy == null)
			return;

		// Pre-load the chunk we're headed for...
		if (!locy.getBlock().getChunk().isLoaded())
			locy.getBlock().getChunk().load();
		
		// Shove a block under them.
		Block below = locy.getBlock().getRelative(BlockFace.DOWN);
		event.getPlayer().sendBlockChange(below.getLocation(), below.getType(), below.getData());

		// Warp the user!
		event.getPlayer().teleport(locy);
		event.setTo(locy);
	}

	private boolean sameBlock(Location from, Location to) {
		return from.getWorld() == to.getWorld() && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ();
	}

	public void unregister()
	{
		PlayerMoveEvent.getHandlerList().unregister(this);
	}
}

