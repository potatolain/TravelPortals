package net.cpprograms.minecraft.TravelPortals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.cpprograms.minecraft.General.CommandSet;
import net.cpprograms.minecraft.General.PluginBase;

/**
 * CommandSet for the portal method.
 * @author cppchriscpp
 *
 */
public class PortalCommandSet extends CommandSet 
{
	/**
	 * Our plugin.
	 */
	TravelPortals plugin;
	
	/**
	 * Set the plugin that we're using. 
	 * @param plugin The plugin to use.
	 */
	public void setPlugin(PluginBase plugin)
	{
		this.plugin = (TravelPortals) plugin;
	}
	
	/**
	 * What to do if we're given no parameters...
	 * @param sender The entity responsible for sending the command.
	 */
	public boolean noParams(CommandSender sender)
	{
        sender.sendMessage("§4Incorrect usage; try /portal help for help with portals.");
		return true;
	}
	
	/**
	 * Incorrect usage... we really just want to show the user the same thing as noParams here.
	 * @param sender The entity responsible for sending the command.
	 * @param method The method sent.
	 * @param args The arguments passed in.
	 */
	public boolean noSuchMethod(CommandSender sender, String method, String[] args)
	{
		return noParams(sender);
	}
	
	/**
	 * Log an internal error, and alert the user.
	 * @param sender The entity responsible for sending the command.
	 * @param method The method the entity tried to use.
	 * @param args The arguments passed in.
	 * @param e The exception that was hit.
	 * @return true if this was handled; false otherwise.
	 */
	public boolean internalError(CommandSender sender, String method, String[] args, Exception e)
	{
		sender.sendMessage("§4An internal error occurred while handling this command.");
		if (!(sender instanceof ConsoleCommandSender))
			plugin.logInfo("An error occurred while handling the command " + method + " from " + sender.getName());

		plugin.logDebug("Arguments passed in were: " + Arrays.toString(args));
		if (plugin.isDebugging())
			e.printStackTrace();
		plugin.logInfo("If you see this continually, please report this bug after turning on debug mode.");
		return true;
	}
	
	/**
	 * Show help for the user/entity.
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed in.
	 * @return true if this was handled; false otherwise.
	 */
	public boolean help(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!plugin.permissions.hasPermission(player, "travelportals.command.help"))
				return noPermissionForAction(sender);
	
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
	
			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.renameworld", false))
				player.sendMessage("§2To rename a world, BLAH");
	        
