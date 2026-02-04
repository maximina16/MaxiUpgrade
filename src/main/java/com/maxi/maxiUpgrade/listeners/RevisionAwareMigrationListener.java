package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import com.maxi.maxiUpgrade.services.revision.RevisionMigrationResult;
import com.maxi.maxiUpgrade.services.revision.RevisionMigrationService;
import com.maxi.maxiUpgrade.utils.InventorySlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Revision değişmişse: RevisionMigrationService ile fresh+merge yapıp slot’a bas
 * Revision aynıysa: MMOItemsRebuildService ile sadece rerender pipeline (fresh lore + merge + suffix)
 */
public final class RevisionAwareMigrationListener implements Listener {

    private final RevisionMigrationService revision;
    private final MMOItemsRebuildService rebuild;

    public RevisionAwareMigrationListener(RevisionMigrationService revision,
                                          MMOItemsRebuildService rebuild) {
        this.revision = revision;
        this.rebuild = rebuild;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();

        ItemStack it = p.getInventory().getItem(e.getNewSlot());
        if (it == null || it.getType().isAir()) return;

        RevisionMigrationResult r = revision.migrateIfRevisionChanged(it);
        if (r.migrated) {
            p.getInventory().setItem(e.getNewSlot(), r.item);
            return;
        }

        // revision aynıysa rerender (safe)
        ItemStack rebuilt = rebuild.rebuildKeepingData(it);
        if (rebuilt != it) {
            p.getInventory().setItem(e.getNewSlot(), rebuilt);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        ItemStack current = e.getCurrentItem();
        if (current == null || current.getType().isAir()) return;

        RevisionMigrationResult r = revision.migrateIfRevisionChanged(current);
        if (r.migrated) {
            InventorySlotUtil.setCurrentItem(e, r.item);
            return;
        }

        ItemStack rebuilt = rebuild.rebuildKeepingData(current);
        if (rebuilt != current) {
            InventorySlotUtil.setCurrentItem(e, rebuilt);
        }
    }
}
