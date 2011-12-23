package net.cpprograms.minecraft.General;

import org.bukkit.entity.Player;

/**
 * Handle permissions stuff, if needed.
 * @author cppchriscpp
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
	 * @param player The player.
	 * @param permission The permission.
	 * @param defaultResult What to use if we're not using permissions.
	 * @return true if the user has permission, false otherwise.
	 */
	public boolean hasPermission(Player player, String permission, boolean defaultResult)
	{
		if (!usePermissions)
			return defaultResult;
		return player.hasPermission(permission);
	}
	
	/**
	 * Find out if a user has a permission. 
	 * @param player The player.
	 * @param permission The permission
	 * @return true if the user has permission; false otherwise.
	 */
	public boolean hasPermission(Player player, String permission) 
	{
		return hasPermission(player, permission, true);
	}
}