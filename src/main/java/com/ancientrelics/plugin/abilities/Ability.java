package com.ancientrelics.plugin.abilities;

import org.bukkit.entity.Player;

/**
 * Represents the passive/active ability behaviour bound to a relic.
 * Implementations are stateless per-player; the owning player is
 * always passed in explicitly so a single Ability instance can be
 * reused for the lifetime of the plugin.
 */
public interface Ability {

    /**
     * Unique key matching the ability-class entry in relics.yml.
     */
    String getKey();

    /**
     * Called once when the player becomes the relic's owner
     * (on pickup/bind). Used to apply persistent effects such as
     * max health modifiers.
     */
    void onBind(Player player);

    /**
     * Called once when the player stops being the relic's owner
     * (death/drop, admin removal). Used to clean up persistent
     * effects applied in onBind.
     */
    void onUnbind(Player player);

    /**
     * Called periodically (see config ability-check-period-ticks)
     * while the player owns the relic, for continuous passive
     * effects (potion effects, environment checks, etc).
     */
    void onTick(Player player);

    /**
     * Particle to display around the owner. Returning null disables
     * the automatic particle loop for this ability (e.g. if it
     * manages its own particles).
     */
    default org.bukkit.Particle getParticle() {
        return null;
    }
}
