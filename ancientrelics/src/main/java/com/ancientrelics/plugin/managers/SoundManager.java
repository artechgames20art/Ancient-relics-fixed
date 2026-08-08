package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.Sound;

/**
 * Resolves configurable Sound values from config.yml, falling back
 * to a safe default if the configured name is invalid.
 */
public class SoundManager {

    private final AncientRelicsPlugin plugin;

    public SoundManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Sound getConfiguredSound(String key) {
        String raw = plugin.getConfigManager().getConfig().getString("sounds." + key, "ENTITY_PLAYER_LEVELUP");
        try {
            return Sound.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }
}
