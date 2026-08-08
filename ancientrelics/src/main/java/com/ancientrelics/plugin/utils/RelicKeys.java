package com.ancientrelics.plugin.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Central holder for the plugin's PersistentDataContainer keys, so
 * relic identification tags are defined in exactly one place.
 */
public final class RelicKeys {

    private static NamespacedKey relicIdKey;

    private RelicKeys() {
    }

    public static void init(Plugin plugin) {
        relicIdKey = new NamespacedKey(plugin, "relic_id");
    }

    public static NamespacedKey relicId() {
        return relicIdKey;
    }
}
