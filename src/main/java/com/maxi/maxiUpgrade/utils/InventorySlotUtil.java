package com.maxi.maxiUpgrade.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class InventorySlotUtil {
    private InventorySlotUtil() {}

    public static void setCurrentItem(InventoryClickEvent e, ItemStack item) {
        try {
            e.setCurrentItem(item);
        } catch (Throwable t) {
            // bazı implementasyonlarda setCurrentItem null sorun çıkarabiliyor, fallback:
            e.getInventory().setItem(e.getSlot(), item);
        }
    }
}
