package net.cpprograms.minecraft.TravelPortals;

import java.util.TreeMap;
import java.util.Iterator;

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

    public TravelPortalsPlayerListener(TravelPortals instance) {
        plugin = instance;
    }

    /**
     * Run whenever a player runs a command.
     * @param event The PlayerChatEvent describing it.
     */
    public boolean onPlayerCommand(Player player, String[] split)
    {
    	//String[] split = event.getMessage().split(" ");
    	//Player player = event.getPlayer();
    	// This is something we need to worry about, right?
    	if (split[0].equalsIgnoreCase("/portal") && split.length > 1)
    	{
    		// Help command
    		if (split[1].equalsIgnoreCase("help"))
    		{
				if (!plugin.permissions.hasPermission(player, "travelportals.command.help"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}

				player.sendMessage("§3-- TravelPortals Help --");

				if (plugin.permissions.hasPermission(player, "travelportals.portal.create"))
				{
                    player.sendMessage("§2You can create portals by surrounding a 2x1 block with");
		            player.sendMessage("§2" + plugin.strBlocktype + " and a " + plugin.strDoortype + ", then putting a");
		            player.sendMessage("§2" + plugin.strTorchtype + " at the bottom.");
				}

				if (plugin.permissions.hasPermission(player, "travelportals.command.name"))
	            	player.sendMessage("§2/portal name [name] sets the name of this portal.");

				if (plugin.permissions.hasPermission(player, "travelportals.command.warp"))
	            	player.sendMessage("§2/portal warp [name] sets the portal name to warp to.");

                if (plugin.permissions.hasPermission(player, "travelportals.command.hide"))
                    player.sendMessage("§2/portal hide [name] hides (or unhides) a portal from the list.");

                if (plugin.permissions.hasPermission(player, "travelportals.command.info"))
                    player.sendMessage("§2/portal info shows information about named or nearby portal.");
                
                if (plugin.permissions.hasPermission(player, "travelportals.command.claim"))
                	player.sendMessage("§2/portal claim claims (or gives up ownership of) a portal.");
                
                if (plugin.permissions.hasPermission(player, "travelportals.admin.command.deactivate", player.isOp()))
                    player.sendMessage("§2/portal deactivate [name] deactivates a portal entirely.");

				if (plugin.permissions.hasPermission(player, "travelportals.command.list"))
	            	player.sendMessage("§7To get a list of existing portals, use the command /portal list.");
    		}

    		// List of portals (8 per page)
    		else if (split[1].equalsIgnoreCase("list"))
    		{
    			if (!plugin.permissions.hasPermission(player, "travelportals.command.list"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}

                // Get what page to use.
    	        int pn = 1;
    	        if (split.length == 3)
    	           try
    	           {
    	               pn = Integer.parseInt(split[2]);
    	           }
    	           catch (NumberFormatException e)
    	           {}

                // Negative page numbers will not fly.
    	        if (pn < 1) pn = 1;

                // This will load all of the portals in alphabetical order
				TreeMap<String, String> allp = new TreeMap<String, String>();
				int tmp = -1;
				for (WarpLocation w : plugin.warpLocations)
				    if (w.hasName() && !w.getHidden())
				    {
				        tmp = this.plugin.getWarp(w.getDestination());
				        if (tmp == -1)
				            allp.put(w.getName(), "§c" + w.getDestination());
				        else if (plugin.warpLocations.get(tmp).getHidden())
				            allp.put(w.getName(), "§9?????");
				        else
                            allp.put(w.getName(), w.getDestination());
				    }


                // No portals! :(
				if (allp.size() == 0)
				{
				    player.sendMessage("§4There are no visible portals to list!");
				    return true;
				}

                // Output this information to the user now!
                player.sendMessage("§2TravelPortals (Page " + pn + "/" + ((allp.size()/8) + (allp.size()%8>0?1:0)) + ")");
                player.sendMessage("§3---------------------------------------------------");

                // An iterator for both names and destinations..
                Iterator<String> pnames = allp.keySet().iterator();
                Iterator<String> pdests = allp.values().iterator();

                // Get to the page the user requested..
                for (int j = 0; j < ((pn-1)*8) && pnames.hasNext(); j++)
                {
                    pnames.next();
                    pdests.next();
                }

                // Now, print out all of the portals on this page.
                for (int j = 0; j < 8 && pnames.hasNext(); j++)
                {
                    // Get the name, make it fill approx half the given space
                    String cl = pnames.next();
                    String dest = pdests.next();
                    if (cl.equals(""))
                        cl = "(no name)";
                    if (dest.equals(""))
                        dest = "(no destination)";
                    int left = (int)(MinecraftFontWidthCalculator.getMaxStringWidth()/(2.2)) - MinecraftFontWidthCalculator.getStringWidth(cl);
                    if (left > 0)
                    {
                        cl += whitespace(left);
                    }
                    else
                    {
                        cl = substring(cl, (int)(MinecraftFontWidthCalculator.getMaxStringWidth()/(2.2)));
                    }
                    // Now make an arrow, and then the destination, with the same padding/trimming from above.
                    cl += " §f-->§3 " + substring(dest, (int)(MinecraftFontWidthCalculator.getMaxStringWidth()/(2.2)));
                    player.sendMessage("§3" + cl);
                }


    		}

    		// Set the name of a nearby portal
    		else if (split[1].equalsIgnoreCase("name"))
    		{
    			if (!plugin.permissions.hasPermission(player, "travelportals.command.name"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
    			
    			if (split.length < 3)
    				player.sendMessage("§4You have to include a name for the location!");
    			else if (this.plugin.getWarp(split[2]) != -1) // Is this name already taken?
    				player.sendMessage("§4There is already a portal named " + split[2] + ". Please pick another name.");
    			else
    			{
    				// Check to make sure the user is actually near a portal.
    				int loc = this.plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
    				
    				
    				if (loc == -1)
    					player.sendMessage("§4No portal found! (You must be within one block of the portal.)");
    				else
    				{
    					// Ownership check
    					if (plugin.usepermissions)
    					{
    						if (!this.plugin.warpLocations.get(loc).getOwner().equals("") && !this.plugin.warpLocations.get(loc).getOwner().equals(player.getName())) 
    						{
    							if (!plugin.permissions.hasPermission(player, "travelportals.admin.command.name"))
    							{
    								player.sendMessage("§4You do not own this portal, and cannot change its name.");
    								return true;
    							}
    						}
    					}
    				    if (this.plugin.warpLocations.get(loc).getDestination() == split[2])
    				    {
    				        player.sendMessage("§4You cannot set a portal to warp to itself!");
    				    }
    				    else
    				    {
    					    this.plugin.warpLocations.get(loc).setName(split[2]);
    					    this.plugin.savedata();
    					    player.sendMessage("§2This portal is now known as " + split[2] + ".");
    				    }
    				}
    			}
    		}

    		// set where to warp to
    		else if (split[1].equalsIgnoreCase("warp"))
    		{
    			if (!plugin.permissions.hasPermission(player, "travelportals.command.warp"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
    			
    			int loc = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
    			if (split.length < 3)
    				player.sendMessage("§4You have to include a destination!");
    			else if (loc == -1)
    				player.sendMessage("§4No portal found! (You must be within one block of a portal.)");
    			else
    			{
					// Ownership check
					if (plugin.usepermissions)
					{
						if (!this.plugin.warpLocations.get(loc).getOwner().equals("") && !this.plugin.warpLocations.get(loc).getOwner().equals(player.getName())) 
						{
							if (!plugin.permissions.hasPermission(player, "travelportals.admin.command.warp"))
							{
								player.sendMessage("§4You do not own this portal, and cannot change its destination.");
								return true;
							}
						}
					}
					
    			    if (this.plugin.warpLocations.get(loc).getName() == split[2])
    			    {
                        player.sendMessage("§4You cannot set a portal to warp to itself!");
    			    }
    			    else
    			    {
    				    this.plugin.warpLocations.get(loc).setDestination(split[2]);
    			   	    this.plugin.savedata();
    				    player.sendMessage("§2This portal now points to " + split[2] + ".");
    			    }
    			}
    		}
    		// Hide a portal
    		else if (split[1].equalsIgnoreCase("hide"))
    		{
    			if (!plugin.permissions.hasPermission(player, "travelportals.command.hide"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
    			
    			if (split.length < 3)
    			{
    				player.sendMessage("§4You have to include portal name!");
    				return true;
    			}
    			int w = this.plugin.getWarp(split[2]);
    			if (w == -1) // Is this name already taken?
    				player.sendMessage("§4That portal does not exist!");
    			else
    			{
					// Ownership check
					if (plugin.usepermissions)
					{
						if (!this.plugin.warpLocations.get(w).getOwner().equals("") && !this.plugin.warpLocations.get(w).getOwner().equals(player.getName())) 
						{
							if (!player.hasPermission("travelportals.admin.command.hide"))
							{
								player.sendMessage("§4You do not own this portal, and thus you cannot hide it.");
								return true;
							}
						}
					}
					this.plugin.warpLocations.get(w).setHidden(!plugin.warpLocations.get(w).getHidden());
    				if (plugin.warpLocations.get(w).getHidden())
    					player.sendMessage("§3Warp " + split[2] + " has been hidden.");
    				else
    					player.sendMessage("§3Warp " + split[2] + " has been unhidden.");
    			}
    		}
    		// Manually trigger the export function.
    		else if (split[1].equalsIgnoreCase("export"))
    		{
                if (plugin.usepermissions && player.hasPermission("travelportals.admin.command.export"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
                
                this.plugin.dumpPortalList();
                player.sendMessage("§3Portal list dumped to travelportals.txt.");
    		}
    		// Op-only portal destruction.
    		else if (split[1].equalsIgnoreCase("deactivate"))
    		{
                if (!plugin.permissions.hasPermission(player, "travelportals.admin.command.deactivate"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
                
                else if (!plugin.usepermissions && !player.isOp())
				{
					player.sendMessage("§2Only ops are able to use this command.");
					return true;
				}

                if (split.length < 3)
                {
                    player.sendMessage("§4You must provide the name of the portal.");
                    return true;
                }

                int w = plugin.getWarp(split[2]);
                if (w == -1)
                {
                    player.sendMessage("§4There is no portal with the name \"" + split[2] + "\"");
                    return true;
                }
                this.plugin.warpLocations.remove(w);
                this.plugin.savedata();
                player.sendMessage("§2You have successfully removed the portal named \"" + split[2] + "\"");
    		}
    		// Print out some information about the portal.
    		else if (split[1].equalsIgnoreCase("info"))
    		{
                if (!plugin.permissions.hasPermission(player, "travelportals.command.info"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
                int w = -1;
                if (split.length == 3)
                {
                    w = plugin.getWarp(split[2]);
                    if (w == -1) {
                        player.sendMessage("§4There is no portal with that name.");
                        return true;
                    }
                } else {
                    w = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                    if (w == -1) {
                        player.sendMessage("§4You must provide a portal name, or stand in front of one.");
                        return true;
                    }
                }
                
                String n = plugin.warpLocations.get(w).getName();
                String d = plugin.warpLocations.get(w).getDestination();
                String o = plugin.warpLocations.get(w).getOwner();
                String l = plugin.warpLocations.get(w).getWorld();
                
            	if (n.equals(""))
                    n = "has no name";
                else
                	n = "is named " + n;
            	
                int m = plugin.getWarp(d);
                if (m == -1 && !d.equals(""))
                    d = "warps to §c" + d + "§ in world §c"+l+"§";
                else if (d.equals(""))
                	d = "has no destination";
                else if (plugin.warpLocations.get(m).getHidden())
                    d = "warps to §9?????§";
                else
                    d = "warps to " + d;
                if (o.equals(""))
                	o = "This portal does not have an owner. If is yours, claim it with /portal claim.";
                else
                	o = "It is owned by " + o + ".";
                player.sendMessage("§3This portal " + n + " and " + d + ".");
                player.sendMessage("§3" + o);
               
    		}
    		else if (split[1].equalsIgnoreCase("claim")) 
    		{
    			if (!plugin.permissions.hasPermission(player, "travelportals.command.claim"))
				{
					player.sendMessage("§2You do not have permission to use this command.");
					return true;
				}
    			
	           int w = -1;
	           w = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
               if (w == -1) {
            	   player.sendMessage("§4No portal found! (You must be within one block of a portal.)");
                   return true;
               }
               
               if (plugin.warpLocations.get(w).getOwner().equals("") || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) {
            	   plugin.warpLocations.get(w).setOwner(player.getName());
            	   plugin.savedata();
            	   player.sendMessage("§2You have successfully claimed this portal!");
            	   return true;
               } else {
            	   if (plugin.warpLocations.get(w).getOwner().equals(player.getName()) || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) {
            		   plugin.warpLocations.get(w).setOwner("");
            		   player.sendMessage("§2This portal no longer has an owner.");
            	   } else {
	            	   player.sendMessage("§4This portal is already owned by "+plugin.warpLocations.get(w).getOwner()+"!");
	            	   return true;
            	   }
               }
    			
    		}
    		else // whoops
    		{
    			player.sendMessage("§4Incorrect usage; try /portal help for help with portals.");
    		}
    		return true;
    	}
        else if (split[0].equalsIgnoreCase("/portal"))
        {
            player.sendMessage("§4Incorrect usage; try /portal help for help with portals.");
            return true;
        }
    	return false;
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

    // Thank you tkelly
    /**
     * Cut a string down to fit in a set area using MCFWC
     * @param name The name to cut down to size.
     * @param left The amount of space to make it take up.
     * @return The string cut down to the given size.
     */
    private String substring(String name, int left) {
        while(MinecraftFontWidthCalculator.getStringWidth(name) > left) {
            name = name.substring(0, name.length()-1);
        }
        return name;
    }

    // Thank you again tkelly
    /**
     * Get some whitespace that fills up the desired amount of space.
     * @param length The amount of space that needs to be filled
     * @return A string of spaces that fills the required area.
     */
    private String whitespace(int length) {
        int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");

        StringBuilder ret = new StringBuilder();

        for(int i = 0; i < length; i+=spaceWidth) {
            ret.append(" ");
        }

        return ret.toString();
    }

}

