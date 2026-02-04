package com.maxi.maxiUpgrade.compat;

import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;

public final class MMOItemUtil {
    private MMOItemUtil() {}

    public static boolean hasStat(ItemStack item, String key) {
        if (item == null) return false;
        return NBTItem.get(item).hasTag(key);
    }

    public static double getStat(ItemStack item, String key) {
        if (item == null) return 0D;
        NBTItem nbt = NBTItem.get(item);
        return nbt.hasTag(key) ? nbt.getDouble(key) : 0D;
    }

    public static void setStat(ItemStack item, String key, double value) {
        if (item == null) return;
        NBTItem nbt = NBTItem.get(item);
        nbt.setDouble(key, value);
    }
}
