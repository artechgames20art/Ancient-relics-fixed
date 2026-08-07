package com.ancientrelics.plugin.abilities;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Convenience base class providing shared helper methods for
 * concrete Ability implementations, removing boilerplate around
 * applying/removing potion effects.
 */
public abstract class AbstractAbility implements Ability {

    protected void apply(Player player, PotionEffectType type, int amplifier, int durationTicks) {
        player.addPotionEffect(new PotionEffect(type, durationTicks, amplifier, true, false, true));
    }

    protected void clear(Player player, PotionEffectType type) {
        if (player.hasPotionEffect(type)) {
            player.removePotionEffect(type);
        }
    }

    @Override
    public Particle getParticle() {
        return null;
    }
}
