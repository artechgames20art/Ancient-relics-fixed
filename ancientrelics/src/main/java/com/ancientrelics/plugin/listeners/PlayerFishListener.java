package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Reserved hook for Water Relic fishing bonuses (e.g. improved
 * catch rates). Kept modular per the required event coverage so a
 * future ability can plug in without touching other listeners.
 */
public class PlayerFishListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerFishListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        // No relic currently modifies fishing outcomes.
    }
}
