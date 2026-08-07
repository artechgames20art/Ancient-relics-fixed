package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.potion.PotionEffectType;

/**
 * Light Relic ability: passive regeneration, bonus damage to
 * undead (handled via EntityDamage listener), and applies Glowing
 * to nearby hostile undead so the owner can always see them.
 */
public class LightAbility extends AbstractAbility {

    public static final String KEY = "LightAbility";
    private static final double GLOW_RADIUS = 15.0D;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        apply(player, PotionEffectType.REGENERATION, 0, Integer.MAX_VALUE);
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.REGENERATION);
    }

    @Override
    public void onTick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            apply(player, PotionEffectType.REGENERATION, 0, 260);
        }

        for (Entity entity : player.getNearbyEntities(GLOW_RADIUS, GLOW_RADIUS, GLOW_RADIUS)) {
            if ((entity instanceof Zombie || entity instanceof Skeleton) && entity instanceof LivingEntity living) {
                living.setGlowing(true);
            }
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.END_ROD;
    }
}
