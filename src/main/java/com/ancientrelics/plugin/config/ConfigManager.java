package com.ancientrelics.plugin.config;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Central manager for all YAML-backed configuration files used by
 * the plugin: config.yml, messages.yml, relics.yml and players.yml.
 * Handles saving default resources, loading, and reload.
 */
public class ConfigManager {

    private final AncientRelicsPlugin plugin;

    private File configFile;
    private File messagesFile;
    private File relicsFile;
    private File playersFile;

    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration relicsConfig;
    private FileConfiguration playersConfig;

    public ConfigManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        configFile = createIfMissing("config.yml");
        messagesFile = createIfMissing("messages.yml");
        relicsFile = createIfMissing("relics.yml");
        playersFile = createIfMissing("players.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        relicsConfig = YamlConfiguration.loadConfiguration(relicsFile);
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    private File createIfMissing(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        relicsConfig = YamlConfiguration.loadConfiguration(relicsFile);
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public void savePlayers() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save players.yml", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getRelicsConfig() {
        return relicsConfig;
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }
}
