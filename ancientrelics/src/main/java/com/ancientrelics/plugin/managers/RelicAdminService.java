package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Shared admin operations used by both the /relic admin command and
 * the Admin GUI page, keeping the mutation logic in one place.
 */
public class RelicAdminService {

    private final AncientRelicsPlugin plugin;

    public RelicAdminService(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean give(CommandSender sender, Player target, String relicId) {
        if (!plugin.getRelicRegistry().exists(relicId)) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return false;
        }
        if (plugin.getRelicManager().playerOwnsAnyRelic(target.getUniqueId())) {
            plugin.getMessageManager().send(sender, "relic.target-already-owner");
            return false;
        }
        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);
        if (state != null && state.isOwned()) {
            unbindQuiet(relicId);
        }
        boolean success = plugin.getRelicManager().bind(relicId, target);
        if (success) {
            Relic relic = plugin.getRelicRegistry().get(relicId);
            String msg = plugin.getMessageManager().format("relic.given",
                    "%relic%", ColorUtil.color(relic.getDisplayName()), "%player%", target.getName());
            sender.sendMessage(msg);
        }
        return success;
    }

    public void remove(CommandSender sender, String relicId) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic == null) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return;
        }
        plugin.getRelicManager().unbindSilently(relicId);
        String msg = plugin.getMessageManager().format("relic.removed",
                "%relic%", ColorUtil.color(relic.getDisplayName()));
        sender.sendMessage(msg);
    }

    public void reset(CommandSender sender, String relicId) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic == null) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return;
        }
        plugin.getRelicManager().unbindSilently(relicId);
        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);
        if (state != null) {
            state.setKills(0);
            state.setDeaths(0);
            state.setObtainedDate(0);
        }
        String msg = plugin.getMessageManager().format("relic.reset",
                "%relic%", ColorUtil.color(relic.getDisplayName()));
        sender.sendMessage(msg);
    }

    public void respawn(CommandSender sender, String relicId) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic == null) {
            plugin.getMessageManager().send(sender, "general.invalid-relic");
            return;
        }
        plugin.getRelicManager().respawnRandomly(relicId);
        String msg = plugin.getMessageManager().format("relic.respawned",
                "%relic%", ColorUtil.color(relic.getDisplayName()));
        sender.sendMessage(msg);
    }

    public void teleport(Player admin, String relicId) {
        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);
        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (state == null || relic == null) {
            plugin.getMessageManager().send(admin, "general.invalid-relic");
            return;
        }
        Location target = null;
        if (state.isOwned()) {
            Player owner = Bukkit.getPlayer(state.getOwnerUuid());
            if (owner != null) {
                target = owner.getLocation();
            }
        } else if (state.getDroppedLocation() != null) {
            target = state.getDroppedLocation();
        }
        if (target == null) {
            plugin.getMessageManager().send(admin, "relic.not-owned");
            return;
        }
        admin.teleport(target);
        String msg = plugin.getMessageManager().format("relic.teleported",
                "%relic%", ColorUtil.color(relic.getDisplayName()));
        admin.sendMessage(msg);
    }

    private void unbindQuiet(String relicId) {
        plugin.getRelicManager().unbindSilently(relicId);
    }
}
