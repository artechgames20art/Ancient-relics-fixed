package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controls the SMP relic season start sequence.
 * /relic start begins a one-Minecraft-day countdown and then shows the
 * first relic's mysterious clue as a title for 30 seconds.
 */
public final class SeasonManager {
    private static final long MINECRAFT_DAY_TICKS = 24_000L;
    private static final long CLUE_DURATION_TICKS = 30L * 20L;

    private final AncientRelicsPlugin plugin;
    private BukkitTask countdownTask;
    private boolean started;

    public SeasonManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean start() {
        if (started || countdownTask != null) {
            return false;
        }

        List<Relic> relics = getRelics();
        if (relics.isEmpty()) {
            plugin.getLogger().warning("Cannot start relic season: no relics are loaded.");
            return false;
        }

        started = true;
        countdownTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            countdownTask = null;
            showFirstClue(relics.get(0));
        }, MINECRAFT_DAY_TICKS);

        Bukkit.broadcastMessage(ColorUtil.color("&6&lAncient Relics &8» &fThe ancient season has begun..."));
        return true;
    }

    public boolean stop() {
        if (!started && countdownTask == null) {
            return false;
        }
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        started = false;
        return true;
    }

    public boolean isStarted() {
        return started;
    }

    private void showFirstClue(Relic relic) {
        String clue = buildClue(relic);
        String title = ColorUtil.color("&5&lAN ANCIENT WHISPER");
        String subtitle = ColorUtil.color(clue);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, (int) CLUE_DURATION_TICKS, 10);
        }

        plugin.getLogger().info("Displayed the first mysterious relic clue for " + relic.getId() + ".");
    }

    private String buildClue(Relic relic) {
        String id = relic.getId().toLowerCase();
        return switch (id) {
            case "fire" -> "&cWhere heat outlives the flame, something waits...";
            case "water" -> "&bWhere still waters hide old secrets, look closer...";
            case "earth" -> "&2Beneath stone that remembers, an ancient power stirs...";
            case "wind" -> "&fFollow the place where the air never truly rests...";
            case "storm" -> "&9When the sky grows restless, the old power awakens...";
            case "frost" -> "&bWhere cold refuses to fade, a forgotten relic sleeps...";
            case "light" -> "&eWhere darkness breaks, something ancient may be waiting...";
            case "shadow" -> "&8Where light disappears, an ancient presence watches...";
            default -> "&dAn ancient power has left a trace. Find what the world forgot...";
        };
    }

    private List<Relic> getRelics() {
        List<Relic> relics = new ArrayList<>(plugin.getRelicRegistry().getAll().values());
        relics.sort(Comparator.comparing(Relic::getId));
        return relics;
    }
}
