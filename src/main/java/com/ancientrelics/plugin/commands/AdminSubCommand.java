package com.ancientrelics.plugin.commands;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles all /relic admin &lt;action&gt; subcommands, delegating
 * mutation to {@link com.ancientrelics.plugin.managers.RelicAdminService}.
 */
public class AdminSubCommand {

    private final AncientRelicsPlugin plugin;

    public AdminSubCommand(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin <give|remove|respawn|reload|reset|tp>"));
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "give" -> give(sender, args);
            case "remove" -> remove(sender, args);
            case "respawn" -> respawn(sender, args);
            case "reload" -> reload(sender);
            case "reset" -> reset(sender, args);
            case "tp" -> teleport(sender, args);
            case "debug" -> debug(sender);
            default -> sender.sendMessage(ColorUtil.color("&cUnknown admin action."));
        }
    }

    private void give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin.give")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin give <player> <relic>"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        Player online = target.getPlayer();
        if (online == null) {
            plugin.getMessageManager().send(sender, "general.invalid-player");
            return;
        }
        plugin.getRelicAdminService().give(sender, online, args[3].toLowerCase());
    }

    private void remove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin.remove")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin remove <relic>"));
            return;
        }
        plugin.getRelicAdminService().remove(sender, args[2].toLowerCase());
    }

    private void respawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin.respawn")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin respawn <relic>"));
            return;
        }
        plugin.getRelicAdminService().respawn(sender, args[2].toLowerCase());
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("ancientrelics.admin.reload")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        plugin.reloadPlugin();
        plugin.getMessageManager().send(sender, "general.reload-success");
    }

    private void reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin.reset")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin reset <relic>"));
            return;
        }
        plugin.getRelicAdminService().reset(sender, args[2].toLowerCase());
    }

    private void teleport(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ancientrelics.admin.tp")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "general.player-only");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic admin tp <relic>"));
            return;
        }
        plugin.getRelicAdminService().teleport(player, args[2].toLowerCase());
    }

    private void debug(CommandSender sender) {
        sender.sendMessage(ColorUtil.color("&7--- Relic Debug ---"));
        for (Relic relic : plugin.getRelicRegistry().getAll().values()) {
            RelicState state = plugin.getRelicManager().getState(relic.getId()).orElse(null);
            sender.sendMessage(ColorUtil.color("&8" + relic.getId() + ": &7" + (state != null ? state.getStatus() : "N/A")));
        }
    }
}
