// utils/ItemStackUtil.java  (NBT.modify / NBT.get ambiguity fix)
package com.maxi.maxiUpgrade.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public final class ItemStackUtil {
    private ItemStackUtil() {}

    private static final String GUI_DECOR_KEY = "MAXIUPGRADE_GUI_DECOR";

    public static boolean isAir(ItemStack it) {
        return it == null || it.getType().isAir();
    }

    public static ItemStack markGuiDecor(ItemStack it) {
        if (isAir(it)) return it;

        Consumer<ReadWriteItemNBT> c = nbt -> nbt.setBoolean(GUI_DECOR_KEY, true);
        NBT.modify(it, c); // ambiguity bitti
        return it;
    }

    public static boolean isGuiDecor(ItemStack it) {
        if (isAir(it)) return false;

        Function<ReadableItemNBT, Boolean> f = nbt -> nbt.getOrDefault(GUI_DECOR_KEY, false);
        Boolean v = NBT.get(it, f);
        return v != null && v;
    }
}
