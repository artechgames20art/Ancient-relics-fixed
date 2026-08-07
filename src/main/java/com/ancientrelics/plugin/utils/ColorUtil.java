package com.ancientrelics.plugin.utils;

import org.bukkit.ChatColor;

/**
 * Small helper for translating '&' color codes used throughout
 * config/messages/relics YAML files.
 */
public final class ColorUtil {

    private ColorUtil() {
    }

    public static String color(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
