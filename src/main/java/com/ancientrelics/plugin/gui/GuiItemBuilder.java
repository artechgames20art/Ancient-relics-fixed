package com.ancientrelics.plugin.gui;

import com.ancientrelics.plugin.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Small fluent builder for constructing GUI control items, keeping
 * page classes free of repetitive ItemMeta boilerplate.
 */
public class GuiItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;
    private final List<String> lore = new ArrayList<>();

    public GuiItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = stack.getItemMeta();
    }

    public static GuiItemBuilder of(Material material) {
        return new GuiItemBuilder(material);
    }

    public GuiItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(ColorUtil.color(name));
        }
        return this;
    }

    public GuiItemBuilder lore(String line) {
        lore.add(ColorUtil.color(line));
        return this;
    }

    public GuiItemBuilder lore(List<String> lines) {
        for (String line : lines) {
            lore.add(ColorUtil.color(line));
        }
        return this;
    }

    public GuiItemBuilder glow() {
        if (meta != null) {
            meta.setEnchantmentGlintOverride(true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }
}
