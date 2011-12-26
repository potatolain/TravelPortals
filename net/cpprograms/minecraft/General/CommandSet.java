package net.cpprograms.minecraft.General;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

/**
 * Class to implement commands with. The extending class should be named for the command name that it represents.
 * The class's name should end with CommandSet. For example, a forecast command would be in the file
 * ForecastCommandSet.java. Note that case is ignored for commands.
 * 
 * Subcommands are methods in a class implementing this interface. They should be all lowercase, and are not
 * case sensitive.
 * 
 ** For example, if your plugin has the command "helloworld", and you wanted it to have it do two things:
 ** - Print "hello world" to any user who used the command; ie /helloworld
 ** - Print "hello world" to the entire server when the command "/helloworld global" was used,
 ** You would do something like the following: 
 **
 ** public class HelloWorldCommandSet extends CommandSet {
 ** 	MyHelloWorldPlugin plugin;
 ** 	public void setPlugin(PluginBase _plugin) {
 **			plugin = (MyHelloWorldPlugin) _plugin;
 **		}
 ** 	public boolean noParams(CommandSender sender) {
 ** 		player.sendMessage("hello world");
 ** 	}
 ** 	public boolean global(CommandSender sender, String[] args) {
 **			plugin.getServer().broadcastMessage("hello world! ");
 ** 		sender.sendMessage("You sent in these parameters: "+Arrays.toString(args));
 **		}
 *
 * Just implement the methods, and don't bother implementing onCommand; it's all taken care of!
 * @author cppchriscpp
 *
 */
public abstract class CommandSet 
{
	
	PluginBase plugin;
	
	/**
	 * Override this to store a copy of the plugin in your class.
	 * If you really don't need it, just make it a stub...
	 * @param plugin A PluginBase or one of it's descendants.
	 */
	public void setPlugin(PluginBase plugin) {
		this.plugin = plugin;	
	}
	
	/**
	 * What to do if the user does not send any parameters in. (Override this)
	 * @param sender The entity responsible for the command.
	 * @return true if handled; false otherwise.
	 */
	public boolean noParams(CommandSender sender)
	{
		sender.sendMessage("You did not specify an action. Please specify an action.");
		return true;
	}
	
	/**
	 * Stub for the help method. (Override this)
	 * @param sender The entity that send the command.
	 * @param args Any arguments passed in. 
	 * @return true if handled, false otherwise.
	 */
	public boolean help(CommandSender sender, String[] args)
	{
		sender.sendMessage("No help available for this command.");
		return true;
	}
	
	/**
	 * What to do if an unknown method is called. (Override this)
	 * @param sender The entity responsible for the command.
	 * @param method true if handled; false otherwise.
	 * @param params Any parameters that were passed in.
	 * @return true if handled, false otherwise.
	 */
	public boolean noSuchMethod(CommandSender sender, String method, String[] params)
	{
		sender.sendMessage("The method " + method + " does not exist!");
		return true;
	}
	
	/**
	 * Reports an internal error trying to call a method here. (Not a noSuchMethod exception, but the rest...)
	 * Turn on debugging for more information.
	 * @param sender The CommandSender that sent the command.
	 * @param command The command sent.
	 * @param args The arguments passed in.
	 * @param e The exception being handled by this.
	 * @return true if handled; false otherwise.
	 */
	public boolean internalError(CommandSender sender, String command, String[] args, Exception e)
	{
		sender.sendMessage("An internal error occurred while executing the command " + command);
		plugin.logInfo("An internal error occurred while executing the command " + command + " from " + sender.getName());
		plugin.logDebug("Params: " + Arrays.toString(args));
		plugin.logDebug("Error type: " + e.getClass());
		if (plugin.debugMode)
			e.printStackTrace();
		return true;
	}
	
}