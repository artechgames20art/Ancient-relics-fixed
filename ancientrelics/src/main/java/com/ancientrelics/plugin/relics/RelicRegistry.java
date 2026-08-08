package com.ancientrelics.plugin.relics;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.abilities.Ability;
import com.ancientrelics.plugin.abilities.impl.EarthAbility;
import com.ancientrelics.plugin.abilities.impl.FireAbility;
import com.ancientrelics.plugin.abilities.impl.FrostAbility;
import com.ancientrelics.plugin.abilities.impl.LightAbility;
import com.ancientrelics.plugin.abilities.impl.ShadowAbility;
import com.ancientrelics.plugin.abilities.impl.StormAbility;
import com.ancientrelics.plugin.abilities.impl.WaterAbility;
import com.ancientrelics.plugin.abilities.impl.WindAbility;
import com.ancientrelics.plugin.models.RelicRarity;
import com.ancientrelics.plugin.relics.impl.EarthRelic;
import com.ancientrelics.plugin.relics.impl.FireRelic;
import com.ancientrelics.plugin.relics.impl.FrostRelic;
import com.ancientrelics.plugin.relics.impl.LightRelic;
import com.ancientrelics.plugin.relics.impl.ShadowRelic;
import com.ancientrelics.plugin.relics.impl.StormRelic;
import com.ancientrelics.plugin.relics.impl.WaterRelic;
import com.ancientrelics.plugin.relics.impl.WindRelic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Loads relic definitions from relics.yml and builds the concrete
 * {@link Relic} instances.
 *
 * <p>Extensibility: to add a new relic, register its Relic
 * constructor and Ability supplier in the two maps below, then add
 * a matching entry to relics.yml. No other core code needs to
 * change.</p>
 */
public class RelicRegistry {

    private final AncientRelicsPlugin plugin;
    private final Map<String, Relic> relics = new LinkedHashMap<>();

    private final Map<String, Supplier<Ability>> abilityFactories = new LinkedHashMap<>();
    private final Map<String, RelicFactory> relicFactories = new LinkedHashMap<>();

    @FunctionalInterface
    private interface RelicFactory {
        Relic create(Ability ability, String name, java.util.List<String> lore, String description, RelicRarity rarity);
    }

    public RelicRegistry(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    private void registerDefaults() {
        abilityFactories.put(FireAbility.KEY, FireAbility::new);
        abilityFactories.put(WaterAbility.KEY, WaterAbility::new);
        abilityFactories.put(EarthAbility.KEY, EarthAbility::new);
        abilityFactories.put(WindAbility.KEY, WindAbility::new);
        abilityFactories.put(ShadowAbility.KEY, ShadowAbility::new);
        abilityFactories.put(StormAbility.KEY, StormAbility::new);
        abilityFactories.put(FrostAbility.KEY, FrostAbility::new);
        abilityFactories.put(LightAbility.KEY, LightAbility::new);

        relicFactories.put("fire_relic", FireRelic::new);
        relicFactories.put("water_relic", WaterRelic::new);
        relicFactories.put("earth_relic", EarthRelic::new);
        relicFactories.put("wind_relic", WindRelic::new);
        relicFactories.put("shadow_relic", ShadowRelic::new);
        relicFactories.put("storm_relic", StormRelic::new);
        relicFactories.put("frost_relic", FrostRelic::new);
        relicFactories.put("light_relic", LightRelic::new);
    }

    public void loadAll() {
        relics.clear();
        FileConfiguration config = plugin.getConfigManager().getRelicsConfig();
        ConfigurationSection root = config.getConfigurationSection("relics");
        if (root == null) {
            plugin.getLogger().warning("relics.yml has no 'relics' section, nothing loaded.");
            return;
        }

        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) continue;

            RelicFactory relicFactory = relicFactories.get(id);
            if (relicFactory == null) {
                plugin.getLogger().warning("No Relic implementation registered for id '" + id + "', skipping.");
                continue;
            }

            String abilityClass = section.getString("ability-class", "");
            Supplier<Ability> abilitySupplier = abilityFactories.get(abilityClass);
            if (abilitySupplier == null) {
                plugin.getLogger().warning("No Ability implementation registered for '" + abilityClass + "' (relic " + id + "), skipping.");
                continue;
            }

            String name = section.getString("name", id);
            java.util.List<String> lore = section.getStringList("lore");
            String description = section.getString("description", "");
            String rarityRaw = section.getString("rarity", "EPIC");
            RelicRarity rarity;
            try {
                rarity = RelicRarity.valueOf(rarityRaw.toUpperCase());
            } catch (IllegalArgumentException ex) {
                rarity = RelicRarity.EPIC;
            }

            Ability ability = abilitySupplier.get();
            Relic relic = relicFactory.create(ability, name, lore, description, rarity);
            relics.put(id, relic);
        }

        plugin.getLogger().info("Loaded " + relics.size() + " relics.");
    }

    public Relic get(String id) {
        return relics.get(id);
    }

    public boolean exists(String id) {
        return relics.containsKey(id);
    }

    public Map<String, Relic> getAll() {
        return relics;
    }
}
