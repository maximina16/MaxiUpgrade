package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.managers.MMOItemsUpgradeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public final class UnbreakableMMOItemListener implements Listener {

    private final MMOItemsUpgradeManager manager;

    public UnbreakableMMOItemListener(MMOItemsUpgradeManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(PlayerItemDamageEvent e) {
        ItemStack it = e.getItem();
        if (manager.isMMOItem(it)) {
            e.setCancelled(true);
        }
    }
}
