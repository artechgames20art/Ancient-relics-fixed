package com.ancientrelics.plugin.models;

import java.util.UUID;

/**
 * Persistent per-player data. Each player may own at most one relic
 * at a time, tracked by relicId (nullable when they own nothing).
 */
public class PlayerData {

    private final UUID uuid;
    private String ownedRelicId;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getOwnedRelicId() {
        return ownedRelicId;
    }

    public void setOwnedRelicId(String ownedRelicId) {
        this.ownedRelicId = ownedRelicId;
    }

    public boolean ownsRelic() {
        return ownedRelicId != null;
    }
}
