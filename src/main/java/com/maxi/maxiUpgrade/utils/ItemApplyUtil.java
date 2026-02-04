package com.maxi.maxiUpgrade.utils;

import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ItemApplyUtil {
    private ItemApplyUtil() {}

    public static void applyToMainHand(PlayerInventory inv, ItemMigrationFacade facade) {
        ItemStack it = inv.getItemInMainHand();
        ItemStack out = facade.apply(it);
        if (out != it) inv.setItemInMainHand(out);
    }

    public static void applyToOffHand(PlayerInventory inv, ItemMigrationFacade facade) {
        ItemStack it = inv.getItemInOffHand();
        ItemStack out = facade.apply(it);
        if (out != it) inv.setItemInOffHand(out);
    }

    public static ItemStack applyAndReturn(ItemStack it, ItemMigrationFacade facade) {
        return facade.apply(it);
    }

    public static boolean isMainHand(EquipmentSlot slot) {
        return slot == null || slot == EquipmentSlot.HAND;
    }
}
