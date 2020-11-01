package net.cpprograms.minecraft.TravelPortals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.cpprograms.minecraft.TravelPortals.storage.StorageType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
				player.sendMessage(ChatColor.RED + "/portal deactivate [name] deactivates a portal entirely.");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.renameworld", player.isOp()))
				player.sendMessage(ChatColor.RED + "If you rename a world, use /portal renameworld [oldname] [newname] to redirect existing portals");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.fixworld", player.isOp()))
				sender.sendMessage(ChatColor.RED + "You can set any portals without worlds with /portal fixworld world");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.deleteworld", player.isOp()))
				player.sendMessage(ChatColor.RED + "If you delete a world, use /portal deleteworld [name] to delete all portals pointing to it.");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.export", player.isOp()))
				player.sendMessage(ChatColor.RED + "You can export to travelportals.txt with /portal export");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.reimport", player.isOp()))
				player.sendMessage(ChatColor.RED + "You can import portals with /portal reimport [file name]");

			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.reload", player.isOp()))
				player.sendMessage(ChatColor.RED + "If you want to reload the plugin config use /portal reload");
			/*
			if (plugin.permissions.hasPermission(player, "travelportals.admin.command.convert", player.isOp()))
				sender.sendMessage(ChatColor.RED + "If you want to convert from one storage to another use /portal convert from|to " + StorageType.stringValues());
			*/
			if (plugin.permissions.hasPermission(player, "travelportals.command.list"))
				player.sendMessage(ChatColor.GRAY + "To get a list of existing portals, use the command /portal list.");

		}
		else
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "-- TravelPortals Help --");
			sender.sendMessage(ChatColor.GRAY + "Note: Most commands aren't accessible from the command line.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal info shows information about named or nearby portal.");
			sender.sendMessage(ChatColor.DARK_GREEN + "portal hide hides or unhides a named portal.");
			sender.sendMessage(ChatColor.RED + "portal deactivate [name] deactivates a portal entirely.");
			sender.sendMessage(ChatColor.RED + "portal export exports all known portals to travelportals.txt");
			sender.sendMessage(ChatColor.RED + "portal reimport [file name] imports a list of portals from a txt file");
			sender.sendMessage(ChatColor.RED + "If you rename a world, use /portal renameworld oldname newname to replace it");
			sender.sendMessage(ChatColor.RED + "You can set any portals without worlds with /portal fixworld world");
			sender.sendMessage(ChatColor.RED + "If you delete a world, use /portal deleteworld [name] to delete all portals pointing to it.");
			//sender.sendMessage(ChatColor.RED + "If you want to convert from one storage to another use /portal convert from|to " + StorageType.stringValues());
			sender.sendMessage(ChatColor.RED + "If you want to reload the plugin config use /portal reload");
			sender.sendMessage(ChatColor.GRAY + "To get a list of existing portals, use the command /portal list.");
		}
		return true;
	}

	/**
	 * Command to reload the config
	 * @param sender The entity responsible for sending the command.
	 * @param args The arguments passed in (none needed)
	 * @return true if handled; false otherwise.
	 */
	public boolean reload(CommandSender sender, String[] args)
	{
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.reload", sender.isOp()))
			return noPermissionForAction(sender);

		plugin.getPortalStorage().save(); // Force save before loading it again
		plugin.reloadConfig();
		if (plugin.load()) {
			sender.sendMessage(ChatColor.DARK_GREEN + "Reloaded the config!");
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Failed to reload the config!");
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.command.list"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		String ownerName = null;
		// Get what page to use.
		int pn = 1;
		if (args.length >= 1)
			try
			{
				pn = Integer.parseInt(args[args.length - 1]);
				if (args.length >= 2)
				{
					ownerName = args[0];
				}
			}
			catch (NumberFormatException e)
			{
				ownerName = args[0];
			}

		// Negative page numbers will not fly.
		if (pn < 1) pn = 1;

		boolean showAll = plugin.permissions.hasPermission(sender, "travelportals.command.list.all");

		// This will load all of the portals in alphabetical order
		String finalOwner = plugin.permissions.hasPermission(sender, "travelportals.command.list.others") ? ownerName : null;
		List<WarpLocation> allp = plugin.getPortalStorage().getPortals().values().stream()
				.filter(w -> w.hasName()
						&& (finalOwner == null || finalOwner.equalsIgnoreCase(w.getOwnerName()))
						&& ((showAll && !w.isHidden()) || w.canAccess(sender) || plugin.permissions.hasPermission(sender, "travelportals.admin.portal.see", sender.isOp())))
				.sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
				.collect(Collectors.toList());

		// No portals! :(
		if (allp.isEmpty())
		{
			sender.sendMessage(ChatColor.DARK_RED + "There are no visible portals to list!");
			return true;
		}

		// Output this information to the user now!
		sender.sendMessage(ChatColor.DARK_GREEN + "TravelPortals" + (finalOwner != null ? " by " + finalOwner : "") + " (Page " + pn + "/" + (int) Math.ceil(allp.size() / 8.0) + ")");
		sender.sendMessage(ChatColor.DARK_AQUA + "---------------------------------------------------");

		for (int i = (pn - 1) * 8; i < pn * 8 && i < allp.size(); i++) {
			WarpLocation w = allp.get(i);
			// Get the name, make it fill approx half the given space
			String cl = w.getName();
			if (w.isHidden()) {
				cl = ChatColor.BLUE + cl;
			}
			int maxWidth = (int) (MinecraftFontWidthCalculator.getMaxStringWidth() / 2.2);
			int left = maxWidth - MinecraftFontWidthCalculator.getStringWidth(cl);
			if (left > 0)
				cl += whitespace(left);
			else
				cl = substring(cl, maxWidth);
			String destination = "(no destination)";
			if (!w.getDestination().isEmpty()) {
				WarpLocation dest = plugin.getPortalStorage().getPortal(w.getDestination());
				if (dest != null) {
					if (dest.isHidden()) {
						if (dest.canSee(sender)) {
							destination = ChatColor.BLUE + dest.getName();
						} else {
							destination = ChatColor.BLUE + "(???)";
						}
					} else {
						destination = dest.getName();
					}
				} else {
					destination = ChatColor.RED + w.getDestination();
				}
			}

			// Now make an arrow, and then the destination, with the same padding/trimming from above.
			cl += ChatColor.WHITE + " --> " + ChatColor.DARK_AQUA +
					substring(destination, maxWidth);
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

		if (!plugin.permissions.hasPermission(sender, "travelportals.command.name"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
			sender.sendMessage(ChatColor.DARK_RED + "You have to include a name for the location!");
		else if (plugin.getPortalStorage().getPortal(args[0]) != null) // Is this name already taken?
			sender.sendMessage(ChatColor.DARK_RED + "There is already a portal named " + args[0] + ". Please pick another name.");
		else
		{
			int nameIndex = args.length - 1;
			WarpLocation portal = null;
			if (nameIndex > 0)
				portal = plugin.getPortalStorage().getPortal(args[0]);
			else if (sender instanceof Player)
				portal = getPortal((Player) sender);

			if (portal == null)
				sender.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be within one block of the portal.)");
			else
			{
				// Ownership check
				if (plugin.usepermissions)
				{
					if (sender instanceof Player && !portal.isOwner((Player) sender))
					{
						if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.name"))
						{
							sender.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and cannot change its name.");
							return true;
						}
					}
				}
				if (portal.getName().equals(args[nameIndex]))
				{
					sender.sendMessage(ChatColor.DARK_RED + "The portal was already named " + args[nameIndex] + "!");
				}
				else
				{
					plugin.getPortalStorage().namePortal(portal, args[nameIndex]);
					sender.sendMessage(ChatColor.DARK_GREEN + "This portal is now known as " + args[nameIndex] + ".");
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.command.warp"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}
		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You have to include a destination!");
			return true;
		}

		int nameIndex = args.length - 1;
		WarpLocation portal = null;
		if (nameIndex > 0)
			portal = plugin.getPortalStorage().getPortal(args[0]);
		else if (sender instanceof Player)
			portal = getPortal((Player) sender);

		if (portal == null)
			sender.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be within one block of a portal.)");
		else
		{
			// Ownership check
			if (plugin.usepermissions)
			{
				if (!portal.canAccess(sender))
				{
					if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.warp"))
					{
						sender.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and cannot change its destination.");
						return true;
					}
				}
			}

			if (portal.getName().equals(args[nameIndex]))
			{
				sender.sendMessage(ChatColor.DARK_RED + "You cannot set a portal to warp to itself!");
			}
			else
			{
				WarpLocation destination = plugin.getPortalStorage().getPortal(args[nameIndex]);
				if (destination != null)
				{
					if (!plugin.crossWorldPortals
							&& !destination.getWorld().isEmpty() && !portal.getWorld().equals(destination.getWorld())
							&& !plugin.permissions.hasPermission(sender, "travelportals.command.warp.crossworld", false)
					)
					{
						sender.sendMessage(ChatColor.DARK_RED + "You cannot create portals between worlds.");
						return true;
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "A portal with the name " + args[nameIndex] + " does not exist. Linking anyways.");
				}
				portal.setDestination(args[nameIndex]);
				plugin.getPortalStorage().save(portal);
				sender.sendMessage(ChatColor.DARK_GREEN + "This portal now points to " + args[nameIndex] + ".");
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.command.hide"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		int nameIndex = args.length - 1;
		WarpLocation portal = null;
		if (nameIndex > 0)
			portal = plugin.getPortalStorage().getPortal(args[0]);
		else if (sender instanceof Player)
			portal = getPortal((Player) sender);

		if (portal == null) // Is this name already taken?
			sender.sendMessage(ChatColor.DARK_RED + "No portal found!");
		else
		{
			// Ownership check
			if (sender instanceof Player && plugin.usepermissions)
			{
				if (!portal.canAccess(sender))
				{
					if (!this.plugin.permissions.hasPermission(sender, "travelportals.admin.command.hide"))
					{
						sender.sendMessage(ChatColor.DARK_RED + "You do not own this portal, and thus you cannot hide it.");
						return true;
					}
				}
			}
			portal.setHidden(!portal.isHidden());
			if (portal.isHidden())
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.export", sender.isOp()))
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.reimport", sender.isOp()))
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
		sender.sendMessage(ChatColor.DARK_AQUA + "Portals imported successfully. "+plugin.getPortalStorage().getPortals().size()+" warps imported");

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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.deactivate", sender.isOp()))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You must provide the name of the portal.");
			return true;
		}

		WarpLocation portal = null;
		if (args.length > 0)
			portal = plugin.getPortalStorage().getPortal(args[0]);
		else if (sender instanceof Player)
			portal = getPortal((Player) sender);

		if (portal == null)
		{
			sender.sendMessage(ChatColor.DARK_RED + "No portal found.");
			return true;
		}
		this.plugin.getPortalStorage().removePortal(portal);
		this.plugin.getPortalStorage().save(portal);
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.command.info"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		WarpLocation portal = null;
		if (args.length > 0)
			portal = plugin.getPortalStorage().getPortal(args[0]);
		else if (sender instanceof Player)
			portal = getPortal((Player) sender);

		if (portal == null)
		{
			sender.sendMessage(ChatColor.DARK_RED + "No portal found.");
			return true;
		}

		String name = portal.getName();
		String dest = portal.getDestination();
		String owner = portal.getOwnerName();
		String world = portal.getWorld();

		if (world == null || world.isEmpty())
			world = "(Unknown)";

		if (name.isEmpty())
			name = "has no name";
		else {
			name = "is named " + name;
			if (portal.isHidden())
				if (portal.canAccess(sender))
					name += ChatColor.BLUE + " (hidden)";
				else
					name = "is hidden";
		}

		WarpLocation destination = dest.isEmpty() ? null : plugin.getPortalStorage().getPortal(dest);

		if (destination == null && !dest.isEmpty())
			dest = "warps to " + ChatColor.RED + dest + ChatColor.DARK_AQUA + " in world " + ChatColor.RED + world;
		else if (dest.isEmpty())
			dest = "has no destination";
		else {
			dest = "warps to " + dest + ChatColor.DARK_AQUA + " in world " + destination.getWorld();
			if (destination.isHidden())
				if (destination.canAccess(sender))
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

		if (plugin.permissions.hasPermission(sender, "travelportals.command.info.details", sender.isOp())) {
		    sender.sendMessage(ChatColor.DARK_AQUA + "Owner UUID: " + ChatColor.YELLOW + portal.getOwnerId());
			sender.sendMessage(ChatColor.DARK_AQUA + "Portal location: " + ChatColor.YELLOW + portal.getIdentifierString());
			float rotation = 0f;
			if (portal.getDoorPosition() > 0)
			{
				if (portal.getDoorPosition() == 1)
					rotation = 270.0f;
				else if (portal.getDoorPosition() == 2)
					rotation = 0.0f;
				else if (portal.getDoorPosition() == 3)
					rotation = 90.0f;
				else
					rotation = 180.0f;
			}
			sender.sendMessage(ChatColor.DARK_AQUA + "Portal direction: " + ChatColor.YELLOW + portal.getDoorPosition() + "/" + rotation);
			sender.sendMessage(ChatColor.DARK_AQUA + "Destination location: " + ChatColor.YELLOW + (destination != null ? destination.getIdentifierString() : "none"));
		}

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

		WarpLocation portal = getPortal(player);
		if (portal == null)
		{
			player.sendMessage(ChatColor.DARK_RED + "No portal found! (You must be near the portal to claim it)");
			return true;
		}

		if (!portal.hasOwner() || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp()))
		{
			if (args.length > 0) {
				Player newOwner = plugin.getServer().getPlayer(args[0]);
				if (newOwner != null)
					portal.setOwner(newOwner);
				else
					portal.setOwner(args[0]);
			} else
				portal.setOwner(player);

			plugin.getPortalStorage().save(portal);
			player.sendMessage(ChatColor.DARK_GREEN + "You have successfully claimed this portal"+(args.length>0?" for " + args[0]:"")+"!");

			return true;
		}
		else
		{
			if (portal.isOwner(player) || plugin.permissions.hasPermission(player, "travelportals.admin.command.claim", player.isOp()))
			{
				portal.setOwner("");
				player.sendMessage(ChatColor.DARK_GREEN + "This portal no longer has an owner.");
			}
			else
			{
				player.sendMessage(ChatColor.DARK_RED + "This portal is already owned by "+portal.getOwnerName()+"!");
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.renameworld", sender.isOp()))
			return noPermissionForAction(sender);

		if (args.length < 2)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You need to include the name of the old world and the name of the new world.");
			return true;
		}
		plugin.getPortalStorage().renameWorld(args[0], args[1]);
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.renameworld", sender.isOp()))
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
		plugin.getPortalStorage().deleteWorld(args[0]);
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
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.fixworld", sender.isOp()))
			return this.noPermissionForAction(sender);

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED + "You need to include the name of a default world to use.");
			return true;
		}
		plugin.getPortalStorage().renameWorld("", args[0]);
		sender.sendMessage(ChatColor.DARK_GREEN + "All portals without a saved world now point to world \"" + args[0] + "\"");
		return true;
	}

	/**
	 * Convert from one storage option to another
	 * @param sender The entity which sent the command.
	 * @param args The arguments passed in.
	 * @return true if handled; false otherwise.
	 */
	public boolean convert(CommandSender sender, String[] args)
	{
		if (!plugin.permissions.hasPermission(sender, "travelportals.admin.command.convert", sender.isOp()))
			return this.noPermissionForAction(sender);

		if (args.length < 2)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Missing arguments. Correct usage: " + ChatColor.RED + "/portal convert from|to " + StorageType.stringValues());
			return true;
		}

		try {
			StorageType input = StorageType.valueOf(args[1].toUpperCase());
			boolean success = false;
			if ("from".equalsIgnoreCase(args[0])) {
				success = plugin.convertStorage(plugin.createStorage(input), plugin.getPortalStorage());
			} else if ("to".equalsIgnoreCase(args[0])) {
				success = plugin.convertStorage(plugin.getPortalStorage(), plugin.createStorage(input));
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Please either insert " + ChatColor.RED + "from" + ChatColor.DARK_RED + " or " + ChatColor.RED + "to" + ChatColor.DARK_RED + " as a conversion direction!");
				return true;
			}
			if (success) {
				sender.sendMessage(ChatColor.DARK_GREEN + "Converted storage " + args[0].toLowerCase() + " " + input + "!");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Error while trying to convert storage " + args[0].toLowerCase() + " " + input + "! Take a look at the console/logs for more info.");
			}
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + args[1] + ChatColor.DARK_RED + " is not a valid storage type. Valid types are " + ChatColor.RED + StorageType.stringValues());
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
		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
		return true;
	}

	/**
	 * Utility method to get the portal that the sender  wants to edit
	 * @param player The command sender
	 * @return The portal location or null if none was found
	 */
	private WarpLocation getPortal(Player player)
	{
		// Get target block
		WarpLocation portal = plugin.getPortalStorage().getNearbyPortal(player.getTargetBlock(getTransparent(), 5).getLocation(), 1);
		if (portal == null)
			// Check see if the player is just near a portal
			portal = plugin.getPortalStorage().getNearbyPortal(player.getLocation(), 1);
		return portal;
	}

	private Set<Material> getTransparent() {
		Set<Material> set = EnumSet.noneOf(Material.class);
		set.addAll(plugin.doortypes);
		Collections.addAll(set,
				Material.AIR,
				Material.CAVE_AIR,
				Material.VOID_AIR
		);
		return set;
	}


}