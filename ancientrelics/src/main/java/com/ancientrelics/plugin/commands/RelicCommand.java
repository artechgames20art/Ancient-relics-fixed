package com.ancientrelics.plugin.commands;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Handles /relic and its subcommands: info, list, owner, help, and
 * delegates "admin" to {@link AdminSubCommand}.
 */
public class RelicCommand implements CommandExecutor {

    private final AncientRelicsPlugin plugin;
    private final AdminSubCommand adminSubCommand;

    public RelicCommand(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        this.adminSubCommand = new AdminSubCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                plugin.getMessageManager().send(sender, "general.player-only");
                return true;
            }
            plugin.getGuiManager().openHome(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "info" -> handleInfo(sender, args);
            case "list" -> handleList(sender);
            case "owner" -> handleOwner(sender, args);
            case "help" -> handleHelp(sender);
            case "admin" -> adminSubCommand.handle(sender, args);
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            default -> plugin.getMessageManager().send(sender, "general.unknown-command");
        }
        return true;
    }


    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("ancientrelics.admin")) {
            sender.sendMessage(ColorUtil.color("&cYou do not have permission to start the relic season."));
            return;
        }
        if (plugin.getSeasonManager().start()) {
            sender.sendMessage(ColorUtil.color("&aRelic season started. The first clue will appear after one Minecraft day."));
        } else {
            sender.sendMessage(ColorUtil.color("&eThe relic season is already running, or no relics are loaded."));
        }
    }

    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("ancientrelics.admin")) {
            sender.sendMessage(ColorUtil.color("&cYou do not have permission to stop the relic season."));
            return;
        }
        if (plugin.getSeasonManager().stop()) {
            sender.sendMessage(ColorUtil.color("&cRelic season stopped."));
        } else {
            sender.sendMessage(ColorUtil.color("&eRelic season is not running."));
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic info <relic>"));
            return;
        }
        String relicId = args[1].toLowerCase();
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic == null) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return;
        }
        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);
        sender.sendMessage(ColorUtil.color(relic.getDisplayName()));
        sender.sendMessage(ColorUtil.color(relic.getDescription()));
        sender.sendMessage(ColorUtil.color("&7Rarity: " + relic.getRarity().getDisplay()));
        if (state != null) {
            sender.sendMessage(ColorUtil.color("&7Status: &f" + state.getStatus().name()));
            if (state.isOwned()) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(state.getOwnerUuid());
                sender.sendMessage(ColorUtil.color("&7Owner: &a" + owner.getName()));
            }
        }
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(ColorUtil.color("&6&lAncient Relics:"));
        for (Map.Entry<String, Relic> entry : plugin.getRelicRegistry().getAll().entrySet()) {
            RelicState state = plugin.getRelicManager().getState(entry.getKey()).orElse(null);
            String status = state != null ? state.getStatus().name() : "UNKNOWN";
            sender.sendMessage(ColorUtil.color(" &8- " + entry.getValue().getDisplayName() + " &7(" + status + ")"));
        }
    }

    private void handleOwner(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color("&cUsage: /relic owner <relic>"));
            return;
        }
        String relicId = args[1].toLowerCase();
        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (state == null || relic == null) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return;
        }
        if (!state.isOwned()) {
            plugin.getMessageManager().send(sender, "relic.not-owned");
            return;
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer(state.getOwnerUuid());
        sender.sendMessage(ColorUtil.color(relic.getDisplayName() + " &7is owned by &a" + owner.getName()));
    }

    private void handleHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.color("&6&lAncient Relics Help"));
        sender.sendMessage(ColorUtil.color("&e/relic &7- Open the relic GUI"));
        sender.sendMessage(ColorUtil.color("&e/relic info <relic> &7- View relic details"));
        sender.sendMessage(ColorUtil.color("&e/relic list &7- List all relics"));
        sender.sendMessage(ColorUtil.color("&e/relic owner <relic> &7- Show a relic's owner"));
        if (sender.hasPermission("ancientrelics.admin")) {
            sender.sendMessage(ColorUtil.color("&c/relic start|stop &7- Start/stop the relic season"));
            sender.sendMessage(ColorUtil.color("&c/relic admin give|remove|reset|respawn|reload|tp"));
        }
    }
}
