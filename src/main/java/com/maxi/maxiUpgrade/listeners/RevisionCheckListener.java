// listeners/RevisionCheckListener.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.MaxiUpgradePlugin;
import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class RevisionCheckListener implements Listener {

    private final MaxiUpgradePlugin plugin;
    private final ItemMigrationFacade migration;
    private final MMOItemsRebuildService rebuild;

    public RevisionCheckListener(MaxiUpgradePlugin plugin, ItemMigrationFacade migration, MMOItemsRebuildService rebuild) {
        this.plugin = plugin;
        this.migration = migration;
        this.rebuild = rebuild;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // join'de eldeki itemi en azından güncelle
        try {
            ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
            if (hand == null || hand.getType().isAir()) return;

            if (migration.migrateIfNeeded(hand)) {
                hand = rebuild.rebuild(hand, e.getPlayer());
                e.getPlayer().getInventory().setItemInMainHand(hand);
                e.getPlayer().updateInventory();
            }
        } catch (Throwable t) {
            plugin.getLogger().warning("RevisionCheckListener error: " + t.getMessage());
        }
    }
}
