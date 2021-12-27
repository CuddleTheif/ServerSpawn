package com.cuddletheif.serverspawn;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class ServerSpawn extends JavaPlugin
{

    private FileConfiguration config;

    // The text to send when the player teleports to spawn via command
    private String tpText;

    // The text to send when the player starts the spawn command
    private String spawnText;

    // The text to show when the player moves to cancel the spawn tp (If null doesn't cancel on tp)
    private String noMoveText;

    // The text to show when the player teleports from hitting void
    private String voidText;

    // The amount of time from when the player runs the spawn command to when they actually teleport
    private int spawnCommandTime;

    // The amount of time the player must be off the server to be teleported to server spawn on enter
    private int serverSpawnTime;

    // If the respawn should override a player's set bed spawn
    private boolean respawnBedOverride;

    // Spawn locations
    private Location serverSpawn;
    private Location commandSpawn;
    private Location firstSpawn;
    private Location respawn;
    private Map<String, Location> voidSpawns;

    // Map of tasks to players for running the spawn command
    Map<UUID, BukkitTask> spawnCommands = new HashMap<>();

    @Override
    public void onEnable() {

        // Create default config if needed and get the current config
        this.saveDefaultConfig();
        loadConfig();

        // Create the listener for updating spawn points
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    }

    /**
     * Loads all the settings and the spawn locations from the config
     */
    private void loadConfig(){

        FileConfiguration config = this.getConfig();

        // Load the settings
        this.tpText = config.getString("tp-text", null);
        this.spawnText = config.getString("spawn-text", null);
        this.noMoveText = config.getString("no-move", null);
        this.voidText = config.getString("void-text", null);
        this.spawnCommandTime = config.getInt("spawn-time", 0);
        this.serverSpawnTime = config.getInt("server-time", 0);
        this.respawnBedOverride = config.getBoolean("respawn-bed", false);

        // Load any spawn locations
        this.serverSpawn= config.getLocation("server");
        this.commandSpawn = config.getLocation("command");
        this.firstSpawn = config.getLocation("first");
        this.respawn = config.getLocation("respawn");

        // Load the void spawn locations if any
        if(config.contains("void")){
            this.voidSpawns = new HashMap<String, Location>();
            ConfigurationSection locations = config.getConfigurationSection("void");
            for(String world : locations.getValues(false).keySet())
                this.voidSpawns.putIfAbsent(world, locations.getLocation(world));
        }

    }

    /**
     * Handle a command of this plugin
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        
        // If reload command reload the config
        if(command.getName().equals("serverspawnreload")){

            // Reload the config and all values
            this.reloadConfig();
            this.saveDefaultConfig();
            this.loadConfig();

            // Let the sender know it was reloaded
            sender.sendMessage("ServerSpawn Config reloaded!");
            return true;

        }

        // Make sure it's a player
        if(!(sender instanceof Player))
            return false;
        Player player = (Player)sender;
        
        // Check which command it is
        this.getLogger().info("RAN COMMAND:"+command.getName());

        // If spawn command just tp the player to the set spawn
        if(command.getName().equals("spawn")){

            // If spawn text send that as a message first
            if(this.spawnText!=null)
                player.sendMessage(this.spawnText);

            // If spawn time is set start the task
            if(this.spawnCommandTime>0){

                // Run the command after a cooldown
                BukkitTask spawnCommand = Bukkit.getScheduler().runTaskLater(this, () -> {
                    if(this.tpToCommandSpawn(player) && this.tpText!=null)
                    player.sendMessage(this.tpText);
                }, this.spawnCommandTime*20);

                // If the command should stop on move save the task for canceling
                if(this.noMoveText!=null)
                    spawnCommands.put(player.getUniqueId(), spawnCommand);
                
            }
            // If spawn time is not set just teleport the player and if succeed send them the tp text
            else if(this.tpToCommandSpawn(player) && this.tpText!=null)
                player.sendMessage(this.tpText);
            
            return true;
        }
        // If setspawn command check which spawn to set and set it
        else if(command.getName().equals("setspawn")){
            
            // Get the new location, update the config and the class var
            Location loc = player.getLocation();
            switch(args[0]){
                case "command":
                    this.commandSpawn = loc;
                    this.getConfig().set("command", loc);
                    player.sendMessage("Set Command Spawn for the server!");
                    break;
                case "first":
                    this.firstSpawn = loc;
                    this.getConfig().set("first", loc);
                    player.sendMessage("Set First Spawn for the server!");
                    break;
                case "respawn":
                    this.respawn = loc;
                    this.getConfig().set("respawn", loc);
                    player.sendMessage("Set Respawn for the server!");
                    break;
                case "void":

                    // Check to make sure world given exists
                    if(args.length<2){
                        player.sendMessage("Please provide a world to set the void spawn for!");
                        return true;
                    }
                    if(Bukkit.getWorld(args[1])==null){
                        player.sendMessage("Couldn't find world "+args[1]+"!");
                        return true;
                    }

                    // Set the void spawn for that world
                    if(this.voidSpawns==null){
                        this.voidSpawns = new HashMap<>();
                        config.set("void", new MemoryConfiguration());
                    }
                    config.getConfigurationSection("void").set(args[1], loc);
                    player.sendMessage("Set Void Spawn for the world "+args[1]+"!");

                    break;
                default:
                    this.serverSpawn = loc;
                    this.getConfig().set("server", loc);
                    player.sendMessage("Set the Server Spawn for the server!");
                    break;
            }

            // Update the actual config file
            this.saveConfig();

        }

        return true;
    }

    /**
     * Give tab list when command is setspawn
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        List<String> completions = new ArrayList<String>();

        // Only tab complete if setspawn and add the list of server, first, respawn, command, void
        if(command.getName().equals("setspawn") && args.length<=1){
            if(args.length==0 || args[0].trim().equals("")){
                completions.add("server");
                completions.add("first");
                completions.add("respawn");
                completions.add("command");
                completions.add("void");
            }
            else if("server".startsWith(args[0].trim()))
                completions.add("server");
            else if("first".startsWith(args[0].trim()))
                completions.add("first");
            else if("respawn".startsWith(args[0].trim()))
                completions.add("respawn");
            else if("command".startsWith(args[0].trim()))
                completions.add("command");
            else if("void".startsWith(args[0].trim()))
                completions.add("void");
        }

        return completions;
    }

    /**
     * Trys to teleport the player to the command spawn, then server spawn, then respawn, then their bed
     * 
     * @param player The player to attempt to teleport
     * @return If the player was actually teleported
     */
    public boolean tpToCommandSpawn(Player player){
        if(this.commandSpawn!=null){
            player.teleport(this.commandSpawn);
            return true;
        }
        else if(this.serverSpawn!=null){
            player.teleport(this.serverSpawn);
            return true;
        }
        else if(this.respawn!=null){
            player.teleport(this.respawn);
            return true;
        }
        else if(player.getBedSpawnLocation()!=null){
            player.teleport(player.getBedSpawnLocation());
            return true;
        }
        return false;
    }

    /**
     * Trys to teleport the player to the void spawn of the world they are in
     * 
     * @param player The player to attempt to teleport
     * @return If the player was actually teleported
     */
    public boolean tpToVoidSpawn(Player player){
        
        if(this.voidSpawns!=null){
            Location voidSpawn = this.voidSpawns.get(player.getWorld().getName());
            if(voidSpawn!=null){
                player.teleport(voidSpawn);
                if(this.voidText!=null)
                    player.sendMessage(this.voidText);
                return true;
            }
        }

        return false;

    }

    /**
     * Checks if the player should respawn naturally
     * 
     * @param player
     * @return
     */
    public boolean shouldRespawnNat(Player player){
        return this.respawn==null || (!this.respawnBedOverride && player.getBedSpawnLocation()!=null);
    }

    /**
     * Get the respawn location if any
     * @return the respawn location (null if none)
     */
    public Location getRespawn(){
        return this.respawn;
    }

    /**
     * Get the server spawn location if any
     * @return the server spawn location (null if none)
     */
    public Location getServerSpawn(){
        return this.serverSpawn;
    }

    /**
     * Get the first spawn location if any
     * @return the first spawn location (null if none)
     */
    public Location getFirstSpawn(){
        return this.firstSpawn;
    }

    /**
     * If the plugin should check if the player moves when doing the spawn command
     * 
     * @return if the plugin should check player movement
     */
    public boolean shouldCheckMove(UUID player){
        return this.noMoveText!=null && this.spawnCommandTime>0 && spawnCommands.containsKey(player);
    }

    /**
     * Stops the player's spawn command if any and no move is set
     * @param player Player to stop the command of
     */
    public void stopPlayer(Player player){
        if(this.noMoveText!=null){
            BukkitTask spawnCommand = spawnCommands.get(player.getUniqueId());
            if(spawnCommand!=null){
                spawnCommand.cancel();
                if(this.noMoveText!="")
                    player.sendMessage(this.noMoveText);
            }
        }
    }

    /**
     * Should the given player be spawned at server spawn if set
     * 
     * @param player the player to check if should be spawned
     * @return if the player should be moved to server spawn
     */
    public boolean shouldServerSpawn(Player player){
        return this.serverSpawn!=null && this.serverSpawnTime>0 && (System.currentTimeMillis()-player.getLastPlayed())/1000>this.serverSpawnTime;
    }

}
