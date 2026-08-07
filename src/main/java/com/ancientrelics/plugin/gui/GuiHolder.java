package com.ancientrelics.plugin.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Tags an Inventory as belonging to a specific GUI page so the
 * InventoryClickListener can route clicks without string-matching
 * inventory titles.
 */
public class GuiHolder implements InventoryHolder {

    private final GuiPage page;
    private Inventory inventory;
    private String contextRelicId;

    public GuiHolder(GuiPage page) {
        this.page = page;
    }

    public GuiPage getPage() {
        return page;
    }

    public String getContextRelicId() {
        return contextRelicId;
    }

    public void setContextRelicId(String contextRelicId) {
        this.contextRelicId = contextRelicId;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
