package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Water Relic ability: water breathing, faster swimming (dolphin's
 * grace) and bubble particles.
 */
public class WaterAbility extends AbstractAbility {

    public static final String KEY = "WaterAbility";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        apply(player, PotionEffectType.WATER_BREATHING, 0, Integer.MAX_VALUE);
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.WATER_BREATHING);
        clear(player, PotionEffectType.DOLPHINS_GRACE);
    }

    @Override
    public void onTick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            apply(player, PotionEffectType.WATER_BREATHING, 0, 260);
        }
        if (player.isInWater()) {
            apply(player, PotionEffectType.DOLPHINS_GRACE, 1, 60);
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.BUBBLE_COLUMN_UP;
    }
}
