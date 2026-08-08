package com.ancientrelics.plugin.relics.impl;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.AbstractRelic;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class LightRelic extends AbstractRelic {

    public LightRelic(Ability ability, String name, List<String> lore, String description, RelicRarity rarity) {
        super("light_relic", name, lore, description, rarity, Material.NETHER_STAR,
                Particle.END_ROD, Sound.ENTITY_PLAYER_LEVELUP, ability);
    }
}
