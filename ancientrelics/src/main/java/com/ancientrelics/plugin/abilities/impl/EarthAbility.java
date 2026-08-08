package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Earth Relic ability: bonus max health, knockback resistance and
 * faster mining (haste).
 */
public class EarthAbility extends AbstractAbility {

    public static final String KEY = "EarthAbility";
    private static final double BONUS_HEALTH = 4.0D;
    private static final double KNOCKBACK_RESISTANCE = 0.5D;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(health.getBaseValue() + BONUS_HEALTH);
        }
        AttributeInstance kb = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(KNOCKBACK_RESISTANCE);
        }
    }

    @Override
    public void onUnbind(Player player) {
        AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health != null) {
            double newBase = Math.max(20.0D, health.getBaseValue() - BONUS_HEALTH);
            health.setBaseValue(newBase);
            if (player.getHealth() > newBase) {
                player.setHealth(newBase);
            }
        }
        AttributeInstance kb = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(0.0D);
        }
        clear(player, PotionEffectType.HASTE);
    }

    @Override
    public void onTick(Player player) {
        apply(player, PotionEffectType.HASTE, 1, 60);
    }

    @Override
    public Particle getParticle() {
        return Particle.HAPPY_VILLAGER;
    }
}
