package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class WindRelic extends AbstractRelic {

    public WindRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("wind_relic", name, lore, description, rarity, Material.FEATHER,
                Particle.CLOUD, Sound.ENTITY_PHANTOM_FLAP, ability);
    }
}
