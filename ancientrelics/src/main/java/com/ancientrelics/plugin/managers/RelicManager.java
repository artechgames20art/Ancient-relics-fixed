package com.ancientrelics.plugin.managers;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import com.ancientrelics.plugin.models.PlayerData;
import com.ancientrelics.plugin.models.RelicState;
import com.ancientrelics.plugin.models.RelicStatus;
import com.ancientrelics.plugin.relics.Relic;
import com.ancientrelics.plugin.storage.PlayerDataStorage;
import com.ancientrelics.plugin.utils.ColorUtil;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager owning relic ownership state, binding/unbinding
 * abilities, drop-on-death handling, pickup handling and the
 * despawn/respawn lifecycle. All other components (listeners, GUI,
 * commands) go through this class rather than mutating state
 * directly.
 */
public class RelicManager {

    private final AncientRelicsPlugin plugin;
    private final PlayerDataStorage storage;

    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();
    private final Map<String, RelicState> relicStates = new ConcurrentHashMap<>();

    // relicId -> the ground Item entity currently representing a dropped/unclaimed relic
    private final Map<String, UUID> groundItems = new ConcurrentHashMap<>();
    // relicId -> scheduled despawn task
    private final Map<String, BukkitTask> despawnTasks = new ConcurrentHashMap<>();
    // relicId -> scheduled respawn task
    private final Map<String, BukkitTask> respawnTasks = new ConcurrentHashMap<>();

    public RelicManager(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
        this.storage = new PlayerDataStorage(plugin);
    }

    public void loadAll() {
        playerData.putAll(storage.loadPlayers());
        relicStates.putAll(storage.loadRelicStates());

        // Ensure every relic defined in the registry has a state object.
        for (String id : plugin.getRelicRegistry().getAll().keySet()) {
            relicStates.computeIfAbsent(id, RelicState::new);
        }
    }

