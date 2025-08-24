package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Adds a certain entity type to all current raiders' attack list for this wave only.
 */
public class RaiderAttackCommand implements CommandExecutor {
    private final LargeRaids plugin;
    private final RaidManager manager;

    public RaiderAttackCommand(LargeRaids plugin) {
        this.plugin = plugin;
        manager = plugin.getRaidManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3)
            return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("sender-player"));
            return false;
        }

        Location location = ((Player) sender).getLocation();
        Optional<LargeRaid> largeRaid = manager.getLargeRaid(location);
        if (!largeRaid.isPresent()) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.no-large-raid"));
            return true;
        }

        if (largeRaid.get().isLoading()) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.no-raiders"));
            return true;
        }

        int prio = 0;
        try {
            prio = Integer.parseInt(args[0]);
            if (prio < 0) {
                sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.prio-too-low"));
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.invalid-prio"));
            return false;
        }

        if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.thru-walls-boolean"));
            return false;
        }
        boolean thruWalls = Boolean.parseBoolean(args[1]);

        Class<?> entityClass = null;
        try {
            entityClass = Class.forName(args[2]);
        } catch (ClassNotFoundException e) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.class-not-found"));
            return false;
        }

        if (!largeRaid.get().addAttackGoal(prio, !thruWalls, entityClass))
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raider-attack.invalid-entity-class"));
        else
            sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("raider-attack.attack-goal-set"));

        return true;
    }
}
