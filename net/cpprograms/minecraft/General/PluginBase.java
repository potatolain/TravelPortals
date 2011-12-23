package net.cpprograms.minecraft.General;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	boolean debugMode = false;
	
	/**
	 * Logging component.
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Permissions handler.
	 */
	public PermissionsHandler permissions;
	
	/**
	 * Constructor. Do some setup stuff.
	 */
	public void onLoad() 
	{	
		// Grab a name and version from the plugin's description file.
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		pluginVersion = pdfFile.getVersion();
		this.permissions = new PermissionsHandler();
	}
	
	/**
	 * What to do when the plugin is enabled. 
	 * If nothing else, this will show that the plugin was loaded.
	 */
	public void onEnable() 
	{
		
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
	 * Call the CommandHandler for this plugin...
	 * @return true if command is handled; else false.
	 */
	public boolean OnCommand(/* params */)
	{
		// TODO: call CommandHandler
		return false;
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
			this.log(message, Level.CONFIG);
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
	
	
}

