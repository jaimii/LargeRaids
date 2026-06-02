package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.util.CustomEggUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveEggCommand implements CommandExecutor {

    private final CustomEggUtil eggUtil;

    public GiveEggCommand(CustomEggUtil eggUtil) {
        this.eggUtil = eggUtil;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("largeraids.admin.giveegg")) {
            sender.sendMessage(Component.text("You do not have permission to execute this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /" + label + " <player> <variant> [amount]", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        String variant = args[1];
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid amount: " + args[2], NamedTextColor.RED));
                return true;
            }
        }

        ItemStack egg = eggUtil.createCustomSpawnEgg(variant, amount);
        if (egg == null) {
            sender.sendMessage(Component.text("Unknown variant: " + variant, NamedTextColor.RED));
            return true;
        }

        target.getInventory().addItem(egg);
        sender.sendMessage(Component.text("Gave " + amount + "x " + variant + " spawn egg to " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }
}