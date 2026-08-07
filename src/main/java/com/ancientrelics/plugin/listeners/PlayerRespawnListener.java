package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Reserved for respawn-time logic (e.g. clearing stale ability
 * state). Currently a no-op hook kept modular so future relic
 * effects tied to respawn can be added without touching other
 * listeners.
 */
public class PlayerRespawnListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerRespawnListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // Intentionally empty: relic ownership already cleared on death.
        // Hook retained for future respawn-based relic mechanics.
    }
}
