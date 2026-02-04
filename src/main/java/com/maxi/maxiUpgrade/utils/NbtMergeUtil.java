package com.maxi.maxiUpgrade.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class NbtMergeUtil {
    private NbtMergeUtil() {}

    /**
     * Kaynak item custom_data -> SNBT string
     */
    public static String snapshotCustomData(ItemStack src) {
        if (src == null || src.getType().isAir()) return "{}";
        return NBT.get(src, nbt -> {
            String s = nbt.toString();
            return (s == null || s.isBlank()) ? "{}" : s;
        });
    }

    /**
     * SNBT string'i hedef item custom_data'ya merge eder.
     * 1.20.5+ runtime’da NBT.modify sadece custom_data’yı hedeflediği için güvenli.
     */
    public static void mergeIntoCustomData(ItemStack dest, String snbt) {
        if (dest == null || dest.getType().isAir()) return;
        if (snbt == null || snbt.isBlank()) return;

        final NBTContainer cont = new NBTContainer(snbt);

        NBT.modify(dest, nbt -> {
            // NBTCompound'da mergeCompound metodu mevcut (API'nin eski ve yeni sürümlerinde var)
            nbt.mergeCompound(cont);
        });
    }

    /**
     * Kopyalama sonrası bazı key'leri tekrar "fresh" haline almak için temizleme helper’ı.
     */
    public static void removeKeys(ItemStack dest, Set<String> keys) {
        if (dest == null || dest.getType().isAir()) return;
        if (keys == null || keys.isEmpty()) return;

        NBT.modify(dest, nbt -> {
            for (String k : keys) {
                if (k == null || k.isBlank()) continue;
                if (nbt.hasTag(k)) nbt.removeKey(k);
            }
        });
    }
}
