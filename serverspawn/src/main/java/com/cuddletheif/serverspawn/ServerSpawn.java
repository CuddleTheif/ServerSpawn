package com.cuddletheif.serverspawn;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class ServerSpawn extends JavaPlugin
{

    @Override
    public void onEnable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        return new ArrayList<String>();
    }

}
