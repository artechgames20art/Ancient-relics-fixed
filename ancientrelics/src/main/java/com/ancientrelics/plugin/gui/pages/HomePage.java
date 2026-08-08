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
 * The main /relic landing page: navigation buttons to every other
 * page, gated by permission for the admin button.
 */
public class HomePage {

    private final AncientRelicsPlugin plugin;

    public HomePage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        GuiHolder holder = new GuiHolder(GuiPage.HOME);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.home", "&8Ancient Relics"));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        inv.setItem(10, GuiItemBuilder.of(Material.NETHER_STAR)
                .name("&6&lMy Relic")
                .lore(List.of("&7View the relic you currently own,", "&7if any."))
                .build());

        inv.setItem(12, GuiItemBuilder.of(Material.CHEST)
                .name("&e&lAll Relics")
                .lore(List.of("&7Browse every relic in existence", "&7and their current status."))
                .build());

        inv.setItem(14, GuiItemBuilder.of(Material.PLAYER_HEAD)
                .name("&b&lOwners")
                .lore(List.of("&7See which players currently", "&7own a relic."))
                .build());

        inv.setItem(16, GuiItemBuilder.of(Material.BOOK)
                .name("&d&lInformation")
                .lore(List.of("&7Learn about the Ancient Relics", "&7system."))
                .build());

        inv.setItem(22, GuiItemBuilder.of(Material.COMPARATOR)
                .name("&7&lSettings")
                .lore(List.of("&7Personal preferences for the", "&7relic GUI."))
                .build());

        if (player.hasPermission("ancientrelics.admin")) {
            inv.setItem(26, GuiItemBuilder.of(Material.COMMAND_BLOCK)
                    .name("&c&lAdmin Panel")
                    .lore(List.of("&7Manage relics as an", "&7administrator."))
                    .build());
        }

        return inv;
    }
}
