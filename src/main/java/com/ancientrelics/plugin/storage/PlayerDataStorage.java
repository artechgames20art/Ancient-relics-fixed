package com.ancientrelics.plugin.storage;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.models.PlayerData;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.models.RelicStatus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Persists {@link PlayerData} and {@link RelicState} objects to and
 * from players.yml via the ConfigManager.
 */
public class PlayerDataStorage {

    private final AncientRelicsPlugin plugin;

    public PlayerDataStorage(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, PlayerData> loadPlayers() {
        Map<UUID, PlayerData> result = new HashMap<>();
        FileConfiguration file = plugin.getConfigManager().getPlayersConfig();
        ConfigurationSection section = file.getConfigurationSection("players");
        if (section == null) {
            return result;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PlayerData data = new PlayerData(uuid);
                String ownedRelic = section.getString(key + ".owned-relic", null);
                data.setOwnedRelicId(ownedRelic);
                result.put(uuid, data);
            } catch (IllegalArgumentException ignored) {
                // Skip malformed UUID keys.
            }
        }
        return result;
    }

    public Map<String, RelicState> loadRelicStates() {
        Map<String, RelicState> result = new HashMap<>();
        FileConfiguration file = plugin.getConfigManager().getPlayersConfig();
        ConfigurationSection section = file.getConfigurationSection("relic-states");
        if (section == null) {
            return result;
        }
        for (String relicId : section.getKeys(false)) {
            RelicState state = new RelicState(relicId);
            String ownerRaw = section.getString(relicId + ".owner", null);
            if (ownerRaw != null) {
                try {
                    state.setOwnerUuid(UUID.fromString(ownerRaw));
                } catch (IllegalArgumentException ignored) {
                    // corrupt data, skip owner
                }
            }
            String statusRaw = section.getString(relicId + ".status", "UNCLAIMED");
            try {
                state.setStatus(RelicStatus.valueOf(statusRaw));
            } catch (IllegalArgumentException ignored) {
                state.setStatus(RelicStatus.UNCLAIMED);
            }
            state.setObtainedDate(section.getLong(relicId + ".obtained-date", 0L));
            state.setKills(section.getInt(relicId + ".kills", 0));
            state.setDeaths(section.getInt(relicId + ".deaths", 0));
            state.setDroppedAt(section.getLong(relicId + ".dropped-at", 0L));

            String worldName = section.getString(relicId + ".dropped-location.world", null);
            if (worldName != null) {
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    double x = section.getDouble(relicId + ".dropped-location.x");
                    double y = section.getDouble(relicId + ".dropped-location.y");
                    double z = section.getDouble(relicId + ".dropped-location.z");
                    state.setDroppedLocation(new Location(world, x, y, z));
                }
            }

            result.put(relicId, state);
        }
        return result;
    }

    public void saveAll(Map<UUID, PlayerData> players, Map<String, RelicState> relicStates) {
        FileConfiguration file = plugin.getConfigManager().getPlayersConfig();
        file.set("players", null);
        file.set("relic-states", null);

        for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            String base = "players." + entry.getKey();
            file.set(base + ".owned-relic", entry.getValue().getOwnedRelicId());
        }

        for (Map.Entry<String, RelicState> entry : relicStates.entrySet()) {
            String base = "relic-states." + entry.getKey();
            RelicState state = entry.getValue();
            file.set(base + ".owner", state.getOwnerUuid() != null ? state.getOwnerUuid().toString() : null);
            file.set(base + ".status", state.getStatus().name());
            file.set(base + ".obtained-date", state.getObtainedDate());
            file.set(base + ".kills", state.getKills());
            file.set(base + ".deaths", state.getDeaths());
            file.set(base + ".dropped-at", state.getDroppedAt());

            Location loc = state.getDroppedLocation();
            if (loc != null && loc.getWorld() != null) {
                file.set(base + ".dropped-location.world", loc.getWorld().getName());
                file.set(base + ".dropped-location.x", loc.getX());
                file.set(base + ".dropped-location.y", loc.getY());
                file.set(base + ".dropped-location.z", loc.getZ());
            }
        }

        plugin.getConfigManager().savePlayers();
    }
}
