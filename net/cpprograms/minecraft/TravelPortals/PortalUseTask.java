package net.cpprograms.minecraft.TravelPortals;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


/**
 * Checks all players for if they are currently using a portal and teleports them if necessary..
 * @author mc_dev
 *
 */
public class PortalUseTask implements Runnable {
    
	/**
	 * The ID of this task, or -1 if it is not set.
	 */
    int taskId = -1;
    
    /**
     * The plugin that we are attached to.
     */
    TravelPortals plugin;
    
    /**
     * Prior portal destinations; cached for quick recall.
     */
    Map<String,Location> lastDestinations = new HashMap<String, Location>();
    
    /**
     * Subclass of this task which follows individual players and checks them.
     * @author mc_dev
     *
     */
    class FollowTask implements Runnable
    {
    	/**
    	 * The PortalUseTask which owns this.
    	 */
        PortalUseTask useTask;
        
        /**
         * The player which is being followed by this.
         */
        String playerName;
        
        /**
         * Which warp we're checking against.
         */
        int w;
        
        /**
         * Constructor for this.
         * @param plugin The PortalUseTask which this is connected to.
         * @param playerName The player we're following.
         * @param w The warp to check for.
         */
        public FollowTask( PortalUseTask plugin, String playerName, int w)
        {
            this.useTask = plugin;
            this.playerName = playerName;
            this.w = w;
        }

        /**
         * What to do when this is run.
         */
        @Override
        public void run() 
        {
            useTask.checkPlayer(playerName, w);
        }
    }
    
    /**
     * Constructor for the PortalUseTask.
     * @param plugin The plugin we're attaching to.
     */
    public PortalUseTask(TravelPortals plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Core repeated checks - iterate over all players and check if they are in a portal they can use.
     * Then check for teleport directly or delay the check 
     */
    @Override
    public void run() 
    {

        if ( this.taskId == -1 ) 
        	return;
        
        Server server = Bukkit.getServer();
        Player[] players = server.getOnlinePlayers();
        int n = 0; // cleanup heuristic (if 0 , can clear map of lastDestinations).
        
        for (Player player : players)
        {
            int w = isInPortal(player);
            String playerName = player.getName();
            if (w == -1)
            {
                lastDestinations.remove(playerName);
                continue;
            }
            WarpLocation portal = plugin.warpLocations.get(w);
            if (!portal.isUsable(plugin.cooldown))
            {
                n++;
                continue;
            }
            String destName = portal.getDestination();
            if (destName == null)
            {
                lastDestinations.remove(playerName);
            } 
            else 
            {
                Location lastTarget = lastDestinations.get(playerName);
                if (lastTarget != null)
                {
                    if (sameLoc(lastTarget, player.getLocation()))
                    {
                        n++;
                        continue;
                    }
                }
                
                int targetW = plugin.getWarp(destName);
                
                if (targetW == -1)
                {
                    lastDestinations.remove(playerName);
                } 
                else 
                {
                    WarpLocation wl = plugin.warpLocations.get(targetW);
                    World world = plugin.getServer().getWorld(wl.getWorld());
                    if (world == null) 
                    	continue;
                    
                    lastDestinations.put(playerName, new Location(world, .5 + wl.getX(), wl.getY(), .5 + wl.getZ()));
                }
            }
            n++;
            
            if (plugin.followTicks > 0)
            {
                // register a simple repeated Task for checking he player then
                server.getScheduler().scheduleSyncDelayedTask( plugin, new FollowTask(this, playerName, w));
            }
            else
            {
            	Location warpTo = plugin.getWarpLocationIfAllowed(player);
            	if (warpTo != null)
                    player.teleport(warpTo);
            }
        }
        if (n == 0) 
        	lastDestinations.clear();
    }
    
    /**
     * Test to see if the two locations passed in are functionally the same.
     * @param l1 The first location.
     * @param l2 The second location.
     * @return True if they are the same; false otherwise.
     */
    boolean sameLoc(Location l1, Location l2) {
        return ( (l1.getWorld().getName().equals(l2.getWorld().getName())) && (l1.getX()==l2.getX())  && (l1.getY()==l2.getY()) && (l1.getZ()==l2.getZ()));
    }

    /**
     * Check if a player is an a portal and has permission, return the "w" portal code.
     * BIT OF CODE CLONING, with adjustments (player.getLocation()), moved isUsable to outside .
     * @param player The player to test.
     * @return true if the player can be teleported from this portal; false otherwise.
     */
    int isInPortal(Player player)
    { 
         // Permissions check
    	if (!plugin.permissions.hasPermission(player, "travelportals.portal.use"))
            return -1;
        
        Location playerLoc = player.getLocation();
        World world = player.getWorld();
        
        // The player that caused this is necessary, as is the block.
        Block blk = world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());

        // Is the user actually in portal material?
        if (blk.getTypeId() == plugin.portaltype)
        {
            // Find nearby warp.
            int w = plugin.getWarpFromLocation(world.getName(),playerLoc.getBlockX(),playerLoc.getBlockY(), playerLoc.getBlockZ());
            
            return w;
            
        }
        return -1;
    }
    
    /**
     * Check if the player is still in the same portal and start tp.
     * @param playerName The name of the player to check.
     * @param w The warp to test.
     */
    void checkPlayer(String playerName, int w) 
    {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if ( player == null ) 
        	return;
        // check if still in same portal
        if (isInPortal(player) == w)
        {
            if ((plugin.warpLocations.get(w).isUsable(plugin.cooldown))) 
            {
            	Location loc = plugin.getWarpLocationIfAllowed(player);
            	if (loc != null)
            		player.teleport(loc);
            }
        }
    }
    
    /**
     * Register this task (convenience)
     * @return true if successful; false otherwise.
     */
    public boolean register()
    {
        if (this.taskId != -1) 
        	this.cancel();
        
        int mainTicks = plugin.mainTicks;
        int followTicks = plugin.followTicks;
        
        if (mainTicks <= 0) 
        	mainTicks = 23;
        
        if (mainTicks <= followTicks) 
        	mainTicks = Math.max(23, followTicks+3);
        
        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, mainTicks, mainTicks);
        return (taskId != -1);
    }
    
    /**
     * Unregister this task (convenience).
     */
    public void cancel()
    {
        if (taskId != -1)
        {
            Bukkit.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
    
    /**
     * Get the ID of this task.
     * @return The id of this task.
     */
    public int getTaskId()
    {
        return taskId;
    }

}