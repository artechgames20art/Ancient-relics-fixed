package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Reserved hook for active relic abilities triggered by right-click
 * (none currently defined as all 8 relics are passive-only), kept
 * so new relics with active interact-triggered abilities can be
 * added without introducing a new listener.
 */
public class PlayerInteractListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerInteractListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // No active right-click relic abilities defined yet.
    }
}
