package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Reserved hook for relic behaviour tied to teleportation (e.g.
 * Shadow Relic short-range blink abilities in future updates).
 * Currently ensures no special handling is required, but keeps the
 * event wired per the required architecture.
 */
public class PlayerTeleportListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerTeleportListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        // No relic currently reacts to teleportation.
    }
}
