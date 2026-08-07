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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Shows details of the relic the viewing player currently owns, or
 * a placeholder message if they own none.
 */
public class MyRelicPage {

    private final AncientRelicsPlugin plugin;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm");

    public MyRelicPage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        GuiHolder holder = new GuiHolder(GuiPage.MY_RELIC);
        String title = ColorUtil.color(plugin.getConfigManager().getConfig().getString("gui.titles.my-relic", "&8My Relic"));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        Optional<Relic> relicOpt = plugin.getRelicManager().getOwnedRelic(player);
        if (relicOpt.isEmpty()) {
            inv.setItem(13, GuiItemBuilder.of(Material.BARRIER)
                    .name("&cNo Relic Owned")
                    .lore(List.of(plugin.getMessageManager().get("gui.no-relic-owned")))
                    .build());
        } else {
            Relic relic = relicOpt.get();
            RelicState state = plugin.getRelicManager().getState(relic.getId()).orElse(null);

            ItemStack displayItem = relic.createItemStack();
            var meta = displayItem.getItemMeta();
            if (meta != null && state != null) {
                List<String> lore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : new ArrayList<>());
                lore.add("");
                lore.add(ColorUtil.color("&7Status: &a" + state.getStatus().name()));
                lore.add(ColorUtil.color("&7Obtained: &f" + (state.getObtainedDate() > 0
                        ? DATE_FORMAT.format(new Date(state.getObtainedDate())) : "Unknown")));
                lore.add(ColorUtil.color("&7Kills: &c" + state.getKills()));
                lore.add(ColorUtil.color("&7Deaths: &4" + state.getDeaths()));
                lore.add(ColorUtil.color("&7Power Level: &6" + state.getPowerLevel()));
                lore.add("");
                lore.add(ColorUtil.color(relic.getDescription()));
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }
            inv.setItem(13, displayItem);
        }

        inv.setItem(22, GuiItemBuilder.of(Material.ARROW).name("&7Back").build());
        return inv;
    }
}
