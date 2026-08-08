package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Intercepts item pickups: if the item is a tagged relic, routes it
 * through RelicManager.handlePickup and cancels the vanilla pickup
 * (since binding happens via a broadcast + inventory item grant,
 * not silent absorption) when the picker already owns a relic.
 */
public class PlayerPickupListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public PlayerPickupListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Item item = event.getItem();
        var meta = item.getItemStack().getItemMeta();
        if (meta == null) {
            return;
        }
        String relicId = meta.getPersistentDataContainer().get(RelicKeys.relicId(), PersistentDataType.STRING);
        if (relicId == null) {
            return;
        }

        boolean alreadyOwnsRelic = plugin.getRelicManager().playerOwnsAnyRelic(player.getUniqueId());
        if (alreadyOwnsRelic) {
            // Can't hold two relics: block pickup entirely, leave item on ground.
            event.setCancelled(true);
            return;
        }

        boolean handled = plugin.getRelicManager().handlePickup(player, item);
        if (handled) {
            // Binding already granted ownership + item to the player via bind();
            // remove the ground item and cancel the raw pickup to avoid duplicates.
            event.setCancelled(true);
            player.getInventory().addItem(item.getItemStack());
            item.remove();
        }
    }
}
