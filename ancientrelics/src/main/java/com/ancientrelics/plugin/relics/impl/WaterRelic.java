package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class WaterRelic extends AbstractRelic {

    public WaterRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("water_relic", name, lore, description, rarity, Material.HEART_OF_THE_SEA,
                Particle.BUBBLE_COLUMN_UP, Sound.ENTITY_DOLPHIN_SPLASH, ability);
    }
}
