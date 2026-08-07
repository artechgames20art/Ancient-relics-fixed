package com.ancientrelics.plugin.abilities.impl;

import com.ancientrelics.plugin.abilities.AbstractAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Frost Relic ability: freezes nearby enemies periodically and lets
 * the owner walk on water by creating temporary frosted ice beneath
 * their feet, which reverts after a short delay.
 */
public class FrostAbility extends AbstractAbility {

    public static final String KEY = "FrostAbility";
    private static final double FREEZE_RADIUS = 6.0D;
    private final Map<Block, BlockData> temporaryIce = new HashMap<>();

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void onBind(Player player) {
        // No persistent baseline effect.
    }

    @Override
    public void onUnbind(Player player) {
        clear(player, PotionEffectType.SLOWNESS);
    }

    @Override
    public void onTick(Player player) {
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(FREEZE_RADIUS, FREEZE_RADIUS, FREEZE_RADIUS)) {
            if (entity instanceof LivingEntity living && !(entity instanceof Player)) {
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 40, 3, true, false, false));
                living.getWorld().spawnParticle(Particle.SNOWFLAKE, living.getLocation(), 10, 0.3, 0.5, 0.3, 0.01);
            }
        }

        Location below = player.getLocation().clone().subtract(0, 1, 0);
        Block block = below.getBlock();
        if (block.getType() == org.bukkit.Material.WATER) {
            BlockData original = block.getBlockData();
            block.setType(org.bukkit.Material.FROSTED_ICE);
            temporaryIce.put(block, original);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (temporaryIce.containsKey(block) && block.getType() == org.bukkit.Material.FROSTED_ICE) {
                        block.setType(org.bukkit.Material.WATER);
                    }
                    temporaryIce.remove(block);
                }
            }.runTaskLater(player.getServer().getPluginManager().getPlugin("AncientRelics"), 60L);
        }
    }

    @Override
    public Particle getParticle() {
        return Particle.SNOWFLAKE;
    }
}
