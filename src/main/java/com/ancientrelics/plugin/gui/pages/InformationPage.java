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
 * Detailed single-relic information view: description, abilities,
 * owner, stats and a particle preview slot.
 */
public class InformationPage {

    private final AncientRelicsPlugin plugin;

    public InformationPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player viewer, String relicId) {
        GuiHolder holder = new GuiHolder(GuiPage.INFORMATION);
        holder.setContextRelicId(relicId);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.information", "&8Information"));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic == null) {
            inv.setItem(13, GuiItemBuilder.of(Material.BARRIER).name("&cRelic not found").build());
            inv.setItem(22, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
            return inv;
        }

        RelicState state = plugin.getRelicManager().getState(relicId).orElse(null);

        ItemStack item = relic.createItemStack();
        var meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ColorUtil.color(relic.getDescription()));
            lore.add("");
            lore.add(ColorUtil.color("&8Rarity: " + relic.getRarity().getDisplay()));
            if (state != null && state.isOwned()) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(state.getOwnerUuid());
                lore.add(ColorUtil.color("&7Owner: &a" + owner.getName()));
            } else {
                lore.add(ColorUtil.color("&7Owner: &cUnowned"));
            }
            lore.add(ColorUtil.color("&7Status: &f" + (state != null ? state.getStatus().name() : "UNKNOWN")));
            lore.add(ColorUtil.color("&7Kills: &c" + (state != null ? state.getKills() : 0)));
            lore.add(ColorUtil.color("&7Deaths: &4" + (state != null ? state.getDeaths() : 0)));
            lore.add(ColorUtil.color("&7Power Level: &6" + (state != null ? state.getPowerLevel() : 0)));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        inv.setItem(13, item);

        inv.setItem(22, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
