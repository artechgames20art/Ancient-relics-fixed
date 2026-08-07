package com.ancientrelics.plugin.relics;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Static definition of a relic loaded from relics.yml. This is the
 * immutable "template" for a relic; ownership/status/kills etc live
 * in {@link com.ancientrelics.plugin.models.RelicState}.
 */
public interface Relic {

    String getId();

    String getDisplayName();

    List<String> getLore();

    String getDescription();

    RelicRarity getRarity();

    Particle getParticle();

    Sound getSound();

    Ability getAbility();

    /**
     * Builds a fresh ItemStack representing this relic, with all
     * lore/name/enchant-glow applied.
     */
    ItemStack createItemStack();
}
