package net.cpprograms.minecraft.TravelPortals;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import org.bukkit.ChatColor;
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
	@Override
	public void setPlugin(PluginBase plugin)
	{
		this.plugin = (TravelPortals) plugin;
	}

	/**
	 * What to do if we're given no parameters...
	 * @param sender The entity responsible for sending the command.
	 */
	@Override
	public boolean noParams(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage; try /portal help for help with portals.");
		return true;
	}

	/**
	 * Incorrect usage... we really just want to show the user the same thing as noParams here.
	 * @param sender The entity responsible for sending the command.
	 * @param method The method sent.
	 * @param args The arguments passed in.
	 */
	@Override
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
	@Override
	public boolean internalError(CommandSender sender, String method, String[] args, Exception e)
	{
		sender.sendMessage(ChatColor.DARK_RED + "An internal error occurred while handling this command.");
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
	@Override
	public boolean help(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!plugin.permissions.hasPermission(player, "travelportals.command.help"))
				return noPermissionForAction(sender);

			player.sendMessage(ChatColor.DARK_AQUA + "-- TravelPortals Help --");

			if (plugin.permissions.hasPermission(player, "travelportals.portal.create"))
			{
				player.sendMessage(ChatColor.BLUE + "Create a portal by surrounding a 2x1 area with " + plugin.strBlocktype);
				player.sendMessage(ChatColor.BLUE + "and a " + plugin.strDoortype + ", then putting a" + plugin.strTorchtype + " at the bottom.");
			}

			if (plugin.permissions.hasPermission(player, "travelportals.command.name"))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal name [name] sets the name of this portal.");

			if (plugin.permissions.hasPermission(player, "travelportals.command.warp"))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal warp [name] sets the portal name to warp to.");

			if (plugin.permissions.hasPermission(player, "travelportals.command.hide"))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal hide [name] hides (or unhides) a portal from the list.");

			if (plugin.permissions.hasPermission(player, "travelportals.command.info"))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal info shows information about named or nearby portal.");

			if (plugin.permissions.hasPermission(player, "travelportals.command.claim"))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal claim claims (or gives up ownership of) a portal.");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.deactivate", player.isOp()))
				player.sendMessage(ChatColor.DARK_GREEN + "/portal deactivate [name] deactivates a portal entirely.");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.renameworld", player.isOp()))
				player.sendMessage(ChatColor.DARK_GREEN + "If you rename a world, use /portal renameworld [oldname] [newname] to redirect existing portals");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.deleteworld"))
				player.sendMessage(ChatColor.DARK_GREEN + "If you delete a world, use /portal deleteworld [name] to delete all portals pointing to it.");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.export", player.isOp()))
				player.sendMessage(ChatColor.DARK_GREEN + "You can export to travelportals.txt with /portal export");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.reimport", player.isOp()))
				player.sendMessage(ChatColor.DARK_GREEN + "You can import portals with /portal reimport [file name]");

			if (plugin.permissions.hasPermission(player, "travelportals.command.list"))
				player.sendMessage(ChatColor.GRAY + "To get a list of existing portals, use the command /portal list.");

		}
		else
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "-- TravelPortals Help --");
			sender.sendMessage(ChatColor.GRAY + "Note: Most commands aren't accessible from the command line.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal info shows information about named or nearby portal.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal hide hides or unhides a named portal.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal deactivate [name] deactivates a portal entirely.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal export exports all known portals to travelportals.txt");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal reimport [file name] imports a list of portals from a txt file");
			sender.sendMessage(ChatColor.DARK_GREEN + "If you rename a world, use /portal renameworld oldname newname to replace it");
			sender.sendMessage(ChatColor.DARK_GREEN + "You can set any portals without worlds with /portal fixworld world");
			sender.sendMessage(ChatColor.GRAY + "To get a list of existing portals, use the command /portal list.");
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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
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
			if (w.hasName() && !w.isHidden())
			{
				tmp = this.plugin.getWarp(w.getDestination());
				if (tmp == -1)
					allp.put(w.getName(), ChatColor.RED + "" + w.getDestination());
				else if (plugin.warpLocations.get(tmp).isHidden())
					allp.put(w.getName(), ChatColor.BLUE + "?????");
				else
					allp.put(w.getName(), w.getDestination());
			}


		// No portals! :(
		if (allp.size() == 0)
		{
			sender.sendMessage(ChatColor.DARK_RED + "There are no visible portals to list!");
			return true;
		}

		// Output this information to the user now!
		sender.sendMessage(ChatColor.DARK_GREEN + "TravelPortals (Page " + pn + "/" + ((allp.size()/8) + (allp.size()%8>0?1:0)) + ")");
		sender.sendMessage(ChatColor.DARK_AQUA + "---------------------------------------------------");

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
			cl += ChatColor.WHITE + " --> " + ChatColor.DARK_AQUA + substring(dest, (int)(MinecraftFontWidthCalculator.getMaxStringWidth()/(2.2)));
			sender.sendMessage(ChatColor.DARK_AQUA + cl);
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
			player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
			player.sendMessage(ChatColor.DARK_RED + "You have to include a name for the location!");
		else if (this.plugin.getWarp(args[0]) != -1) // Is this name already taken?
			player.sendMessage(ChatColor.DARK_RED + "There is already a portal named " + args[0] + ". Please pick another name.");
		else
		{
			// Check to make sure the user is actually near a portal.
			int loc = this.plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());


			if (loc == -1)
				player.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be within one block of the portal.)");
			else
			{
				// Ownership check
				if (plugin.usepermissions)
				{
					if (!this.plugin.warpLocations.get(loc).getOwner().equals("") && !this.plugin.warpLocations.get(loc).getOwner().equals(player.getName())) 
					{
						if (!plugin.permissions.hasPermission(player, "travelportals.admin.command.name"))
						{
							player.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and cannot change its name.");
							return true;
						}
					}
				}
				if (this.plugin.warpLocations.get(loc).getDestination() == args[0])
				{
					player.sendMessage(ChatColor.DARK_RED + "You cannot set a portal to warp to itself!");
				}
				else
				{
					this.plugin.warpLocations.get(loc).setName(args[0]);
					this.plugin.savedata();
					player.sendMessage(ChatColor.DARK_GREEN + "This portal is now known as " + args[0] + ".");
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
			player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		int loc = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if (args.length < 1)
			player.sendMessage(ChatColor.DARK_RED + "You have to include a destination!");
		else if (loc == -1)
			player.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be within one block of a portal.)");
		else
		{
			// Ownership check
			if (plugin.usepermissions)
			{
				if (!this.plugin.warpLocations.get(loc).getOwner().equals("") && !this.plugin.warpLocations.get(loc).getOwner().equals(player.getName())) 
				{
					if (!plugin.permissions.hasPermission(player, "travelportals.admin.command.warp"))
					{
						player.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and cannot change its destination.");
						return true;
					}
				}
			}

			if (this.plugin.warpLocations.get(loc).getName() == args[0])
			{
				player.sendMessage(ChatColor.DARK_RED + "You cannot set a portal to warp to itself!");
			}
			else
			{
				this.plugin.warpLocations.get(loc).setDestination(args[0]);
				this.plugin.savedata();
				player.sendMessage(ChatColor.DARK_GREEN + "This portal now points to " + args[0] + ".");
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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You have to include portal name!");
			return true;
		}
		int w = this.plugin.getWarp(args[0]);
		if (w == -1) // Is this name already taken?
			sender.sendMessage(ChatColor.DARK_RED + "That portal does not exist!");
		else
		{
			// Ownership check
			if (sender instanceof Player && plugin.usepermissions)
			{
				if (!this.plugin.warpLocations.get(w).getOwner().equals("") && !this.plugin.warpLocations.get(w).getOwner().equals(sender.getName())) 
				{
					if (!this.plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.hide"))
					{
						sender.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and thus you cannot hide it.");
						return true;
					}
				}
			}
			this.plugin.warpLocations.get(w).setHidden(!plugin.warpLocations.get(w).isHidden());
			if (plugin.warpLocations.get(w).isHidden())
				sender.sendMessage(ChatColor.DARK_AQUA + "Warp " + args[0] + " has been hidden.");
			else
				sender.sendMessage(ChatColor.DARK_AQUA + "Warp " + args[0] + " has been unhidden.");
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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		this.plugin.dumpPortalList();
		sender.sendMessage(ChatColor.DARK_AQUA + "Portal list dumped to travelportals.txt.");
		return true;
	}

	/**
	 * Import a txt-formatted list of portals, likey you'd export with the export function.
	 * @param sender User/console sending the message.
	 * @param args Command line arguments.
	 * @return true if handled; false otherwise.
	 */
	public boolean reimport(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && plugin.usepermissions && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.reimport"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You must provide a file to import from");
			return true;
		}

		File file = new File(plugin.getDataFolder(), args[0]);
		if (!file.exists())
		{
			sender.sendMessage(ChatColor.DARK_RED + "File "+args[0]+" not found!");
			return true;
		}

		if (args.length != 2 || !args[1].equals("confirm"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "This will COMPLETELY ERASE your existing portals!");
			sender.sendMessage(ChatColor.DARK_RED + "If you are sure, type /portal reimport "+args[0]+" confirm");
			return true;
		}
		plugin.importPortalList(file);
		sender.sendMessage(ChatColor.DARK_AQUA + "Portals imported successfully. "+plugin.warpLocations.size()+" warps imported");

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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You must provide the name of the portal.");
			return true;
		}

		int w = plugin.getWarp(args[0]);
		if (w == -1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "There is no portal with the name \"" + args[0] + "\"");
			return true;
		}
		this.plugin.warpLocations.remove(w);
		this.plugin.savedata();
		sender.sendMessage(ChatColor.DARK_GREEN + "You have successfully removed the portal named \"" + args[0] + "\"");
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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}
		int w = -1;
		if (args.length > 0)
		{
			w = plugin.getWarp(args[0]);
			if (w == -1) {
				sender.sendMessage(ChatColor.DARK_RED + "There is no portal with that name.");
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
				sender.sendMessage(ChatColor.DARK_RED + "You must provide a portal name, or stand in front of one.");
				return true;
			}
		}

		WarpLocation portal = plugin.warpLocations.get(w);
		String name = portal.getName();
		String dest = portal.getDestination();
		String owner = portal.getOwner();
		String world = portal.getWorld();

		if (world == null || world.isEmpty())
			world = "(Unknown)";

		if (name.isEmpty())
			name = "has no name";
		else {
			name = "is named " + name;
			if (portal.isHidden())
				if (portal.hasAccess(sender))
					name += ChatColor.BLUE + " (hidden)";
				else
					name = "is hidden";
		}

		int m = plugin.getWarp(dest);

		if (m == -1 && !dest.isEmpty())
			dest = "warps to " + ChatColor.RED + dest + ChatColor.DARK_AQUA + " in world " + ChatColor.RED + world;
		else if (dest.isEmpty())
			dest = "has no destination";
		else {
			WarpLocation destination = plugin.warpLocations.get(m);
			dest = "warps to " + dest + ChatColor.DARK_AQUA + " in world " + destination.getWorld();
			if (destination.isHidden())
				if (destination.hasAccess(sender))
					dest += ChatColor.BLUE + " (hidden)";
				else
					dest = "warps to " + ChatColor.BLUE + "?????";
		}

		if (owner.isEmpty())
			owner = "This portal does not have an owner. If is yours, claim it with /portal claim.";
		else
			owner = "It is owned by " + owner + ".";

		sender.sendMessage(ChatColor.DARK_AQUA + "This portal " + name + ChatColor.DARK_AQUA + " and " + dest + ChatColor.DARK_AQUA + ".");
		sender.sendMessage(ChatColor.DARK_AQUA  + owner);

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
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		int w = -1;
		w = plugin.getWarpFromLocation(player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if (w == -1) 
		{
			player.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be within one block of a portal.)");
			return true;
		}

		if (plugin.warpLocations.get(w).getOwner().equals("") || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) 
		{
			if (args.length > 0)
				plugin.warpLocations.get(w).setOwner(args[0]);
			else
				plugin.warpLocations.get(w).setOwner(player.getName());

			plugin.savedata();
			player.sendMessage(ChatColor.DARK_GREEN + "You have successfully claimed this portal"+(args.length>0?" for " + args[0]:"")+"!");

			return true;
		} 
		else 
		{
			if (plugin.warpLocations.get(w).getOwner().equals(player.getName()) || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp())) 
			{
				plugin.warpLocations.get(w).setOwner("");
				player.sendMessage(ChatColor.DARK_GREEN + "This portal no longer has an owner.");
			} 
			else 
			{
				player.sendMessage(ChatColor.DARK_RED + "This portal is already owned by "+plugin.warpLocations.get(w).getOwner()+"!");
				return true;
			}
		}

		return true;

	}

	/**
	 * Rename a world.
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed in (old world name, new world name)
	 * @return true if handled; false otherwise.
	 */
	public boolean renameworld(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.renameworld", false))
			return noPermissionForAction(sender);

		if (args.length < 2)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You need to include the name of the old world and the name of the new world.");
			return true;
		}
		plugin.renameWorld(args[0], args[1]);
		sender.sendMessage(ChatColor.DARK_GREEN + "The world \"" + args[0] + "\" has been renamed to \"" + args[1] + "\".");
		return true;
	}

	/**
	 * Rename a world.
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed in (old world name, new world name)
	 * @return true if handled; false otherwise.
	 */
	public boolean deleteworld(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.renameworld", false))
			return noPermissionForAction(sender);

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You need to include the name of the deleted world.");
			return true;
		}
		if (args.length < 2 || !args[1].equals("confirm"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "This will irreversibly delete all portals linked to \"" + args[0] + "\"!");
			sender.sendMessage(ChatColor.DARK_GREEN + "Are you sure you want to do this?");
			sender.sendMessage(ChatColor.DARK_GREEN + "Type " + ChatColor.DARK_RED + "/portal deleteworld " + args[0] + " confirm " + ChatColor.DARK_GREEN + " to delete.");
			return true;
		}
		plugin.deleteWorld(args[0]);
		sender.sendMessage(ChatColor.DARK_GREEN + "All portals linked to \"" + args[0] + "\" have been deleted.");
		return true;
	}


	/**
	 * Fix all blank world names in portals
	 * @param sender The entity which sent the command.
	 * @param args The arguments passed in.
	 * @return true if handled; false otherwise.
	 */
	public boolean fixworld(CommandSender sender, String[] args)
	{
		if (sender instanceof Player && !plugin.permissions.hasPermission((Player)sender, "travelportals.admin.command.fixworld", false))
			return this.noPermissionForAction(sender);

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You need to include the name of a default world to use.");
			return true;
		}
		plugin.renameWorld("", args[0]);
		sender.sendMessage(ChatColor.DARK_GREEN + "All portals without a saved world now point to world \"" + args[0] + "\"");
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
		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
		return true;
	}


}