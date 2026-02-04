package com.maxi.maxiUpgrade.utils;

import org.bukkit.inventory.ItemStack;

public final class StackUtil {
    private StackUtil() {}

    public static boolean isEmpty(ItemStack it) {
        return it == null || it.getType().isAir() || it.getAmount() <= 0;
    }

    public static void decrement(ItemStack it, int amount) {
        if (isEmpty(it)) return;
        int a = it.getAmount() - amount;
        if (a <= 0) it.setAmount(0);
        else it.setAmount(a);
    }
}
