package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.relics.Relic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

/**
 * Handles the core "relic drops on death" requirement, and awards a
 * kill credit to a killer's relic if applicable.
 */
public class PlayerDeathListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerDeathListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        Optional<Relic> relicOpt = plugin.getRelicManager().getOwnedRelic(player);
        relicOpt.ifPresent(relic -> plugin.getRelicManager()
                .dropOnDeath(player, relic.getId(), player.getLocation()));

        // Credit the killer's relic with a kill, if the killer owns one.
        var killer = player.getKiller();
        if (killer != null) {
            plugin.getRelicManager().getOwnedRelic(killer).ifPresent(killerRelic ->
                    plugin.getRelicManager().getState(killerRelic.getId())
                            .ifPresent(com.ancientrelics.plugin.models.RelicState::incrementKills));
        }
    }
}
