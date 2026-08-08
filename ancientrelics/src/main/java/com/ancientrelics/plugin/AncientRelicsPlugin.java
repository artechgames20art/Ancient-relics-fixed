package com.ancientrelics.plugin;

import com.ancientrelics.plugin.commands.RelicCommand;
import com.ancientrelics.plugin.commands.RelicTabCompleter;
import com.ancientrelics.plugin.config.ConfigManager;
import com.ancientrelics.plugin.config.MessageManager;
import com.ancientrelics.plugin.gui.GuiManager;
import com.ancientrelics.plugin.listeners.BlockBreakListener;
import com.ancientrelics.plugin.listeners.EntityDamageListener;
import com.ancientrelics.plugin.listeners.InventoryClickListener;
import com.ancientrelics.plugin.listeners.InventoryCloseListener;
import com.ancientrelics.plugin.listeners.PlayerDeathListener;
import com.ancientrelics.plugin.listeners.PlayerFishListener;
import com.ancientrelics.plugin.listeners.PlayerInteractListener;
import com.ancientrelics.plugin.listeners.PlayerJoinQuitListener;
import com.ancientrelics.plugin.listeners.PlayerMoveListener;
import com.ancientrelics.plugin.listeners.PlayerPickupListener;
import com.ancientrelics.plugin.listeners.PlayerRespawnListener;
import com.ancientrelics.plugin.listeners.PlayerTeleportListener;
import com.ancientrelics.plugin.managers.AbilityManager;
import com.ancientrelics.plugin.managers.ParticleManager;
import com.ancientrelics.plugin.managers.RelicAdminService;
import com.ancientrelics.plugin.managers.RelicManager;
import com.ancientrelics.plugin.managers.SoundManager;
import com.ancientrelics.plugin.managers.SeasonManager;
import com.ancientrelics.plugin.relics.RelicRegistry;
import com.ancientrelics.plugin.utils.RelicKeys;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for the Ancient Relics plugin. Responsible only for
 * bootstrapping managers, registering listeners/commands, and
 * scheduling the two shared repeating tasks (particles, abilities).
 * All actual business logic lives in the managers/ package.
 */
public final class AncientRelicsPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private RelicRegistry relicRegistry;
    private RelicManager relicManager;
    private RelicAdminService relicAdminService;
    private SoundManager soundManager;
    private ParticleManager particleManager;
    private AbilityManager abilityManager;
    private GuiManager guiManager;
    private SeasonManager seasonManager;

    @Override
    public void onEnable() {
        RelicKeys.init(this);

        this.configManager = new ConfigManager(this);
        this.configManager.loadAll();

        this.messageManager = new MessageManager(this);
        this.relicRegistry = new RelicRegistry(this);
        this.relicRegistry.loadAll();

        this.relicManager = new RelicManager(this);
        this.relicManager.loadAll();

        this.relicAdminService = new RelicAdminService(this);
        this.soundManager = new SoundManager(this);
        this.guiManager = new GuiManager(this);
        this.seasonManager = new SeasonManager(this);

        this.particleManager = new ParticleManager(this);
        this.particleManager.start();

        this.abilityManager = new AbilityManager(this);
        this.abilityManager.start();

        registerCommands();
        registerListeners();
        scheduleAutosave();

        getLogger().info("AncientRelics has been enabled with " + relicRegistry.getAll().size() + " relics loaded.");
    }

    @Override
    public void onDisable() {
        if (particleManager != null) {
            particleManager.stop();
        }
        if (abilityManager != null) {
            abilityManager.stop();
        }
        if (seasonManager != null) {
            seasonManager.stop();
        }
        if (relicManager != null) {
            relicManager.saveAll();
        }
        getLogger().info("AncientRelics has been disabled.");
    }

    private void registerCommands() {
        var command = getCommand("relic");
        if (command != null) {
            RelicCommand executor = new RelicCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(new RelicTabCompleter(this));
        }
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new PlayerRespawnListener(this), this);
        pm.registerEvents(new PlayerPickupListener(this), this);
        pm.registerEvents(new InventoryClickListener(this), this);
        pm.registerEvents(new InventoryCloseListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new EntityDamageListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new PlayerFishListener(this), this);
        pm.registerEvents(new PlayerTeleportListener(this), this);
    }

    private void scheduleAutosave() {
        long minutes = configManager.getConfig().getLong("settings.save-interval-minutes", 5L);
        long ticks = minutes * 60L * 20L;
        getServer().getScheduler().runTaskTimer(this, () -> relicManager.saveAll(), ticks, ticks);
    }

    /**
     * Fully reloads config/messages/relics.yml and reapplies runtime
     * task periods, used by /relic admin reload.
     */
    public void reloadPlugin() {
        configManager.reload();
        relicRegistry.loadAll();
        particleManager.start();
        abilityManager.start();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public RelicRegistry getRelicRegistry() {
        return relicRegistry;
    }

    public RelicManager getRelicManager() {
        return relicManager;
    }

    public RelicAdminService getRelicAdminService() {
        return relicAdminService;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }
}
