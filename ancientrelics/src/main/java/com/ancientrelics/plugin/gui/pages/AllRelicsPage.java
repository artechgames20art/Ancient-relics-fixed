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

import java.util.ArrayList;
import java.util.List;

/**
 * Lists every relic that exists on the server, one slot each, with
 * owner/status summary lore. Clicking an entry opens the
 * Information page for that relic.
 */
public class AllRelicsPage {

    private final AncientRelicsPlugin plugin;

    public AllRelicsPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player viewer) {
        GuiHolder holder = new GuiHolder(GuiPage.ALL_RELICS);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.all-relics", "&8All Relics"));
        int size = plugin.getConfigManager().getConfig().getInt("gui.inventory-size", 54);
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        int slot = 0;
        for (Relic relic : plugin.getRelicRegistry().getAll().values()) {
            if (slot >= size - 9) {
                break;
            }
            RelicState state = plugin.getRelicManager().getState(relic.getId()).orElse(null);
            ItemStack item = relic.createItemStack();
            var meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : new ArrayList<>());
                lore.add("");
                if (state != null && state.isOwned()) {
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(state.getOwnerUuid());
                    lore.add(ColorUtil.color("&7Owner: &a" + owner.getName()));
                } else {
                    lore.add(ColorUtil.color("&7Owner: &cUnowned"));
                }
                lore.add(ColorUtil.color("&7Status: &f" + (state != null ? state.getStatus().name() : "UNKNOWN")));
                lore.add("");
                lore.add(ColorUtil.color("&eClick for more information"));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
        }

        inv.setItem(size - 1, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
