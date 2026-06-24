package com.elonmuschio.gumball.commands;

import com.elonmuschio.gumball.MachineColor;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class GumballTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gumball.admin")) {
            return List.of();
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options(args), new ArrayList<>());
    }

    private List<String> options(String[] args) {
        if (args.length == 1) {
            return List.of("get", "give", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        }
        if ((args.length == 2 && args[0].equalsIgnoreCase("get"))
                || (args.length == 3 && args[0].equalsIgnoreCase("give"))) {
            return colors();
        }
        return List.of();
    }

    private List<String> colors() {
        List<String> names = new ArrayList<>();
        for (MachineColor color : MachineColor.values()) {
            names.add(color.name());
        }
        return names;
    }
}
