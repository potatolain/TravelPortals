package net.cpprograms.minecraft.General;

import net.cpprograms.minecraft.General.PluginBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Class to handle commands in a nice clean way. 
 * @author cppchriscpp
 *
 */
public class CommandHandler 
{
	
	/**
	 * The plugin to reference when necessary.
	 */
	public PluginBase plugin;
	
	/**
	 * The name of the command. Named from the class of the extension of CommandSet...
	 */
	public String commandName;
	
	/**
	 * The class that has all of the methods we want to run.
	 */
	public CommandSet commands;
	
	
	/**
	 * Default constructor.
	 */
	public CommandHandler() {}
	
	/**
	 * Constructor. Please provide your plugin here. 
	 * @param plugin The plugin.
	 * @param commandClass The class to get our methods from.
	 */
	@SuppressWarnings("rawtypes")
	public CommandHandler(PluginBase plugin, Class commandClass)
	{
		
		this.plugin = plugin;
		try {
			this.commands = (CommandSet)(commandClass.newInstance());
			this.commands.setPlugin(plugin);
		} catch (InstantiationException e) {
			plugin.logWarning("Error enabling plugin " + plugin.getPluginName() + " InstanciationException while adding plugin to CommandSet");
			plugin.logWarning("Commands may not work right!");
			if (plugin.isDebugging())
				e.printStackTrace();
		} catch (IllegalAccessException e) {
			plugin.logWarning("Error enabling plugin " + plugin.getPluginName() + " IllegalAccessException while adding plugin to CommandSet");
			plugin.logWarning("Commands may not work right!");
			if (plugin.isDebugging())
				e.printStackTrace();
		}
		
		// Try to get the method name from the class...
		if (commandClass.getName().endsWith("CommandSet"))
		{
			this.commandName = commandClass.getSimpleName().substring(0,commandClass.getSimpleName().length() - 10);
		}
	}
	
	/**
	 * Handle a command passed in from a plugin.
	 * @param sender The entity (player or otherwise) that send the command.
	 * @param command The command sent.
	 * @param label The label of the command. 
	 * @param args The arguments passed in.
	 * @return True if the command was handled; false otherwise.
	 */
	public boolean HandleCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (plugin != null && commands != null && command.getName().equalsIgnoreCase(commandName))
		{
			if (args.length == 0)
				return commands.noParams(sender);
			
			String[] args_nocommand = new String[args.length - 1];
			if (args_nocommand.length > 0)
				System.arraycopy(args, 1, args_nocommand, 0, args.length-1);
			
			try 
			{				
				Method tocall = commands.getClass().getMethod(args[0].toLowerCase(), new Class[] { CommandSender.class, String[].class });
				tocall.invoke(commands, new Object[] { sender, args_nocommand });
			}
			catch (SecurityException e)
			{
				commands.internalError(sender, command.getName(), args, e);
			}
			catch (NoSuchMethodException e)
			{
				commands.noSuchMethod(sender, args[0], args_nocommand);
			}
			catch (InvocationTargetException e)
			{
				return commands.internalError(sender, command.getName(), args, e);
			}
			catch (IllegalAccessException e)
			{
				// This happens when a user tries to use a private method. It doesn't exist. ;)
				return commands.noSuchMethod(sender, args[0], args_nocommand);
			}
			return true;
		}
		return false;
	}
}