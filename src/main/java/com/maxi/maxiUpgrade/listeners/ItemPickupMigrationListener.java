package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class ItemPickupMigrationListener implements Listener {

    private final ItemMigrationFacade facade;

    public ItemPickupMigrationListener(ItemMigrationFacade facade) {
        this.facade = facade;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        ItemStack stack = e.getItem().getItemStack();
        if (stack == null || stack.getType().isAir()) return;

        ItemStack out = facade.apply(stack);
        if (out != stack) {
            e.getItem().setItemStack(out);
        }
    }
}
