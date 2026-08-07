package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.abilities.impl.WindAbility;
import com.ancientrelics.plugin.relics.Relic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Detects the Wind Relic double-jump trigger: since Paper does not
 * expose a native "jumped while airborne" event, this listens for
 * upward Y velocity changes while airborne via PlayerMoveEvent and
 * delegates the actual jump-availability logic to WindAbility.
 */
public class PlayerMoveListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerMoveListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getY() >= event.getTo().getY()) {
            return; // only interested in upward motion attempts
        }
        if (player.isOnGround() || player.isFlying() || player.getAllowFlight()) {
            return;
        }

        plugin.getRelicManager().getOwnedRelic(player).ifPresent(relic -> {
            if (relic.getAbility() instanceof WindAbility windAbility) {
                windAbility.tryDoubleJump(player);
            }
        });
    }
}
