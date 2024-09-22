package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;
import com.solarrabbit.largeraids.util.VersionUtil;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Displays the current wave. This works for large raids and normal raids.
 */
public class ShowCurrentWaveCommand implements CommandExecutor {
    private final LargeRaids plugin;
    private final RaidManager manager;

    public ShowCurrentWaveCommand(LargeRaids plugin) {
        this.plugin = plugin;
        manager = plugin.getRaidManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("sender-player"));
            return false;
        }

        Location location = ((Player) sender).getLocation();
        Optional<LargeRaid> largeRaid = manager.getLargeRaid(location);
        if (largeRaid.isPresent()) {
            sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("view-wave.large-raid"),
                    largeRaid.get().getCurrentWave(), largeRaid.get().getTotalWaves()));
            return true;
        }
        Optional<Raid> raid = manager.getRaid(location);
        if (raid.isPresent()) {
            AbstractRaidWrapper raidWrapper = VersionUtil.getCraftRaidWrapper(raid.get()).getHandle();
            int currentWave = raidWrapper.getGroupsSpawned() == 0 || raidWrapper.isBetweenWaves() ? raidWrapper.getGroupsSpawned() + 1
                    : raidWrapper.getGroupsSpawned();
            if (currentWave > raidWrapper.getTotalGroups())
                sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("view-wave.bonus-wave"),
                        raidWrapper.getTotalGroups()));
            else
                sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("view-wave.normal-raid"),
                        currentWave, raidWrapper.getTotalGroups()));
            return true;
        }
        sender.sendMessage(ChatColor.RED + this.plugin.getMessage("view-wave.no-raid-exists"));
        return true;
    }
}
