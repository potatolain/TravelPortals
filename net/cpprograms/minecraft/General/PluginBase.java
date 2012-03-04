package net.cpprograms.minecraft.General;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
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
	 * Whether to automatically load and use the built in configuration stuff. 
	 * Set this to be false in your onLoad before calling super() if you do not want it.
	 */
	boolean useConfig = true;
	
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
		if (useConfig)
			if (!loadConfig())
			{
				this.logSevere("Could not load configuration for " + getPluginName() + "! This may break the plugin!");
			}
			else
			{
				if (getConfig().contains("debug"))
				{
					debugMode = getConfig().getBoolean("debug");
				}
			}
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
        this.logDebug("Debugging mode is active.");
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
	 * @param message The message to log.
	 */
	public void logWarning(String message)
	{
		this.log(message, Level.WARNING);
	}
	
	/**
	 * Log an info message
	 * @param message The message to log.
	 */
	public void logInfo(String message)
	{
		this.log(message, Level.INFO);
	}
	
	/**
	 * Log a severe message
	 * @param message The message to log.
	 */
	public void logSevere(String message)
	{
		this.log(message, Level.SEVERE);
	}
	
	/**
	 * Log a debug message (if debugging is on.)
	 * @param message The message to log.
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
	public String getPluginName()
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
	
	/**
	 * Load our default config.yml, or alternatively, create and load the default one.
	 * @return
	 */
	protected boolean loadConfig()
	{
    	try
    	{
    		if (!getDataFolder().exists())
    			getDataFolder().mkdirs();
    		getConfig().load(new File(getDataFolder(), "config.yml"));
    	} catch (FileNotFoundException e) {
			logInfo("No config file found. Creating a default configuration file: " + getPluginName() + "/config.yml");
			return this.saveDefaultConfiguration();
		} catch (IOException e) {
			logSevere("IOException while loading " + getPluginName() + "'s config file! Check on your config.yml, and make sure that the plugins folder is writable.");
			if (debugMode)
				e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			logSevere("Your configuration file for " + getPluginName() + " is invalid. Double check your syntax. (And remove any tab characters)");
			if (debugMode)
				e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save a default configuration file if ours does not exist.
	 * @return true if the save was successful; false otherwise.
	 */
	protected boolean saveDefaultConfiguration()
	{
		try
		{
			File conf = new File(this.getDataFolder(), "config.yml");
			
			InputStream is = this.getClass().getResourceAsStream("/config.yml");
			if (!conf.exists())
				conf.createNewFile();
			OutputStream os = new FileOutputStream(conf);
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				os.write(buf, 0, len);

			is.close();
			os.close();
		} 
		catch (IOException e) 
		{
			logSevere("Could not save default config.yml file! Check the plugin's data directory!");
			if (debugMode)
				e.printStackTrace();
			return false;
		} 
		catch (NullPointerException e) 
		{
			logSevere("Could not find default config.yml file! Plugin developer: Did you include config.yml in the root of the jar?");
			if (debugMode)
				e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
}

