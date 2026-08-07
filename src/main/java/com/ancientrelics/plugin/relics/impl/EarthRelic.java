package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class EarthRelic extends AbstractRelic {

    public EarthRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("earth_relic", name, lore, description, rarity, Material.EMERALD,
                Particle.HAPPY_VILLAGER, Sound.BLOCK_STONE_BREAK, ability);
    }
}
