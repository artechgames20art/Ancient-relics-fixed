package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class StormRelic extends AbstractRelic {

    public StormRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("storm_relic", name, lore, description, rarity, Material.TRIDENT,
                Particle.ELECTRIC_SPARK, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, ability);
    }
}
