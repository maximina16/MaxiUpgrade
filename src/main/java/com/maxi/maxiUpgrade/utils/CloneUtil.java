package com.maxi.maxiUpgrade.utils;

import org.bukkit.inventory.ItemStack;

public final class CloneUtil {
    private CloneUtil() {}

    public static ItemStack cloneOrAir(ItemStack it) {
        if (it == null) return null;
        return it.clone();
    }
}
