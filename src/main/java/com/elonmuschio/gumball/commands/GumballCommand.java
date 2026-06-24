package com.elonmuschio.gumball.commands;

import com.elonmuschio.gumball.Gumball;
import com.elonmuschio.gumball.MachineColor;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GumballCommand implements CommandExecutor {
    private final Gumball plugin;

    public GumballCommand(Gumball plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gumball.admin")) {
            sender.sendMessage(plugin.messages().get("no-permission"));
            return true;
        }
        if (args.length == 0) {
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> reload(sender);
            case "get" -> get(sender, args);
            case "give" -> give(sender, args);
            default -> {
            }
        }
        return true;
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("gumball.reload")) {
            sender.sendMessage(plugin.messages().get("no-permission"));
            return;
        }
        plugin.config().reload();
        plugin.messages().reload();
        sender.sendMessage(plugin.messages().get("reloaded"));
    }

    private void get(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gumball.get")) {
            sender.sendMessage(plugin.messages().get("no-permission"));
            return;
        }
        if (!(sender instanceof Player player)) {
            return;
        }
        MachineColor color = parseColor(sender, args, 1);
        if (color == null) {
            return;
        }
        giveMachine(player, color);
    }

    private void give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gumball.give")) {
            sender.sendMessage(plugin.messages().get("no-permission"));
            return;
        }
        if (args.length < 2) {
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.messages().get("player-not-found"));
            return;
        }
        MachineColor color = parseColor(sender, args, 2);
        if (color == null) {
            return;
        }
        giveMachine(target, color);
    }

    private MachineColor parseColor(CommandSender sender, String[] args, int index) {
        if (args.length <= index) {
            return MachineColor.RED;
        }
        MachineColor color = MachineColor.parse(args[index]);
        if (color == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%color%", args[index]);
            sender.sendMessage(plugin.messages().var("invalid-color", placeholders));
        }
        return color;
    }

    private void giveMachine(Player player, MachineColor color) {
        ItemStack machine = plugin.machines().getMachineItem(color);
        if (!player.getInventory().addItem(machine).isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), machine);
        }
    }
}
