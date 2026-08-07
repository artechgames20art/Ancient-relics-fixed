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
 * Simple settings page. Currently exposes a toggle placeholder for
 * particle visibility preference (extensible for future per-player
 * settings without touching core code).
 */
public class SettingsPage {

    private final AncientRelicsPlugin plugin;

    public SettingsPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        GuiHolder holder = new GuiHolder(GuiPage.SETTINGS);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.settings", "&8Settings"));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        inv.setItem(13, GuiItemBuilder.of(Material.REDSTONE_TORCH)
                .name("&eParticle Effects")
                .lore(List.of("&7Toggle whether you see your", "&7own relic's particle effects.", "", "&7(Coming soon)"))
                .build());

        inv.setItem(22, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
