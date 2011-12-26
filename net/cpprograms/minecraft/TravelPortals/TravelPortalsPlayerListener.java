package net.cpprograms.minecraft.TravelPortals;

import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.World;

/**
 * Handle events for all Player related events
 * @author cppchriscpp
 */
public class TravelPortalsPlayerListener extends PlayerListener {
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
	 */
    public void onPlayerMove(PlayerMoveEvent event)
    {
        // Permissions check
        if (!plugin.permissions.hasPermission(event.getPlayer(), "travelportals.portal.use"))
    		return;

        // The player that caused this is necessary, as is the block.
    	Player player = event.getPlayer();
		Block blk = player.getWorld().getBlockAt(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());

        // Is the user actually in portal material?
		if (blk.getTypeId() == plugin.portaltype || blk.getTypeId() == 9 || blk.getTypeId() == 76)
		{
		    // Find nearby warp.
			int w = plugin.getWarpFromLocation(event.getTo().getWorld().getName(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
			if (w == -1)
				return;

            if (!plugin.warpLocations.get(w).isUsable(plugin.cooldown))
            {
                return;
            }
            
            // Ownership check
            if (plugin.usepermissions) 
            {
            	if (!plugin.permissions.hasPermission(player, "travelportals.portal.use")) 
            	{
            		player.sendMessage("§4You do not have permission to use portals.");
            		return;
            	}
            	
            	if (!plugin.warpLocations.get(w).getOwner().equals("") && !plugin.warpLocations.get(w).getOwner().equals(player.getName()))
            	{
            		if (!plugin.permissions.hasPermission(player, "travelportals.admin.portal.use")) 
            		{
            			player.sendMessage("§4You do not own this portal, so you cannot use it.");
            			return;
            		}
            	}
            }

            // Complain if this isn't usable
			if (!plugin.warpLocations.get(w).hasDestination())
			{
				if (plugin.usepermissions && (!plugin.permissions.hasPermission(player, "travelportals.command.warp") || (!plugin.warpLocations.get(w).getOwner().equals("") && !plugin.warpLocations.get(w).getOwner().equals(player.getName()))))
				{
					player.sendMessage("§4This portal has no destination.");
				}
				else
				{
					player.sendMessage("§4You need to set this portal's destination first!");
					player.sendMessage("§2See /portal help for more information.");
				}
                plugin.warpLocations.get(w).setLastUsed();
			}
			else // send the user on his way!
			{
				// Find the warp this one points to
				int loc = plugin.getWarp(plugin.warpLocations.get(w).getDestination());

				if (loc == -1)
				{
					player.sendMessage("§4This portal's destination (" + plugin.warpLocations.get(w).getDestination() + ") does not exist.");
					if (!(plugin.permissions.hasPermission(player, "travelportals.command.warp")))
						player.sendMessage("§2See /portal help for more information.");

                    plugin.warpLocations.get(w).setLastUsed();
				}
				else
				{
					
					// Another permissions check...
					if (!plugin.permissions.hasPermission(player, "travelportals.admin.portal.use")) 
					{
						
						if (!plugin.warpLocations.get(w).getOwner().equals("") && !plugin.warpLocations.get(w).getOwner().equals(player.getName()))
						{
							player.sendMessage("§4You do not own the destination portal, and do not have permission to use it.");
							return;
						}
					}
                    int x = plugin.warpLocations.get(loc).getX();
                    int y = plugin.warpLocations.get(loc).getY();
                    int z = plugin.warpLocations.get(loc).getZ();
                    float rotation = 180.0f; // c

                    // Use rotation to place the player correctly.
					int d = plugin.warpLocations.get(loc).getDoorPosition();

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
					else if (player.getWorld().getBlockAt(x + 1, y, z).getTypeId() == plugin.doortype)
                    {
                        rotation = 270.0f;
                        plugin.warpLocations.get(loc).setDoorPosition(1);
                    }
                    else if (player.getWorld().getBlockAt(x, y, z+1).getTypeId() == plugin.doortype)
                    {
                        rotation = 0.0f;
                        plugin.warpLocations.get(loc).setDoorPosition(2);
                    }
                    else if (player.getWorld().getBlockAt(x - 1, y, z).getTypeId() == plugin.doortype)
                    {
                        rotation = 90.0f;
                        plugin.warpLocations.get(loc).setDoorPosition(3);
                    }
                    else if (player.getWorld().getBlockAt(x, y, z-1).getTypeId() == plugin.doortype)
                    {
                        rotation = 180.0f;
                        plugin.warpLocations.get(loc).setDoorPosition(4);
                    }
                    else
                    {
                    	// oh noes :<
                    }

                    // Create the location for the user to warp to
                    Location locy = new Location(player.getWorld(), x + 0.50, y, z + 0.50, rotation, 0);
                    if (plugin.warpLocations.get(loc).getWorld() != null && !plugin.warpLocations.get(loc).getWorld().equals(""))
                    {
                    	World wo = WorldCreator.name(plugin.warpLocations.get(loc).getWorld()).createWorld();
                    	locy.setWorld(wo);
                    }
                    else
                    {
                        locy.setWorld(TravelPortals.server.getWorlds().get(0));
                    }
                    // Pre-load the chunk we're headed for...
                    if (!locy.getBlock().getChunk().isLoaded())
                    	locy.getBlock().getChunk().load();
                    
                    // Warp the user!
                    player.teleport(locy);
                    event.setTo(locy);
                    plugin.warpLocations.get(loc).setLastUsed();
                    plugin.warpLocations.get(w).setLastUsed();
				}
			}
		}
    }

}

