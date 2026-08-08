package com.ancientrelics.plugin.models;

/**
 * Lifecycle status of a relic at any point in time.
 */
public enum RelicStatus {
    UNCLAIMED,   // Never found, waiting to spawn/be found
    OWNED,       // Currently bound to a player
    DROPPED,     // On the ground after owner death, awaiting pickup
    DESPAWNED    // Dropped item expired, awaiting scheduled respawn
}
