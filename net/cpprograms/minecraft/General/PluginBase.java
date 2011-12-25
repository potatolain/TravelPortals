package net.cpprograms.minecraft.General;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin base for all of my plugins. Automates some stuff, and makes it easier to manage for me.
 *
 * @author cppchriscpp
 */
public class PluginBase extends JavaPlugin {
	
	/**
	 * The name of this plugin.
	 */
	String pluginName = "";
	
	/**
	 * The version of this plugin.
	 */
	String pluginVersion = "";
	
	/**
	 * Debugging mode - determines whether to send debugging messages.
	 */
	boolean debugMode = true;
	
	/**
	 * Logging component.
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Permissions handler.
	 */
	public PermissionsHandler permissions;
	
	/**
	 * Handle our commands!
	 */
	public CommandHandler commandHandler;
	
	/**
	 * Constructor. Do some setup stuff.
	 */
	public void onLoad() 
	{	
		// Grab a name and version from the plugin's description file.
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		pluginVersion = pdfFile.getVersion();
		permissions = new PermissionsHandler();
	}
	
	/**
	 * What to do when the plugin is enabled. 
	 * If nothing else, this will show that the plugin was loaded.
	 */
	public void onEnable() 
	{
		commandHandler = new CommandHandler();
		showLoadedMessage();
    }
	
	/**
	 * What to do when the plugin is disabled.
	 */
	public void onDisable()
	{
		showUnloadedMessage();
	}
	
	/**
	 * Show the message that the plugin is loaded.
	 */
	public void showLoadedMessage() 
	{
        this.logInfo( pluginName + " version " + pluginVersion + " is enabled!" );
	}
	
	/**
	 * Show the message when the plugin is unloaded.
	 */
	public void showUnloadedMessage()
	{
		this.logInfo( pluginName + " version " + pluginVersion + " has been disabled.");
	}
	
	
	/**
	 * Log a warning message
	 * @param string message The message to log.
	 */
	public void logWarning(String message)
	{
		this.log(message, Level.WARNING);
	}
	
	/**
	 * Log an info message
	 * @param string message The message to log.
	 */
	public void logInfo(String message)
	{
		this.log(message, Level.INFO);
	}
	
	/**
	 * Log a severe message
	 * @param string message The message to log.
	 */
	public void logSevere(String message)
	{
		this.log(message, Level.SEVERE);
	}
	
	/**
	 * Log a debug message (if debugging is on.)
	 * @param string message The message to log.
	 */
	public void logDebug(String message)
	{
		if (this.debugMode)
			this.log(message, Level.INFO);
	}
	
	/**
	 * Check if we're debugging with this plugin.
	 * @return true if we're debugging; false otherwise.
	 */
	public boolean isDebugging()
	{
		return debugMode;
	}
	
	/**
	 * Log a message
	 * @param string message The message to log.
	 * @param Level level The level of the message.
	 */
	private void log(String message, Level level)
	{
		log.log(level, "[" + pluginName +"] " + message);
	}
	
	/**
	 * Run a command with our CommandSender.
	 * @param sender Our sender; entity or otherwise.
	 * @param command The command being sent.
	 * @param label The label for the command.
	 * @param args The arguments passed in.
	 * @return true if the command is handled; false otherwise.
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return commandHandler.HandleCommand(sender, command, label, args);
	}
	
	/**
	 * Gets the name of this plugin.
	 * @return The name of the plugin.
	 */
	public String getName()
	{
		return pluginName;
	}
	
	/**
	 * Gets the version of the plugin.
	 * @return The version of the plugin.
	 */
	public String getVersion()
	{
		return pluginVersion;
	}
	
	
}

