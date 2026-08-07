package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Ensures a PlayerData record exists on join and reapplies relic
 * ability bind effects if the player owns a relic (in case
 * potion-style effects were lost across a restart).
 */
public class PlayerJoinQuitListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerJoinQuitListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        plugin.getRelicManager().getOrCreatePlayerData(player.getUniqueId());
        plugin.getRelicManager().getOwnedRelic(player)
                .ifPresent(relic -> relic.getAbility().onBind(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Persistent effects (potion effects marked ambient/infinite) remain
        // applied via saved player data; nothing to unbind on quit since the
        // player keeps ownership while offline.
    }
}
