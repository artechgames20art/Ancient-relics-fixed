package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;

public final class SeasonManager {
    private static final long MINECRAFT_DAY_TICKS = 24_000L;
    private static final int CLUE_TITLE_TICKS = 20 * 30;

    private final AncientRelicsPlugin plugin;
    private final File stateFile;
    private YamlConfiguration state;
    private BukkitTask task;

    private boolean started;
    private boolean firstClueShown;
    private String worldName;
    private long startFullTime;

    public SeasonManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "season.yml");
        load();
    }

    public void startSeason(CommandSender sender) {
        if (started) {
            sender.sendMessage(ColorUtil.color("&cThe relic season has already started."));
            return;
        }

        World world = Bukkit.getWorlds().stream().findFirst().orElse(null);
        if (world == null) {
            sender.sendMessage(ColorUtil.color("&cNo world is loaded, so the season cannot start."));
            return;
        }

        started = true;
        firstClueShown = false;
        worldName = world.getName();
        startFullTime = world.getFullTime();
        save();

        sender.sendMessage(ColorUtil.color("&aThe Ancient Relic season has started!"));
        Bukkit.broadcastMessage(ColorUtil.color("&6&lAncient Relics &8» &eThe ancient powers have awakened..."));
        scheduleCheck();
    }

    public void stopSeason(CommandSender sender) {
        if (!started) {
            sender.sendMessage(ColorUtil.color("&cThe relic season has not started."));
            return;
        }
        started = false;
        firstClueShown = false;
        save();
        cancelTask();
        sender.sendMessage(ColorUtil.color("&aThe Ancient Relic season has been stopped."));
    }

    private void scheduleCheck() {
        cancelTask();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkForFirstClue, 20L, 20L);
    }

    private void checkForFirstClue() {
        if (!started || firstClueShown) return;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;
        if (world.getFullTime() - startFullTime < MINECRAFT_DAY_TICKS) return;
        showFirstRelicClue();
    }

    private void showFirstRelicClue() {
        firstClueShown = true;
        save();
        cancelTask();

        String title = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-title", "&6&lAN ANCIENT POWER AWAKENS");
        String subtitle = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-subtitle", "&7Ancient stone sleeps where &bwater meets the land&7...");
        String chat = plugin.getConfigManager().getConfig().getString(
                "season.first-relic-chat", "&6&lAncient Relics &8» &7A mysterious presence has awakened...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ColorUtil.color(title), ColorUtil.color(subtitle), 10, CLUE_TITLE_TICKS, 20);
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
        if (started && (worldName == null || worldName.isBlank())) started = false;
        if (started && !firstClueShown) scheduleCheck();
    }

    private void save() {
        state.set("started", started);
        state.set("first-clue-shown", firstClueShown);
        state.set("world", worldName);
        state.set("start-full-time", startFullTime);
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            state.save(stateFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save season.yml: " + ex.getMessage());
        }
    }

    private void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
