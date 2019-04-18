package net.cpprograms.minecraft.TravelPortals;

/*
 * 2019 Max Lee aka Phoenix616 (mail@moep.tv)
 */

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Handle events for all Player related events
 * @author cppchriscpp
 */
public class TravelPortalsDrownListener implements Listener {
	private final TravelPortals plugin;

	/**
	 * Constructor.
	 * @param instance The plugin to attach to.
	 */
	public TravelPortalsDrownListener(TravelPortals instance) {
		plugin = instance;
	}

	/**
	 * Runs when a player is inside a portal and looses air
	 * @param event The event related to this.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerAirChange(EntityAirChangeEvent event)
	{
		if (!(event.getEntity() instanceof Player) || ((Player) event.getEntity()).getMaximumAir() != ((Player) event.getEntity()).getRemainingAir())
			return;

		if (plugin.getPortalStorage().getNearbyPortals(event.getEntity().getLocation(), 1).isEmpty())
			return;

		event.setCancelled(true);
	}
	/**
	 * Runs when a player is inside a portal and gets drowning damage to drown
	 * @param event The event related to this.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDrowning(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() == EntityDamageEvent.DamageCause.DROWNING)
			return;

		if (plugin.getPortalStorage().getNearbyPortals(event.getEntity().getLocation(), 1).isEmpty())
			return;

		event.setCancelled(true);
	}
}

