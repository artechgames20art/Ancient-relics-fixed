package com.ancientrelics.plugin.commands;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides tab completion for /relic and its subcommands.
 */
public class RelicTabCompleter implements TabCompleter {

    private final AncientRelicsPlugin plugin;

    private static final List<String> ROOT = List.of("info", "list", "owner", "help", "admin");
    private static final List<String> ADMIN_ACTIONS = List.of(
            "give", "remove", "respawn", "reload", "reset", "tp", "debug");

    public RelicTabCompleter(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(ROOT, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return filter(ADMIN_ACTIONS, args[1]);
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("owner"))) {
            return filter(relicIds(), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            String action = args[1].toLowerCase();
            if (action.equals("give")) {
                return filter(onlinePlayerNames(), args[2]);
            }
            if (action.equals("remove") || action.equals("respawn") || action.equals("reset") || action.equals("tp")) {
                return filter(relicIds(), args[2]);
            }
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("give")) {
            return filter(relicIds(), args[3]);
        }

        return new ArrayList<>();
    }

    private List<String> relicIds() {
        return new ArrayList<>(plugin.getRelicRegistry().getAll().keySet());
    }

    private List<String> onlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private List<String> filter(List<String> options, String input) {
        String lower = input.toLowerCase();
        return options.stream().filter(o -> o.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}
