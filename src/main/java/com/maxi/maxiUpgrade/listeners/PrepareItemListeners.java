// listeners/PrepareItemListeners.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public final class PrepareItemListeners implements Listener {

    private final ItemMigrationFacade migration;
    private final MMOItemsRebuildService rebuild;

    public PrepareItemListeners(ItemMigrationFacade migration, MMOItemsRebuildService rebuild) {
        this.migration = migration;
        this.rebuild = rebuild;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        if (e.getResult() == null) return;
        Player p = (e.getViewers().isEmpty() || !(e.getViewers().get(0) instanceof Player))
                ? null : (Player) e.getViewers().get(0);

        ItemStack out = (p != null) ? migration.apply(p, e.getResult()) : e.getResult();
        if (p != null) rebuild.rebuild(out, p);
        e.setResult(out);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrepareGrindstone(PrepareGrindstoneEvent e) {
        if (e.getResult() == null) return;
        Player p = (e.getViewers().isEmpty() || !(e.getViewers().get(0) instanceof Player))
                ? null : (Player) e.getViewers().get(0);

        ItemStack out = (p != null) ? migration.apply(p, e.getResult()) : e.getResult();
        if (p != null) rebuild.rebuild(out, p);
        e.setResult(out);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrepareSmithing(PrepareSmithingEvent e) {
        if (e.getResult() == null) return;
        Player p = (e.getViewers().isEmpty() || !(e.getViewers().get(0) instanceof Player))
                ? null : (Player) e.getViewers().get(0);

        ItemStack out = (p != null) ? migration.apply(p, e.getResult()) : e.getResult();
        if (p != null) rebuild.rebuild(out, p);
        e.setResult(out);
    }
}
