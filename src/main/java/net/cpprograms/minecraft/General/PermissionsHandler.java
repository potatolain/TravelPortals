package net.cpprograms.minecraft.General;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * Handle permissions stuff, if needed.
 *
 */
public class PermissionsHandler
{
	/**
	 * Whether to actually use this. To make life easier if we allow the user to disable permissions.
	 */
	public boolean usePermissions = true;

	/**
	 * Default constructor. Assumes we want to use permissions.
	 */
	public PermissionsHandler() {}

	/**
	 * Constructor for if we let the user choose to use permissions.
	 * @param useme Whether to use permissions.
	 */
	public PermissionsHandler(boolean useme)
	{
		usePermissions = useme;
	}

	/**
	 * Find out if a player has a permission, using the default if we're not using permissions. 
	 * @param sender The sender.
	 * @param permission The permission.
	 * @param defaultResult What to use if we're not using permissions.
	 * @return true if the user has permission, false otherwise.
	 */
	public boolean hasPermission(CommandSender sender, String permission, boolean defaultResult)
	{
		if (!usePermissions)
			return defaultResult;
		return sender.hasPermission(permission);
	}

	/**
	 * Find out if a user has a permission. 
	 * @param sender The sender.
	 * @param permission The permission
	 * @return true if the user has permission; false otherwise.
	 */
	public boolean hasPermission(CommandSender sender, String permission)
	{
		return hasPermission(sender, permission, true);
	}

	/**
	 * Get the value of a numeric permission
	 * @param sender The player
	 * @param permission The permission
	 * @return The numeric value if it is set, otherwise 0
	 */
	public int getNumVal(CommandSender sender, String permission)
	{
		if (!permission.endsWith("."))
			permission = permission + ".";

		int partsNum = permission.split("\\.").length;
		for(PermissionAttachmentInfo perm: ((Player)sender).getEffectivePermissions()){
			String permString = perm.getPermission();
			if(permString.startsWith(permission)){
				String[] amount = permString.split("\\.");
				if (amount.length == (partsNum +1)) {
					try {
						return Integer.parseInt(amount[amount.length - 1]);
					} catch (Exception ex) {
						// Nothing to do
					}
				}
			}
		}
		return 0;
	}
}