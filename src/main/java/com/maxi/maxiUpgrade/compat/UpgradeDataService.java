// compat/UpgradeDataService.java  ✅ (NBT.get ambiguous + Readable/ReadWrite çakışmalarını sıfırladım)
package com.maxi.maxiUpgrade.compat;

import com.maxi.maxiUpgrade.utils.NbtPaths;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public final class UpgradeDataService {

    public boolean isMMOItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        // ✅ ambiguity çöz: explicit Function cast
        return Boolean.TRUE.equals(NBT.get(item, (Function<de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT, Boolean>) nbt ->
                nbt.hasTag(LegacyKeys.MMOITEMS_ITEM_ID)
        ));
    }

    public int readAnyUpgradeLevel(ItemStack item) {
        if (item == null || item.getType().isAir()) return 0;

        Integer lvl = NBT.get(item, (Function<de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT, Integer>) nbt -> {
            // PublicBukkitValues (read-only tarafta compound erişimi yok diye)
            // -> level okumayı direkt root üstünden de yapabiliyoruz: NBT-API root compound read destekli
            try {
                de.tr7zw.changeme.nbtapi.iface.ReadableNBT pbv = nbt.getCompound("PublicBukkitValues");
                if (pbv != null) {
                    if (pbv.hasTag(LegacyKeys.NEW_PDC_KEY)) return pbv.getInteger(LegacyKeys.NEW_PDC_KEY);
                    if (pbv.hasTag(LegacyKeys.LEGACY_PDC_KEY)) return pbv.getInteger(LegacyKeys.LEGACY_PDC_KEY);
                }
            } catch (Throwable ignored) {}

            if (nbt.hasTag(LegacyKeys.LEGACY_NBT_LEVEL)) return nbt.getInteger(LegacyKeys.LEGACY_NBT_LEVEL);
            if (nbt.hasTag(LegacyKeys.LEGACY_NOISE_LEVEL)) return nbt.getInteger(LegacyKeys.LEGACY_NOISE_LEVEL);
            return 0;
        });

        return Math.max(0, lvl == null ? 0 : lvl);
    }

    public void setUpgradeLevel(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) return;
        final int lvl = Math.max(0, level);

        NBT.modify(item, (ReadWriteItemNBT nbt) -> {
            ReadWriteNBT pbv = NbtPaths.getOrCreatePBV(nbt);
            if (pbv != null) {
                pbv.setInteger(LegacyKeys.NEW_PDC_KEY, lvl);
                pbv.setInteger(LegacyKeys.LEGACY_PDC_KEY, lvl);
            }
            nbt.setInteger(LegacyKeys.LEGACY_NBT_LEVEL, lvl);
        });
    }

    public void ensureBaseSnapshot(ItemStack item, String statNbtKey) {
        if (item == null || item.getType().isAir()) return;
        if (statNbtKey == null || statNbtKey.isBlank()) return;

        final String baseKey = LegacyKeys.BASE_PREFIX + statNbtKey;

        NBT.modify(item, (ReadWriteItemNBT nbt) -> {
            if (nbt.hasTag(baseKey)) return;

            if (!nbt.hasTag(statNbtKey)) nbt.setDouble(statNbtKey, 0D);

            double current;
            try { current = nbt.getDouble(statNbtKey); }
            catch (Throwable ignored) {
                try { current = nbt.getInteger(statNbtKey); }
                catch (Throwable ignored2) { current = 0D; }
            }

            nbt.setDouble(baseKey, current);
        });
    }

    public double getBaseStat(ItemStack item, String statNbtKey) {
        if (item == null || item.getType().isAir()) return 0D;
        if (statNbtKey == null || statNbtKey.isBlank()) return 0D;

        final String baseKey = LegacyKeys.BASE_PREFIX + statNbtKey;

        Double v = NBT.get(item, (Function<de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT, Double>) nbt -> {
            if (!nbt.hasTag(baseKey)) return null;
            try { return nbt.getDouble(baseKey); }
            catch (Throwable ignored) {
                try { return (double) nbt.getInteger(baseKey); }
                catch (Throwable ignored2) { return null; }
            }
        });

        return v == null ? 0D : v;
    }

    public String getString(ItemStack item, String key) {
        if (item == null || item.getType().isAir()) return null;
        if (key == null || key.isBlank()) return null;

        return NBT.get(item, (Function<de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT, String>) nbt -> {
            if (!nbt.hasTag(key)) return null;
            try { return nbt.getString(key); }
            catch (Throwable ignored) { return null; }
        });
    }

    public int getRevision(ItemStack item) {
        if (item == null || item.getType().isAir()) return 0;
        Integer v = NBT.get(item, (Function<de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT, Integer>) nbt -> {
            if (!nbt.hasTag(LegacyKeys.MMOITEMS_REVISION_ID)) return 0;
            return nbt.getInteger(LegacyKeys.MMOITEMS_REVISION_ID);
        });
        return v == null ? 0 : v;
    }

    public int getCurrentRevision() {
        // ✅ MMOItems tarafında "current" compile-time bilinmiyor: güvenli stub.
        // RevisionMigration istersen kapat (config), yoksa "rev değişti mi" check'i burada.
        return Integer.MAX_VALUE; // yani migrate etme (compile fix + güvenli)
    }

    public void cleanupLegacyNoise(ItemStack item) {
        if (item == null || item.getType().isAir()) return;

        NBT.modify(item, (ReadWriteItemNBT nbt) -> {
            if (nbt.hasTag(LegacyKeys.LEGACY_NOISE_LEVEL)) nbt.removeKey(LegacyKeys.LEGACY_NOISE_LEVEL);

            ReadWriteNBT pbv = NbtPaths.getOrCreatePBV(nbt);
            if (pbv != null) {
                if (pbv.hasTag(LegacyKeys.LEGACY_NOISE_DATA)) pbv.removeKey(LegacyKeys.LEGACY_NOISE_DATA);
            }
        });
    }
}
