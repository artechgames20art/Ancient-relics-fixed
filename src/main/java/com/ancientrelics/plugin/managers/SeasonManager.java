package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Controls the relic SMP season lifecycle.
 *
 * /relic start begins a persistent timer. After one complete Minecraft day
 * (24,000 world ticks), the first mysterious relic clue is shown for 30 seconds.
 */
public final class SeasonManager {
    private static final long MINECRAFT_DAY_TICKS = 24_000L;
    private static final int TITLE_DURATION_TICKS = 20 * 30;

    private final AncientRelicsPlugin plugin;
    private final File stateFile;

    private YamlConfiguration state;
    private BukkitTask checkTask;

    private boolean started;
    private boolean firstClueShown;
    private String worldName;
    private long startFullTime;

    public SeasonManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "season.yml");
        load();
    }

    public void start(CommandSender sender) {
        if (started) {
            sender.sendMessage(ColorUtil.color("&cThe relic season has already started."));
            return;
        }

        World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        if (world == null) {
            sender.sendMessage(ColorUtil.color("&cNo loaded world was found."));
            return;
        }

        started = true;
        firstClueShown = false;
        worldName = world.getName();
        startFullTime = world.getFullTime();
        save();
        scheduleCheck();

        sender.sendMessage(ColorUtil.color("&a&lRelic Season Started!"));
        Bukkit.broadcastMessage(ColorUtil.color(
                "&6&lAncient Relics &8» &7Something ancient has awakened..."
        ));
    }

    public void stop(CommandSender sender) {
        if (!started) {
            sender.sendMessage(ColorUtil.color("&cThe relic season is not running."));
            return;
        }

        started = false;
        firstClueShown = false;
        save();
        cancelTask();

        sender.sendMessage(ColorUtil.color("&eThe relic season has been stopped."));
    }

    public boolean isStarted() {
        return started;
    }

    private void scheduleCheck() {
        cancelTask();
        checkTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::checkTimer,
                20L,
                20L
        );
    }

    private void checkTimer() {
        if (!started || firstClueShown) return;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        long elapsed = world.getFullTime() - startFullTime;
        if (elapsed < MINECRAFT_DAY_TICKS) return;

        showFirstClue();
    }

    private void showFirstClue() {
        firstClueShown = true;
        save();
        cancelTask();

        String title = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-title",
                "&6&lAN ANCIENT POWER AWAKENS"
        );
        String subtitle = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-subtitle",
                "&7Ancient stone sleeps where &bwater meets the land&7..."
        );
        String chat = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-chat",
                "&6&lAncient Relics &8» &7A mysterious presence has awakened..."
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    ColorUtil.color(title),
                    ColorUtil.color(subtitle),
                    10,
                    TITLE_DURATION_TICKS,
                    20
            );
        }

        Bukkit.broadcastMessage(ColorUtil.color(chat));
    }

    public void shutdown() {
        cancelTask();
        save();
    }

    private void load() {
        if (!stateFile.exists()) {
            state = new YamlConfiguration();
            return;
        }

        state = YamlConfiguration.loadConfiguration(stateFile);
        started = state.getBoolean("started", false);
        firstClueShown = state.getBoolean("first-clue-shown", false);
        worldName = state.getString("world");
        startFullTime = state.getLong("start-full-time", 0L);

        if (started && !firstClueShown && worldName != null && !worldName.isBlank()) {
            Bukkit.getScheduler().runTask(plugin, this::scheduleCheck);
        }
    }

    private void save() {
        state.set("started", started);
        state.set("first-clue-shown", firstClueShown);
        state.set("world", worldName);
        state.set("start-full-time", startFullTime);

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            state.save(stateFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save season.yml: " + e.getMessage());
        }
    }

    private void cancelTask() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
    }
}
