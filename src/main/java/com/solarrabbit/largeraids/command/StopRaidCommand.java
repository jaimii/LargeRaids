package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRaidCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public StopRaidCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RaidManager listener = plugin.getRaidManager();
        if (args.length >= 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
            	Location location = player.getLocation();
            	Optional<LargeRaid> raid = listener.getLargeRaid(location);
            	if (raid.isPresent()) {
            		raid.get().stopRaid();
            		listener.currentRaids.remove(raid.get());
            		sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("stop-raid.stopped-raid"),
            				location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            	} else
            		sender.sendMessage(ChatColor.RED + String.format(this.plugin.getMessage("stop-raid.no-large-raid"),
            				location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            	return true;
            } else
            	sender.sendMessage(ChatColor.RED + this.plugin.getMessage("stop-raid.player-not-found"));
            return false;
        } else if (sender instanceof Player) {
        	Player player = (Player) sender;
        	Location location = player.getLocation();
        	Optional<LargeRaid> raid = listener.getLargeRaid(location);
        	if (raid.isPresent()) {
        		raid.get().stopRaid();
        		listener.currentRaids.remove(raid.get());
        		sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("stop-raid.stopped-raid"),
        				location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        	} else
        		sender.sendMessage(ChatColor.RED + String.format(this.plugin.getMessage("stop-raid.no-large-raid"),
        				location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        	return true;
        }
        if (!(sender instanceof Player))
        	sender.sendMessage(ChatColor.RED + this.plugin.getMessage("stop-raid.specify-player"));
        return false;
    }

}
