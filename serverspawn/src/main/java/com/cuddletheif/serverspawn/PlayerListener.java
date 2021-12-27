package com.cuddletheif.serverspawn;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Listens for commands for player joining, hitting void, etc
 */
public class PlayerListener implements Listener{

    private ServerSpawn plugin;

    /**
     * Creates a player listener for the given plugin
     * @param config Config holding the command data
     */
    public PlayerListener(ServerSpawn plugin){
        this.plugin = plugin;
    }


    /**
     * When the player spawns into the server redirect them based on their status and the settings
     * @param e The event triggered
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerSpawnEvent(PlayerSpawnLocationEvent e){
        
        Player player = e.getPlayer();

        //player.hasPlayedBefore()
        if(this.plugin.getFirstSpawn()!=null && !player.hasPlayedBefore()){
            e.setSpawnLocation(this.plugin.getFirstSpawn());
            return;
        }

        // Check if server spawn and spawn there
        this.plugin.getLogger().info("SPAWN:"+(System.currentTimeMillis()-e.getPlayer().getLastPlayed()));
        if(this.plugin.shouldServerSpawn(player))
            e.setSpawnLocation(this.plugin.getServerSpawn());

    }

    /**
     * When the player respawns redirect them based on the settings
     * @param e The event triggered
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        if(!this.plugin.shouldRespawnNat(e.getPlayer()))
            e.setRespawnLocation(this.plugin.getRespawn());
    }

    /**
     * When the player takes damage from the void teleport them if set
     * @param e The event triggered
     */
    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(e.getEntity() instanceof Player && e.getCause().equals(DamageCause.VOID))
            this.plugin.tpToVoidSpawn((Player)e.getEntity());
    }

    /**
     * When the player moves and is currently trying to teleport so the teleport
     * @param e The event triggered
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){

        if(this.plugin.shouldCheckMove(e.getPlayer().getUniqueId()) && e.getFrom().distance(e.getTo())>=1)
            this.plugin.stopPlayer(e.getPlayer());
        
    }

}
