// listeners/InventoryCoverageListener.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.config.FeatureToggles;
import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class InventoryCoverageListener implements Listener {

    private final ItemMigrationFacade migration;
    private final MMOItemsRebuildService rebuild;
    private final FeatureToggles toggles;

    public InventoryCoverageListener(ItemMigrationFacade migration,
                                     MMOItemsRebuildService rebuild,
                                     FeatureToggles toggles) {
        this.migration = migration;
        this.rebuild = rebuild;
        this.toggles = toggles;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!toggles.autoMigrateOnInventory()) return;

        ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) return;

        if (migration.migrateIfNeeded(it)) {
            ItemStack rebuilt = rebuild.rebuild(it, null);
            e.setCurrentItem(rebuilt);
        }
    }
}
