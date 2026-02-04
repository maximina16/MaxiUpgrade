package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.managers.MMOItemsUpgradeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class MMOItemsUpgradeListener implements Listener {

    private final MMOItemsUpgradeManager manager;

    public MMOItemsUpgradeListener(MMOItemsUpgradeManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        ItemStack it = e.getItem();
        if (it == null) return;
        manager.checkAndFixRevision(it, e.getPlayer());
    }
}
