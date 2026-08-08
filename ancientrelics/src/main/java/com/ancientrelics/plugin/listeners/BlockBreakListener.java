package com.ancientrelics.plugin.listeners;

import com.ancientrelics.plugin.AncientRelicsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Reserved hook for relic mining-related bonuses. The Earth Relic's
 * faster mining is currently implemented via a passive Haste potion
 * effect in {@link com.ancientrelics.plugin.abilities.impl.EarthAbility},
 * but this listener is kept so future relics can add drop
 * multipliers or block-specific effects without new plumbing.
 */
public class BlockBreakListener implements Listener {

    private final AncientRelicsPlugin plugin;

    public BlockBreakListener(AncientRelicsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        // No relic currently modifies block break drops directly.
    }
}
