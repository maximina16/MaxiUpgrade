// listeners/AutoMigrateListener.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.AutoMigrationService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public final class AutoMigrateListener implements Listener {

    private final AutoMigrationService migrate;

    public AutoMigrateListener(AutoMigrationService migrate) {
        this.migrate = migrate;
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent e) {
        ItemStack it = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (it == null || it.getType().isAir()) return;
        migrate.migrateIfNeeded(it);
    }
}
