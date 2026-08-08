package com.ancientrelics.plugin.managers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Generic keyed cooldown tracker used by active-style abilities
 * (e.g. wind double jump) to prevent spam-triggering.
 */
public class CooldownManager {

    private final ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<>();

    private String key(UUID uuid, String action) {
        return uuid + ":" + action;
    }

    public boolean isOnCooldown(UUID uuid, String action) {
        Long expiry = cooldowns.get(key(uuid, action));
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public void setCooldown(UUID uuid, String action, long duration, TimeUnit unit) {
        long expiry = System.currentTimeMillis() + unit.toMillis(duration);
        cooldowns.put(key(uuid, action), expiry);
    }

    public void clear(UUID uuid, String action) {
        cooldowns.remove(key(uuid, action));
    }
}
