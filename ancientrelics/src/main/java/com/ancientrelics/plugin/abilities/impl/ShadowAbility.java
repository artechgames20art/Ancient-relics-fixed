package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Shadow Relic ability: permanent night vision, nearby hostile
 * mobs ignore the owner while sneaking, and a speed boost at
 * night.
 */
public class ShadowAbility extends AbstractAbility {

    public static final String KEY = "ShadowAbility";
    private static final double MOB_IGNORE_RADIUS = 10.0D;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        apply(player, PotionEffectType.NIGHT_VISION, 0, Integer.MAX_VALUE);
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.NIGHT_VISION);
        clear(player, PotionEffectType.SPEED);
    }

    @Override
    public void onTick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            apply(player, PotionEffectType.NIGHT_VISION, 0, 260);
        }

        long time = player.getWorld().getTime();
        boolean isNight = time >= 13000 && time <= 23000;
        if (isNight) {
            apply(player, PotionEffectType.SPEED, 1, 60);
        }

        if (player.isSneaking()) {
            for (org.bukkit.entity.Entity entity : player.getNearbyEntities(MOB_IGNORE_RADIUS, MOB_IGNORE_RADIUS, MOB_IGNORE_RADIUS)) {
                if (entity instanceof Mob mob) {
                    if (mob.getTarget() != null && mob.getTarget().equals(player)) {
                        mob.setTarget(null);
                    }
                }
            }
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.SMOKE;
    }
}
