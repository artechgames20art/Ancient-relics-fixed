package com.ancientrelics.plugin.gui.pages;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.gui.GuiHolder;
import com.ancientrelics.plugin.gui.GuiItemBuilder;
import com.ancientrelics.plugin.gui.GuiPage;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * Shows a player-head entry for every relic that currently has an
 * owner.
 */
public class OwnersPage {

    private final AncientRelicsPlugin plugin;

    public OwnersPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player viewer) {
        GuiHolder holder = new GuiHolder(GuiPage.OWNERS);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.owners", "&8Relic Owners"));
        int size = plugin.getConfigManager().getConfig().getInt("gui.inventory-size", 54);
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        int slot = 0;
        for (Relic relic : plugin.getRelicRegistry().getAll().values()) {
            RelicState state = plugin.getRelicManager().getState(relic.getId()).orElse(null);
            if (state == null || !state.isOwned()) {
                continue;
            }
            if (slot >= size - 9) {
                break;
            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(state.getOwnerUuid());
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(owner);
                meta.setDisplayName(ColorUtil.color("&e" + owner.getName()));
                meta.setLore(List.of(
                        ColorUtil.color("&7Owns: " + relic.getDisplayName()),
                        ColorUtil.color("&7Power Level: &6" + state.getPowerLevel())
                ));
                head.setItemMeta(meta);
            }
            inv.setItem(slot, head);
            slot++;
        }

        inv.setItem(size - 1, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
