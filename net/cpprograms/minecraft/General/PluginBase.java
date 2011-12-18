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
	 * Logging component.
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Constructor. Do some setup stuff.
	 */
	public void onLoad() 
	{	
		// Grab a name and version from the plugin's description file.
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		pluginVersion = pdfFile.getVersion();
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
	 * Log a message
	 * @param string message The message to log.
	 * @param Level level The level of the message.
	 */
	private void log(String message, Level level)
	{
		log.log(level, "[" + pluginName +"] " + message);
	}
	
	
}