    public void saveAll() {
        storage.saveAll(playerData, relicStates);
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, PlayerData::new);
    }

    public Optional<RelicState> getState(String relicId) {
        return Optional.ofNullable(relicStates.get(relicId));
    }

    public Map<String, RelicState> getAllStates() {
        return relicStates;
    }

    public Optional<Relic> getOwnedRelic(Player player) {
        PlayerData data = playerData.get(player.getUniqueId());
        if (data == null || !data.ownsRelic()) {
            return Optional.empty();
        }
        return Optional.ofNullable(plugin.getRelicRegistry().get(data.getOwnedRelicId()));
    }

    public boolean playerOwnsAnyRelic(UUID uuid) {
        PlayerData data = playerData.get(uuid);
        return data != null && data.ownsRelic();
    }

    /**
     * Attempts to bind the given relic to the given player. Fails
     * if the relic is already owned or the player already owns a
     * different relic.
     */
    public boolean bind(String relicId, Player player) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        RelicState state = relicStates.get(relicId);
        if (relic == null || state == null) {
            return false;
        }
        if (state.isOwned()) {
            return false;
        }
        PlayerData data = getOrCreatePlayerData(player.getUniqueId());
        if (data.ownsRelic()) {
            return false;
        }

        state.setOwnerUuid(player.getUniqueId());
        state.setStatus(RelicStatus.OWNED);
        state.setObtainedDate(System.currentTimeMillis());
        state.setDroppedLocation(null);

        data.setOwnedRelicId(relicId);

        relic.getAbility().onBind(player);
        player.playSound(player.getLocation(), relic.getSound(), 1.0F, 1.0F);

        cancelDespawn(relicId);
        cancelRespawn(relicId);
        groundItems.remove(relicId);

        return true;
    }

    /**
     * Removes ownership from whoever currently owns the relic,
     * without dropping an item (used for admin reset/remove).
     */
    public void unbindSilently(String relicId) {
        RelicState state = relicStates.get(relicId);
        if (state == null || state.getOwnerUuid() == null) {
            return;
        }
        UUID ownerUuid = state.getOwnerUuid();
        Relic relic = plugin.getRelicRegistry().get(relicId);
        Player owner = Bukkit.getPlayer(ownerUuid);
        if (owner != null && relic != null) {
            relic.getAbility().onUnbind(owner);
        }
        PlayerData data = playerData.get(ownerUuid);
        if (data != null) {
            data.setOwnedRelicId(null);
        }
        state.setOwnerUuid(null);
        state.setStatus(RelicStatus.UNCLAIMED);
    }

    /**
     * Handles a relic owner's death: unbinds the ability from the
     * (now dead) player, drops a physical glowing item at the death
     * location, and schedules a despawn timer.
     */
    public void dropOnDeath(Player player, String relicId, Location deathLocation) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        RelicState state = relicStates.get(relicId);
        if (relic == null || state == null) {
            return;
        }

        relic.getAbility().onUnbind(player);

        PlayerData data = playerData.get(player.getUniqueId());
        if (data != null) {
            data.setOwnedRelicId(null);
        }

        state.incrementDeaths();
        state.setOwnerUuid(null);
        state.setStatus(RelicStatus.DROPPED);
        state.setDroppedLocation(deathLocation);
        state.setDroppedAt(System.currentTimeMillis());

        spawnGroundItem(relic, deathLocation);
        scheduleDespawn(relicId);

        String msg = plugin.getMessageManager().format(
                "relic.dropped", "%relic%", ColorUtil.color(relic.getDisplayName()));
        player.sendMessage(msg);
    }

    private void spawnGroundItem(Relic relic, Location location) {
        boolean glow = plugin.getConfigManager().getConfig().getBoolean("drop.glow", true);
        ItemStack stack = relic.createItemStack();

        Item item = location.getWorld().dropItem(location, stack);
        item.setGlowing(glow);
        item.setUnlimitedLifetime(true);
        item.setInvulnerable(true);
        item.setCanPlayerPickup(true);
        item.setPersistent(true);

        groundItems.put(relic.getId(), item.getUniqueId());
    }

    private void scheduleDespawn(String relicId) {
        cancelDespawn(relicId);
        long seconds = plugin.getConfigManager().getConfig().getLong("drop.despawn-seconds", 300L);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                despawn(relicId);
            }
        }.runTaskLater(plugin, seconds * 20L);
        despawnTasks.put(relicId, task);
    }

    private void cancelDespawn(String relicId) {
        BukkitTask task = despawnTasks.remove(relicId);
        if (task != null) {
            task.cancel();
        }
    }

    private void cancelRespawn(String relicId) {
        BukkitTask task = respawnTasks.remove(relicId);
        if (task != null) {
            task.cancel();
        }
    }

    private void despawn(String relicId) {
        RelicState state = relicStates.get(relicId);
        if (state == null || state.getStatus() != RelicStatus.DROPPED) {
            return;
        }
        removeGroundItem(relicId);
        state.setStatus(RelicStatus.DESPAWNED);

        Relic relic = plugin.getRelicRegistry().get(relicId);
        if (relic != null && plugin.getConfigManager().getConfig().getBoolean("broadcast.despawned", true)) {
            String msg = plugin.getMessageManager().format(
                    "relic.despawned-broadcast", "%relic%", ColorUtil.color(relic.getDisplayName()));
            Bukkit.broadcastMessage(msg);
        }

        scheduleRespawn(relicId);
    }

    private void scheduleRespawn(String relicId) {
        cancelRespawn(relicId);
        long seconds = plugin.getConfigManager().getConfig().getLong("drop.respawn-delay-seconds", 600L);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                respawnRandomly(relicId);
            }
        }.runTaskLater(plugin, seconds * 20L);
        respawnTasks.put(relicId, task);
    }

    /**
     * Respawns a relic at a random-ish safe location in the main
     * world (simple deterministic strategy; can be extended by
     * admins via /relic admin respawn to a specific location).
     */
    public void respawnRandomly(String relicId) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        RelicState state = relicStates.get(relicId);
        if (relic == null || state == null) {
            return;
        }
        org.bukkit.World world = Bukkit.getWorlds().get(0);
        Location spawn = world.getSpawnLocation().clone().add(
                (Math.random() - 0.5) * 200, 0, (Math.random() - 0.5) * 200);
        spawn.setY(world.getHighestBlockYAt(spawn) + 1);

        respawnAt(relicId, spawn);
    }

    public void respawnAt(String relicId, Location location) {
        Relic relic = plugin.getRelicRegistry().get(relicId);
        RelicState state = relicStates.get(relicId);
        if (relic == null || state == null) {
            return;
        }
        cancelDespawn(relicId);
        cancelRespawn(relicId);
        removeGroundItem(relicId);

        state.setStatus(RelicStatus.DROPPED);
        state.setDroppedLocation(location);
        state.setDroppedAt(System.currentTimeMillis());

        spawnGroundItem(relic, location);
        scheduleDespawn(relicId);

        if (plugin.getConfigManager().getConfig().getBoolean("broadcast.respawned", true)) {
            String msg = plugin.getMessageManager().format(
                    "relic.respawned-broadcast", "%relic%", ColorUtil.color(relic.getDisplayName()));
            Bukkit.broadcastMessage(msg);
        }
    }

    private void removeGroundItem(String relicId) {
        UUID itemUuid = groundItems.remove(relicId);
        if (itemUuid == null) {
            return;
        }
        var entity = Bukkit.getEntity(itemUuid);
        if (entity != null) {
            entity.remove();
        }
    }

    /**
     * Called by the pickup listener. Returns true if the item
     * consumed was a relic and pickup was handled.
     */
    public boolean handlePickup(Player player, Item itemEntity) {
        ItemStack stack = itemEntity.getItemStack();
        if (stack.getItemMeta() == null) {
            return false;
        }
        String relicId = stack.getItemMeta().getPersistentDataContainer()
                .get(RelicKeys.relicId(), PersistentDataType.STRING);
        if (relicId == null || !plugin.getRelicRegistry().exists(relicId)) {
            return false;
        }

        // Enforce one relic per player.
        PlayerData data = getOrCreatePlayerData(player.getUniqueId());
        if (data.ownsRelic()) {
            return true; // consumed the event but do not allow pickup; listener cancels pickup
        }

        cancelDespawn(relicId);
        cancelRespawn(relicId);
        groundItems.remove(relicId);

        boolean bound = bind(relicId, player);
        if (bound) {
            Relic relic = plugin.getRelicRegistry().get(relicId);
            if (plugin.getConfigManager().getConfig().getBoolean("broadcast.new-owner", true)) {
                String msg = plugin.getMessageManager().format(
                        "relic.new-owner-broadcast",
                        "%relic%", ColorUtil.color(relic.getDisplayName()),
                        "%player%", player.getName());
                Bukkit.broadcastMessage(msg);
                Sound broadcastSound = plugin.getSoundManager().getConfiguredSound("broadcast");
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.playSound(online.getLocation(), broadcastSound, 1.0F, 1.0F);
                }
            }
        }
        return true;
    }

    public AncientRelicsPlugin getPlugin() {
        return plugin;
    }
}
