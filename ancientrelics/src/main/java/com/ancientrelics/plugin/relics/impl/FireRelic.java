package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class FireRelic extends AbstractRelic {

    public FireRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("fire_relic", name, lore, description, rarity, Material.BLAZE_ROD,
                Particle.FLAME, Sound.ENTITY_BLAZE_SHOOT, ability);
    }
}
