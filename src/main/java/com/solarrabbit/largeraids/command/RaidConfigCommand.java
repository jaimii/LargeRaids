package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
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
 * Configures some settings of the current large raid.
 */
public class RaidConfigCommand implements CommandExecutor {
    private final LargeRaids plugin;
    private final RaidManager manager;

    public RaidConfigCommand(LargeRaids plugin) {
        this.plugin = plugin;
        manager = plugin.getRaidManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("sender-player"));
            return false;
        }

        if (args.length < 1)
            return false;

        Location location = ((Player) sender).getLocation();
        Optional<LargeRaid> largeRaid = manager.getLargeRaid(location);
        if (!largeRaid.isPresent()) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raid-config.no-large-raid"));
            return true;
        }

        switch (args[0]) {
            case "spawn":
                if (args.length == 1) {
                    Location spawn = largeRaid.get().getSpawnLocation();
                    if (spawn == null) {
                        sender.sendMessage(ChatColor.YELLOW + this.plugin.getMessage("raid-config.no-custom-spawn"));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("raid-config.current-custom-spawn"),
                                Math.round(spawn.getX()), Math.round(spawn.getY()), Math.round(spawn.getZ())));
                    }
                    return true;
                }

                if (args.length == 2 && args[1].equals("clear")) {
                    largeRaid.get().setSpawnLocation(null);
                    sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("raid-config.custom-spawn-cleared"));
                    return true;
                }
                if (args.length < 4)
                    return false;

                try {
                    double x = parseDoubleOrRelative(args[1], location, 0);
                    double y = parseDoubleOrRelative(args[2], location, 1);
                    double z = parseDoubleOrRelative(args[3], location, 2);
                    Location spawn = new Location(location.getWorld(), x, y, z);
                    largeRaid.get().setSpawnLocation(spawn);
                    sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("raid-config.custom-spawn-set"),
                            Math.round(spawn.getX()), Math.round(spawn.getY()), Math.round(spawn.getZ())));
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raid-config.invalid-coordinates"));
                    return false;
                }
            case "target":
                if (args.length == 1) {
                    Location target = largeRaid.get().getRaidTarget();
                    if (target == null) {
                        sender.sendMessage(ChatColor.YELLOW + this.plugin.getMessage("raid-config.no-target"));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("raid-config.current-target"),
                                Math.round(target.getX()), Math.round(target.getY()), Math.round(target.getZ()),
                                largeRaid.get().getRaidTargetRadius()));
                    }
                    return true;
                }

                if (args.length == 2 && args[1].equals("clear")) {
                    largeRaid.get().setRaidTarget(null, 0);
                    sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("raid-config.target-cleared"));
                    return true;
                }
                if (args.length < 5)
                    return false;

                double x;
                double y;
                double z;
                try {
                    x = parseDoubleOrRelative(args[1], location, 0);
                    y = parseDoubleOrRelative(args[2], location, 1);
                    z = parseDoubleOrRelative(args[3], location, 2);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raid-config.invalid-coordinates"));
                    return false;
                }

                try {
                    double radius = Double.parseDouble(args[4]);
                    if (radius < 0) {
                        sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raid-config.invalid-radius"));
                        return false;
                    }
                    Location target = new Location(location.getWorld(), x, y, z);
                    largeRaid.get().setRaidTarget(target, radius);
                    sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("raid-config.target-set"),
                            Math.round(target.getX()), Math.round(target.getY()), Math.round(target.getZ()), radius));
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + this.plugin.getMessage("raid-config.invalid-radius"));
                    return false;
                }
            default:
                return false;
        }
    }

    private double parseDoubleOrRelative(String pos, Location loc, int type) {
        if (loc == null || pos.length() == 0 || pos.charAt(0) != '~')
            return Double.parseDouble(pos);
        double relative = pos.length() == 1 ? 0 : Double.parseDouble(pos.substring(1));
        switch (type) {
            case 0:
                return relative + Math.round(loc.getX() * 1000) / 1000D;
            case 1:
                return relative + Math.round(loc.getY() * 1000) / 1000D;
            case 2:
                return relative + Math.round(loc.getZ() * 1000) / 1000D;
            default:
                return 0;
        }
    }
}
