package com.ancientrelics.plugin.config;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.command.CommandSender;

/**
 * Retrieves and formats messages from messages.yml, applying the
 * configured prefix and placeholder replacement.
 */
public class MessageManager {

    private final AncientRelicsPlugin plugin;

    public MessageManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public String get(String path) {
        String raw = plugin.getConfigManager().getMessages().getString(path, path);
        return ColorUtil.color(raw);
    }

    public String getPrefixed(String path) {
        String prefix = plugin.getConfigManager().getMessages().getString("prefix", "");
        return ColorUtil.color(prefix) + get(path);
    }

    public void send(CommandSender sender, String path) {
        sender.sendMessage(getPrefixed(path));
    }

    public void send(CommandSender sender, String path, String placeholder, String value) {
        String message = getPrefixed(path).replace(placeholder, value);
        sender.sendMessage(message);
    }

    public String format(String path, String... placeholdersAndValues) {
        String message = getPrefixed(path);
        for (int i = 0; i + 1 < placeholdersAndValues.length; i += 2) {
            message = message.replace(placeholdersAndValues[i], placeholdersAndValues[i + 1]);
        }
        return message;
    }
}
