package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class FrostRelic extends AbstractRelic {

    public FrostRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("frost_relic", name, lore, description, rarity, Material.BLUE_ICE,
                Particle.SNOWFLAKE, Sound.BLOCK_GLASS_BREAK, ability);
    }
}
