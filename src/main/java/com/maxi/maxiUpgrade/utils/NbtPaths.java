// utils/NbtPaths.java  ✅ (senin projede utils.NbtPaths var ve addCompound yok; doğru NBT-API tipiyle yazdım)
package com.maxi.maxiUpgrade.utils;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;

public final class NbtPaths {
    private NbtPaths() {}

    public static ReadWriteNBT getOrCreatePBV(ReadWriteItemNBT root) {
        if (root == null) return null;
        ReadWriteNBT pbv = root.getCompound("PublicBukkitValues");
        if (pbv == null) {
            // NBT-API'de compound create: addCompound değil, getOrCreateCompound
            pbv = root.getOrCreateCompound("PublicBukkitValues");
        }
        return pbv;
    }
}