package com.ancientrelics.plugin.gui.pages;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.gui.GuiHolder;
import com.ancientrelics.plugin.gui.GuiItemBuilder;
import com.ancientrelics.plugin.gui.GuiPage;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Admin control panel: give/remove/reset/respawn/teleport/reload/
 * debug actions. Actual mutation logic lives in RelicManager and
 * AdminCommand; this page only presents the buttons and the click
 * listener wires them to those commands.
 */
public class AdminPage {

    private final AncientRelicsPlugin plugin;

    public AdminPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        GuiHolder holder = new GuiHolder(GuiPage.ADMIN);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.admin", "&8Relic Admin Panel"));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        inv.setItem(10, GuiItemBuilder.of(Material.EMERALD_BLOCK)
                .name("&a&lGive Relic")
                .lore(List.of("&7Use /relic admin give <player> <relic>"))
                .build());

        inv.setItem(11, GuiItemBuilder.of(Material.REDSTONE_BLOCK)
                .name("&c&lRemove Relic")
                .lore(List.of("&7Use /relic admin remove <relic>"))
                .build());

        inv.setItem(12, GuiItemBuilder.of(Material.BARRIER)
                .name("&4&lReset Relic")
                .lore(List.of("&7Use /relic admin reset <relic>"))
                .build());

        inv.setItem(13, GuiItemBuilder.of(Material.ENDER_PEARL)
                .name("&d&lRespawn Relic")
                .lore(List.of("&7Use /relic admin respawn <relic>"))
                .build());

        inv.setItem(14, GuiItemBuilder.of(Material.COMPASS)
                .name("&b&lTeleport To Relic")
                .lore(List.of("&7Use /relic admin tp <relic>"))
                .build());

        inv.setItem(15, GuiItemBuilder.of(Material.BOOK)
                .name("&e&lReload Config")
                .lore(List.of("&7Use /relic admin reload"))
                .build());

        inv.setItem(16, GuiItemBuilder.of(Material.COMMAND_BLOCK)
                .name("&7&lDebug")
                .lore(List.of("&7Shows internal relic state", "&7in the console."))
                .build());

        inv.setItem(22, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
