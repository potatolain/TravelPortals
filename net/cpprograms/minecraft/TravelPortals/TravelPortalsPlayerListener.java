package net.cpprograms.minecraft.TravelPortals;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        // Permissions check
        if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.portal.use"))
    		return;

        Location locy = plugin.getWarpLocationIfAllowed(event.getPlayer());
        if (locy == null)
        	return;
        
        // Pre-load the chunk we're headed for...
        if (!locy.getBlock().getChunk().isLoaded())
        	locy.getBlock().getChunk().load();
        
        // Warp the user!
        event.getPlayer().teleport(locy);
        event.setTo(locy);
    }

}

