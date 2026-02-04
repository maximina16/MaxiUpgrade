// services/UpgradeBonusCalculator.java
package com.maxi.maxiUpgrade.services;

import com.maxi.maxiUpgrade.compat.LegacyKeys;
import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.services.history.MmoitemsHistoryData;
import com.maxi.maxiUpgrade.services.history.MmoitemsHistoryService;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.inventory.ItemStack;

public final class UpgradeBonusCalculator {

    private final UpgradeDataService data;
    private final MmoitemsHistoryService history;

    public UpgradeBonusCalculator(UpgradeDataService data, MmoitemsHistoryService history) {
        this.data = data;
        this.history = history;
    }

    // jar mantığı: bonus = current - (base + gemExternal)
    public double computeUpgradeBonus(ItemStack item, String nbtKey) {
        if (item == null || item.getType().isAir()) return 0D;
        if (nbtKey == null || nbtKey.isBlank()) return 0D;

        double current = readDouble(item, nbtKey);

        String baseKey = LegacyKeys.BASE_PREFIX + nbtKey;
        double base = readDouble(item, baseKey);

        MmoitemsHistoryData h = history.readHistory(item, nbtKey);
        double gemExternal = Math.max(0D, h.gemValue);

        double bonus = current - (base + gemExternal);
        return Math.max(0D, bonus);
    }

    private static double readDouble(ItemStack item, String key) {
        Double v = NBT.get(item, (ReadableItemNBT nbt) -> {
            if (!nbt.hasTag(key)) return null;
            try { return nbt.getDouble(key); }
            catch (Throwable ignored) {
                try { return (double) nbt.getInteger(key); }
                catch (Throwable ignored2) { return null; }
            }
        });
        return v == null ? 0D : v;
    }
}
