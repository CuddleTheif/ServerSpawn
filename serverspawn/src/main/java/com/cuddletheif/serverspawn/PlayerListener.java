package com.cuddletheif.serverspawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

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

}
