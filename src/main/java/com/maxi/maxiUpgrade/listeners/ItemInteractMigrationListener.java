package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.utils.ItemApplyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ItemInteractMigrationListener implements Listener {

    private final ItemMigrationFacade facade;

    public ItemInteractMigrationListener(ItemMigrationFacade facade) {
        this.facade = facade;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // Paper’da interact hem main hem offhand gelebilir
        if (ItemApplyUtil.isMainHand(e.getHand())) {
            ItemApplyUtil.applyToMainHand(p.getInventory(), facade);
        } else {
            ItemApplyUtil.applyToOffHand(p.getInventory(), facade);
        }
    }
}
