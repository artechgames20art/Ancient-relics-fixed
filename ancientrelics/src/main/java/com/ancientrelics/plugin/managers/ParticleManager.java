package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.relics.Relic;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

/**
 * Runs a single repeating task that displays each relic owner's
 * configured particle around them, instead of spawning one task per
 * player — keeps overhead flat regardless of relic count.
 */
public class ParticleManager {

    private final AncientRelicsPlugin plugin;
    private BukkitTask task;

    public ParticleManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        long period = plugin.getConfigManager().getConfig().getLong("settings.particle-task-period-ticks", 20L);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, period, period);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Optional<Relic> relicOpt = plugin.getRelicManager().getOwnedRelic(player);
            if (relicOpt.isEmpty()) {
                continue;
            }
            Relic relic = relicOpt.get();
            Particle particle = relic.getParticle();
            if (particle == null) {
                continue;
            }
            Location loc = player.getLocation().add(0, 1.0, 0);
            player.getWorld().spawnParticle(particle, loc, 6, 0.4, 0.6, 0.4, 0.01);
        }
    }
}
