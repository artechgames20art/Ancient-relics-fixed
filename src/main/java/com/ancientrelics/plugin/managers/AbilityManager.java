package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.relics.Relic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

/**
 * Runs a single repeating task that ticks the ability of every
 * relic owner online, avoiding per-player scheduled tasks.
 */
public class AbilityManager {

    private final AncientRelicsPlugin plugin;
    private BukkitTask task;

    public AbilityManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        long period = plugin.getConfigManager().getConfig().getLong("settings.ability-check-period-ticks", 20L);
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
            relicOpt.ifPresent(relic -> relic.getAbility().onTick(player));
        }
    }
}
