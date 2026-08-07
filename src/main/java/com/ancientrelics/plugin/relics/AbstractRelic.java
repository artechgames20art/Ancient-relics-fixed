package com.ancientrelics.plugin.relics;

import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.utils.ColorUtil;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared base implementation for all relics: handles building the
 * ItemStack from the data supplied by concrete subclasses, tagging
 * it with the relic id via PersistentDataContainer, and applying
 * the enchant glow.
 */
public abstract class AbstractRelic implements Relic {

    private final String id;
    private final String displayName;
    private final List<String> lore;
    private final String description;
    private final RelicRarity rarity;
    private final Material material;
    private final Particle particle;
    private final Sound sound;
    private final Ability ability;

    protected AbstractRelic(String id, String displayName, List<String> lore, String description,
                             RelicRarity rarity, Material material, Particle particle, Sound sound,
                             Ability ability) {
        this.id = id;
        this.displayName = displayName;
        this.lore = lore;
        this.description = description;
        this.rarity = rarity;
        this.material = material;
        this.particle = particle;
        this.sound = sound;
        this.ability = ability;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public RelicRarity getRarity() {
        return rarity;
    }

    @Override
    public Particle getParticle() {
        return particle;
    }

    @Override
    public Sound getSound() {
        return sound;
    }

    @Override
    public Ability getAbility() {
        return ability;
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.color(displayName));

            List<String> finalLore = new ArrayList<>();
            for (String line : lore) {
                finalLore.add(ColorUtil.color(line));
            }
            finalLore.add("");
            finalLore.add(ColorUtil.color("&8Rarity: " + rarity.getDisplay()));
            meta.setLore(finalLore);

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.setEnchantmentGlintOverride(true);

            meta.getPersistentDataContainer().set(RelicKeys.relicId(), PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }
}
