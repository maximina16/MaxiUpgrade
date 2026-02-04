package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.utils.ItemApplyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public final class ItemSwapMigrationListener implements Listener {

    private final ItemMigrationFacade facade;

    public ItemSwapMigrationListener(ItemMigrationFacade facade) {
        this.facade = facade;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        ItemStack main = e.getMainHandItem();
        ItemStack off = e.getOffHandItem();

        ItemStack main2 = ItemApplyUtil.applyAndReturn(main, facade);
        ItemStack off2 = ItemApplyUtil.applyAndReturn(off, facade);

        if (main2 != main) e.setMainHandItem(main2);
        if (off2 != off) e.setOffHandItem(off2);

        // swap sonrası inventory’ye de garanti bas
        ItemApplyUtil.applyToMainHand(p.getInventory(), facade);
        ItemApplyUtil.applyToOffHand(p.getInventory(), facade);
    }
}
