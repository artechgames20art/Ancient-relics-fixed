package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Fire Relic ability: fire resistance, lava immunity (via fire
 * resistance + freeze-on-lava-tick prevention handled here),
 * and continuous flame particles. Extra fire damage on hit is
 * applied via the EntityDamage listener referencing this ability.
 */
public class FireAbility extends AbstractAbility {

    public static final String KEY = "FireAbility";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        apply(player, PotionEffectType.FIRE_RESISTANCE, 0, Integer.MAX_VALUE);
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.FIRE_RESISTANCE);
        player.setFireTicks(0);
    }

    @Override
    public void onTick(Player player) {
        // Immune to lava: extinguish and clear fire ticks proactively.
        Block feet = player.getLocation().getBlock();
        if (feet.getType().name().contains("LAVA")) {
            player.setFireTicks(0);
        }
        if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            apply(player, PotionEffectType.FIRE_RESISTANCE, 0, 260);
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.FLAME;
    }
}
