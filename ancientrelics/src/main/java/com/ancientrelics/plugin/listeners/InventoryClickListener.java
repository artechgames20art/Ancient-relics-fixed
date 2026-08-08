package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.gui.GuiHolder;
import com.ancientrelics.plugin.gui.GuiPage;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Central click router for every plugin GUI. Cancels all clicks
 * inside plugin inventories (view-only navigation) and dispatches
 * based on which {@link GuiPage} and slot was clicked.
 */
public class InventoryClickListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public InventoryClickListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder holder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        switch (holder.getPage()) {
            case HOME -> handleHome(player, event.getSlot());
            case MY_RELIC, SETTINGS -> handleBackOnly(player, event.getSlot());
            case ALL_RELICS -> handleAllRelics(player, clicked, event.getSlot());
            case OWNERS -> handleBackOnlyLarge(player, event.getSlot());
            case INFORMATION -> handleInformation(player, event.getSlot());
            case ADMIN -> handleBackOnly(player, event.getSlot());
        }
    }

    private void handleHome(Player player, int slot) {
        switch (slot) {
            case 10 -> plugin.getGuiManager().openMyRelic(player);
            case 12 -> plugin.getGuiManager().openAllRelics(player);
            case 14 -> plugin.getGuiManager().openOwners(player);
            case 16 -> plugin.getGuiManager().openInformation(player, firstRelicId());
            case 22 -> plugin.getGuiManager().openSettings(player);
            case 26 -> {
                if (player.hasPermission("ancientrelics.admin")) {
                    plugin.getGuiManager().openAdmin(player);
                }
            }
            default -> {
            }
        }
    }

    private void handleBackOnly(Player player, int slot) {
        if (slot == 22) {
            plugin.getGuiManager().openHome(player);
        }
    }

    private void handleBackOnlyLarge(Player player, int slot) {
        int size = plugin.getConfigManager().getConfig().getInt("gui.inventory-size", 54);
        if (slot == size - 1) {
            plugin.getGuiManager().openHome(player);
        }
    }

    private void handleAllRelics(Player player, ItemStack clicked, int slot) {
        int size = plugin.getConfigManager().getConfig().getInt("gui.inventory-size", 54);
        if (slot == size - 1) {
            plugin.getGuiManager().openHome(player);
            return;
        }
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }
        String relicId = meta.getPersistentDataContainer().get(RelicKeys.relicId(), PersistentDataType.STRING);
        if (relicId != null) {
            plugin.getGuiManager().openInformation(player, relicId);
        }
    }

    private void handleInformation(Player player, int slot) {
        if (slot == 22) {
            plugin.getGuiManager().openHome(player);
        }
    }

    private String firstRelicId() {
        return plugin.getRelicRegistry().getAll().keySet().stream().findFirst().orElse("");
    }
}
