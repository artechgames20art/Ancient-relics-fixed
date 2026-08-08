package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Storm Relic ability: immunity to lightning strikes (handled via
 * EntityDamage listener checking relic ownership), plus strength
 * and speed boosts while it is raining/thundering in the owner's
 * world.
 */
public class StormAbility extends AbstractAbility {

    public static final String KEY = "StormAbility";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        // No persistent effect on bind; conditional effects handled in onTick.
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.STRENGTH);
        clear(player, PotionEffectType.SPEED);
    }

    @Override
    public void onTick(Player player) {
        boolean stormy = player.getWorld().isThundering();
        boolean rainy = player.getWorld().hasStorm();

        if (stormy) {
            apply(player, PotionEffectType.STRENGTH, 0, 60);
            apply(player, PotionEffectType.SPEED, 1, 60);
        } else if (rainy) {
            apply(player, PotionEffectType.STRENGTH, 0, 60);
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.ELECTRIC_SPARK;
    }
}
