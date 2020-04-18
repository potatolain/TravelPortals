package net.cpprograms.minecraft.TravelPortals;

import io.papermc.lib.PaperLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Level;

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

		Location locy = plugin.getWarpLocationIfAllowed(event.getPlayer());
		if (locy == null)
			return;

		plugin.teleportToWarp(event.getPlayer(), locy);

	}

	private boolean sameBlock(Location from, Location to) {
		return to != null && from.getWorld() == to.getWorld() && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ();
	}

	public void unregister()
	{
		PlayerMoveEvent.getHandlerList().unregister(this);
	}
}

