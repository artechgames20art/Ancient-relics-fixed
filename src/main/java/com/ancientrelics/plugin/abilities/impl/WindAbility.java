package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Wind Relic ability: double jump, slow falling and a passive
 * speed boost. Double jump is implemented by tracking whether the
 * player has already used their air jump since last touching the
 * ground; toggled via the PlayerMove listener calling
 * {@link #tryDoubleJump(Player)}.
 */
public class WindAbility extends AbstractAbility {

    public static final String KEY = "WindAbility";

    private final Map<UUID, Boolean> airJumpAvailable = new HashMap<>();

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        apply(player, PotionEffectType.SPEED, 0, Integer.MAX_VALUE);
        airJumpAvailable.put(player.getUniqueId(), true);
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.SPEED);
        clear(player, PotionEffectType.SLOW_FALLING);
        airJumpAvailable.remove(player.getUniqueId());
    }

    @Override
    public void onTick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            apply(player, PotionEffectType.SPEED, 0, 260);
        }
        if (player.getFallDistance() > 2.0F) {
            apply(player, PotionEffectType.SLOW_FALLING, 0, 40);
        }
        if (player.isOnGround()) {
            airJumpAvailable.put(player.getUniqueId(), true);
        }
    }

    /**
     * Called from the movement listener when a player leaves the
     * ground while airborne and jumping. Grants a single upward
     * boost per air-time window.
     */
    public boolean tryDoubleJump(Player player) {
        UUID id = player.getUniqueId();
        if (player.isOnGround() || player.isFlying()) {
            return false;
        }
        boolean available = airJumpAvailable.getOrDefault(id, false);
        if (!available) {
            return false;
        }
        airJumpAvailable.put(id, false);
        Vector velocity = player.getLocation().getDirection().multiply(0.6D);
        velocity.setY(1.0D);
        player.setVelocity(velocity);
        return true;
    }

    @Override
    public Particle getParticle() {
        return Particle.CLOUD;
    }
}
