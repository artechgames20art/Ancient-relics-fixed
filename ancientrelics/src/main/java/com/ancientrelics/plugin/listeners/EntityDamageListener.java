package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.abilities.impl.FireAbility;
import com.ancientrelics.plugin.abilities.impl.LightAbility;
import com.ancientrelics.plugin.abilities.impl.StormAbility;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Applies relic-driven damage modifiers:
 * - Fire Relic: bonus outgoing melee damage, immune to fire/lava damage causes.
 * - Storm Relic: immune to lightning damage.
 * - Light Relic: bonus outgoing damage to undead.
 * Also guarantees dropped relic ground items never take damage
 * (burn/explosion protection), as a defensive backstop alongside
 * the invulnerable flag set at spawn time.
 */
public class EntityDamageListener implements Listener {

    private final AncientRelicsPlugin plugin;
    private static final double FIRE_BONUS_DAMAGE = 2.0D;
    private static final double UNDEAD_BONUS_DAMAGE = 3.0D;

    public EntityDamageListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // Protect relic ground items from fire/lava/explosion/despawn damage.
        if (event.getEntity() instanceof Item item) {
            ItemMetaCheck: {
                var meta = item.getItemStack().getItemMeta();
                if (meta == null) break ItemMetaCheck;
                String relicId = meta.getPersistentDataContainer().get(RelicKeys.relicId(), PersistentDataType.STRING);
                if (relicId == null) break ItemMetaCheck;

                boolean preventBurn = plugin.getConfigManager().getConfig().getBoolean("drop.prevent-burn", true);
                boolean preventExplosion = plugin.getConfigManager().getConfig().getBoolean("drop.prevent-explosion", true);

                EntityDamageEvent.DamageCause cause = event.getCause();
                boolean isBurn = cause == EntityDamageEvent.DamageCause.FIRE
                        || cause == EntityDamageEvent.DamageCause.FIRE_TICK
                        || cause == EntityDamageEvent.DamageCause.LAVA;
                boolean isExplosion = cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                        || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;

                if ((isBurn && preventBurn) || (isExplosion && preventExplosion)) {
                    event.setCancelled(true);
                }
            }
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Storm Relic: lightning immunity.
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            plugin.getRelicManager().getOwnedRelic(player).ifPresent(relic -> {
                if (relic.getAbility() instanceof StormAbility) {
                    event.setCancelled(true);
                }
            });
        }

        // Fire Relic: immune to fire/lava damage (redundant safety net
        // alongside the permanent Fire Resistance effect).
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            plugin.getRelicManager().getOwnedRelic(player).ifPresent(relic -> {
                if (relic.getAbility() instanceof FireAbility) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        var relicOpt = plugin.getRelicManager().getOwnedRelic(attacker);
        if (relicOpt.isEmpty()) {
            return;
        }
        Relic relic = relicOpt.get();

        if (relic.getAbility() instanceof FireAbility) {
            event.setDamage(event.getDamage() + FIRE_BONUS_DAMAGE);
        }

        if (relic.getAbility() instanceof LightAbility
                && (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton)) {
            event.setDamage(event.getDamage() + UNDEAD_BONUS_DAMAGE);
        }
    }
}
