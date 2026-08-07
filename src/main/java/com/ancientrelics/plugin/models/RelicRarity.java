package com.ancientrelics.plugin.models;

/**
 * Represents the rarity tier of a relic. Used purely for display
 * and power-level weighting; does not affect core ownership logic.
 */
public enum RelicRarity {
    RARE("&9Rare", 1),
    EPIC("&5Epic", 2),
    LEGENDARY("&6Legendary", 3),
    MYTHIC("&dMythic", 4);

    private final String display;
    private final int weight;

    RelicRarity(String display, int weight) {
        this.display = display;
        this.weight = weight;
    }

    public String getDisplay() {
        return display;
    }

    public int getWeight() {
        return weight;
    }
}