			if (plugin.permissions.hasPermission(player, "travelportals.command.list"))
	        	player.sendMessage("§7To get a list of existing portals, use the command /portal list.");
				
		}
		else
		{
			sender.sendMessage("§3-- TravelPortals Help --");
			sender.sendMessage("§7Note: You do not have access to most commands from the command line.");
			sender.sendMessage("§2portal info shows information about named or nearby portal.");
			sender.sendMessage("§2portal hide hides or unhides a named portal.");
			sender.sendMessage("§2portal deactivate [name] deactivates a portal entirely.");
			sender.sendMessage("§2portal export exports all known portals to travelportals.txt");
			sender.sendMessage("§2BLAH RENAME WORLD");
			sender.sendMessage("§7To get a list of existing portals, use the command /portal list.");
		}
		return true;
	}
	
	/**
	 * Command to get the list of portals.
	 * @param sender The entity that sent the command.
	 * @param args Any arguments passed in (Page number)
	 * @return true if handled; false otherwise.
	 */
	public boolean list(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.command.list"))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}

        // Get what page to use.
        int pn = 1;
        if (args.length >= 1)
           try
           {
               pn = Integer.parseInt(args[0]);
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
		    sender.sendMessage("§4There are no visible portals to list!");
		    return true;
		}

        // Output this information to the user now!
        sender.sendMessage("§2TravelPortals (Page " + pn + "/" + ((allp.size()/8) + (allp.size()%8>0?1:0)) + ")");
        sender.sendMessage("§3---------------------------------------------------");

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
            sender.sendMessage("§3" + cl);
        }
        return true;
	}
	
	/**
	 * Name an existing portal
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed.
	 * @return true if handled, false otherwise.
	 */
	public boolean name(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
			return notAccessibleFromConsole(sender);
		
		Player player = (Player) sender;
		
		if (!plugin.permissions.hasPermission(player, "travelportals.command.name"))
		{
			player.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
		
		if (args.length < 1)
			player.sendMessage("§4You have to include a name for the location!");
		else if (this.plugin.getWarp(args[0]) != -1) // Is this name already taken?
			player.sendMessage("§4There is already a portal named " + args[0] + ". Please pick another name.");
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
			    if (this.plugin.warpLocations.get(loc).getDestination() == args[0])
			    {
			        player.sendMessage("§4You cannot set a portal to warp to itself!");
			    }
			    else
			    {
				    this.plugin.warpLocations.get(loc).setName(args[0]);
				    this.plugin.savedata();
				    player.sendMessage("§2This portal is now known as " + args[0] + ".");
			    }
			}
		}
		return true;
	}
	
	/**
	 * Set the location for a warp.
	 * @param sender The entity responsible for this command
 	 * @param args The parameters passed in (the name of the portal to warp to.
	 * @return true if this is handled; false otherwise. 
	 */
	public boolean warp(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
			return notAccessibleFromConsole(sender);
		
		Player player = (Player) sender;
		if (!plugin.permissions.hasPermission(player, "travelportals.command.warp"))
		{
			player.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
		
		int loc = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if (args.length < 1)
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
			
		    if (this.plugin.warpLocations.get(loc).getName() == args[0])
		    {
                player.sendMessage("§4You cannot set a portal to warp to itself!");
		    }
		    else
		    {
			    this.plugin.warpLocations.get(loc).setDestination(args[0]);
		   	    this.plugin.savedata();
			    player.sendMessage("§2This portal now points to " + args[0] + ".");
		    }
		}
		return true;
	}
	
	/**
	 * Hide a portal
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed in. (Not used
	 * @return true if handled; false otherwise.
	 */
	public boolean hide(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.command.hide"))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
		
		if (args.length < 1)
		{
			sender.sendMessage("§4You have to include portal name!");
			return true;
		}
		int w = this.plugin.getWarp(args[0]);
		if (w == -1) // Is this name already taken?
			sender.sendMessage("§4That portal does not exist!");
		else
		{
			// Ownership check
			if (sender instanceof Player && plugin.usepermissions)
			{
				if (!this.plugin.warpLocations.get(w).getOwner().equals("") && !this.plugin.warpLocations.get(w).getOwner().equals(sender.getName())) 
				{
					if (!this.plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.hide"))
					{
						sender.sendMessage("§4You do not own this portal, and thus you cannot hide it.");
						return true;
					}
				}
			}
			this.plugin.warpLocations.get(w).setHidden(!plugin.warpLocations.get(w).getHidden());
			if (plugin.warpLocations.get(w).getHidden())
				sender.sendMessage("§3Warp " + args[0] + " has been hidden.");
			else
				sender.sendMessage("§3Warp " + args[0] + " has been unhidden.");
		}
		return true;
	}
	
	/**
	 * Export command - exports the portals to a text file.
	 * @param sender The entity that sent this. 
	 * @param args Arguments (unused)
	 * @return true if handled; false otherwise.
	 */
	public boolean export(CommandSender sender, String[] args)
	{
        if (sender instanceof Player && plugin.usepermissions && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.export"))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
        
        this.plugin.dumpPortalList();
        sender.sendMessage("§3Portal list dumped to travelportals.txt.");
		return true;
	}
	
	/**
	 * Deactivates an existing portal
	 * @param sender The entity responsible for sending the command.
	 * @param args Arguments passed in (name of the portal)
	 * @return true if this is handled; false otherwise.
	 */
	public boolean deactivate(CommandSender sender, String[] args)
	{
        if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.deactivate", sender.isOp()))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}

        if (args.length < 1)
        {
            sender.sendMessage("§4You must provide the name of the portal.");
            return true;
        }

        int w = plugin.getWarp(args[0]);
        if (w == -1)
        {
            sender.sendMessage("§4There is no portal with the name \"" + args[0] + "\"");
            return true;
        }
        this.plugin.warpLocations.remove(w);
        this.plugin.savedata();
        sender.sendMessage("§2You have successfully removed the portal named \"" + args[0] + "\"");
        return true;
	}
	
	/**
	 * Get information about an existing portal.
	 * @param sender The entity that sent the command.
	 * @param args The arguments (name of the portal; optional)
	 * @return true if handled; false otherwise.
	 */
	public boolean info(CommandSender sender, String[] args)
	{
        if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.command.info"))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
        int w = -1;
        if (args.length > 0)
        {
            w = plugin.getWarp(args[0]);
            if (w == -1) {
                sender.sendMessage("§4There is no portal with that name.");
                return true;
            }
        } else {
        	if (!(sender instanceof Player))
        	{
        		sender.sendMessage("You need to provide the name of a portal.");
        		return true;
        	}
        	
            w = plugin.getWarpFromLocation(((Player)sender).getWorld().getName(), ((Player)sender).getLocation().getBlockX(), ((Player)sender).getLocation().getBlockY(), ((Player)sender).getLocation().getBlockZ());
            if (w == -1) {
                sender.sendMessage("§4You must provide a portal name, or stand in front of one.");
                return true;
            }
        }
        
        String n = plugin.warpLocations.get(w).getName();
        String d = plugin.warpLocations.get(w).getDestination();
        String o = plugin.warpLocations.get(w).getOwner();
        String l = plugin.warpLocations.get(w).getWorld();
        if (l == null || l.length() == 0)
        	l = "(Unknown)";
        
    	if (n.equals(""))
            n = "has no name";
        else
        	n = "is named " + n;
    	
        int m = plugin.getWarp(d);
        if (m == -1 && !d.equals(""))
            d = "warps to §c" + d + "§3 in world §c"+l+"§3";
        else if (d.equals(""))
        	d = "has no destination";
        else if (plugin.warpLocations.get(m).getHidden())
            d = "warps to §9?????§";
        else
            d = "warps to " + d + "§3 in world "+l;
        if (o.equals(""))
        	o = "This portal does not have an owner. If is yours, claim it with /portal claim.";
        else
        	o = "It is owned by " + o + ".";
        sender.sendMessage("§3This portal " + n + " and " + d + ".");
        sender.sendMessage("§3" + o);
       
        return true;
	}
	
	/**
	 * Claim a portal for the sender, or a specified person.
	 * @param sender The entity responsible for sending this command.
	 * @param args The name of the person to claim for. (optional)
	 * @return true if handled; false otherwise.
	 */
	public boolean claim(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
			return notAccessibleFromConsole(sender);
		Player player = (Player) sender;
		
		if (!plugin.permissions.hasPermission(player, "travelportals.command.claim"))
		{
			sender.sendMessage("§2You do not have permission to use this command.");
			return true;
		}
		
		int w = -1;
		w = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if (w == -1) 
		{
			player.sendMessage("§4No portal found! (You must be within one block of a portal.)");
		    return true;
		}
		
		if (plugin.warpLocations.get(w).getOwner().equals("") || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) 
		{
			if (args.length > 0)
				plugin.warpLocations.get(w).setOwner(args[0]);
			else
				plugin.warpLocations.get(w).setOwner(player.getName());
			
			plugin.savedata();
			player.sendMessage("§2You have successfully claimed this portal"+(args.length>0?" for " + args[0]:"")+"!");
			
			return true;
		} 
		else 
		{
			if (plugin.warpLocations.get(w).getOwner().equals(player.getName()) || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) 
			{
				plugin.warpLocations.get(w).setOwner("");
				player.sendMessage("§2This portal no longer has an owner.");
			} 
			else 
			{
		    	player.sendMessage("§4This portal is already owned by "+plugin.warpLocations.get(w).getOwner()+"!");
		    	return true;
			}
		}
		
		return true;
		
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
    
    /**
     * Tell the console user that this method is not available from the console, and return true because we handled this.
     * @param sender The entity (console) responsible for sending this request.
     * @return true
     */
    private boolean notAccessibleFromConsole(CommandSender sender)
    {
    	sender.sendMessage("This method is not available from the console.");
    	return true;
    }
    
    /**
     * Tell the user that they do not have permission for this action.
     * @param sender The entity (player) responsible for sending this request.
     * @return true
     */
    private boolean noPermissionForAction(CommandSender sender)
    {
    	sender.sendMessage("§4You do not have permission to use this command.");
    	return true;
    }
	
	
}