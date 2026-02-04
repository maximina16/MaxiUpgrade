// utils/ItemFingerprint.java
package com.maxi.maxiUpgrade.utils;

import com.maxi.maxiUpgrade.compat.LegacyKeys;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class ItemFingerprint {

    public static String fingerprint(ItemStack item) {
        if (item == null || item.getType().isAir()) return "AIR";

        return NBT.get(item, nbt -> {
            String id = nbt.getString(LegacyKeys.MMOITEMS_ITEM_ID);
            String type = nbt.getString(LegacyKeys.MMOITEMS_ITEM_TYPE);
            return Objects.toString(type, "?") + ":" + Objects.toString(id, "?");
        });
    }
}
