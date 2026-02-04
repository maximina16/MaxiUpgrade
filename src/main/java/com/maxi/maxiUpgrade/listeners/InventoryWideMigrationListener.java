package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.utils.InventorySlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import org.bukkit.inventory.ItemStack;

public final class InventoryWideMigrationListener implements Listener {

    private final ItemMigrationFacade facade;

    public InventoryWideMigrationListener(ItemMigrationFacade facade) {
        this.facade = facade;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        // current item
        ItemStack cur = e.getCurrentItem();
        if (cur != null && !cur.getType().isAir()) {
            ItemStack out = facade.apply(cur);
            if (out != cur) InventorySlotUtil.setCurrentItem(e, out);
        }

        // cursor item
        ItemStack cursor = e.getCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            ItemStack out = facade.apply(cursor);
            if (out != cursor) e.setCursor(out);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        ItemStack cursor = e.getOldCursor();
        if (cursor == null || cursor.getType().isAir()) return;

        ItemStack out = facade.apply(cursor);
        if (out != cursor) e.setCursor(out);
    }

    /**
     * Hopper/automation gibi inventory->inventory taşımalar.
     * (Sunucunda gerek yoksa kapatabilirsin, ama “coverage” için burada.)
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(InventoryMoveItemEvent e) {
        ItemStack it = e.getItem();
        if (it == null || it.getType().isAir()) return;

        ItemStack out = facade.apply(it);
        if (out != it) e.setItem(out);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvPickup(InventoryPickupItemEvent e) {
        ItemStack it = e.getItem().getItemStack();
        if (it == null || it.getType().isAir()) return;

        ItemStack out = facade.apply(it);
        if (out != it) e.getItem().setItemStack(out);
    }
}
