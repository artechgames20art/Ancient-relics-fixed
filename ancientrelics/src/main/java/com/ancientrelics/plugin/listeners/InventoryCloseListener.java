package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.gui.GuiHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Reserved hook for cleanup when a plugin GUI is closed (e.g.
 * cancelling any pending "awaiting input" state in future admin
 * flows). Currently a no-op but kept as its own listener per the
 * modular event architecture.
 */
public class InventoryCloseListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public InventoryCloseListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder)) {
            return;
        }
        // No state to clean up currently.
    }
}
