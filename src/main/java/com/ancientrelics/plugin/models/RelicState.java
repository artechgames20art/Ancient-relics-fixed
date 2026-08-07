package com.ancientrelics.plugin.models;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Mutable runtime state for a single relic instance: current owner,
 * status, stats and location data. One RelicState exists per relic id
 * for the lifetime of the server (persisted to players.yml).
 */
public class RelicState {

    private final String relicId;
    private UUID ownerUuid;
    private RelicStatus status;
    private long obtainedDate;
    private int kills;
    private int deaths;
    private Location droppedLocation;
    private long droppedAt;

    public RelicState(String relicId) {
        this.relicId = relicId;
        this.status = RelicStatus.UNCLAIMED;
    }

    public String getRelicId() {
        return relicId;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public RelicStatus getStatus() {
        return status;
    }

    public void setStatus(RelicStatus status) {
        this.status = status;
    }

    public long getObtainedDate() {
        return obtainedDate;
    }

    public void setObtainedDate(long obtainedDate) {
        this.obtainedDate = obtainedDate;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void incrementKills() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public Location getDroppedLocation() {
        return droppedLocation;
    }

    public void setDroppedLocation(Location droppedLocation) {
        this.droppedLocation = droppedLocation;
    }

    public long getDroppedAt() {
        return droppedAt;
    }

    public void setDroppedAt(long droppedAt) {
        this.droppedAt = droppedAt;
    }

    public boolean isOwned() {
        return status == RelicStatus.OWNED && ownerUuid != null;
    }

    public int getPowerLevel() {
        return kills * 2 - deaths + 10;
    }
}
