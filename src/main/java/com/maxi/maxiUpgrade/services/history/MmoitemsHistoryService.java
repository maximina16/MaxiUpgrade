// services/history/MmoitemsHistoryService.java
package com.maxi.maxiUpgrade.services.history;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.manager.StatManager;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class MmoitemsHistoryService {

    public MmoitemsHistoryData readHistory(ItemStack item, String nbtKey) {
        if (item == null || item.getType().isAir()) return new MmoitemsHistoryData(0D, 0D);
        if (nbtKey == null || nbtKey.isBlank()) return new MmoitemsHistoryData(0D, 0D);

        // MMOITEMS_ATTACK_DAMAGE -> ATTACK_DAMAGE
        String statId = nbtKey.startsWith("MMOITEMS_") ? nbtKey.substring("MMOITEMS_".length()) : nbtKey;

        try {
            NBTItem nbt = NBTItem.get(item);
            LiveMMOItem live = new LiveMMOItem(nbt.getItem());

            StatManager sm = MMOItems.plugin.getStats();
            ItemStat stat = sm.get(statId);
            if (stat == null) return new MmoitemsHistoryData(0D, 0D);

            StatHistory hist = live.getStatHistory(stat);
            if (hist == null) return new MmoitemsHistoryData(0D, 0D);

            double og = 0D;
            StatData original = hist.getOriginalData();
            if (original instanceof DoubleData dd) og = dd.getValue();

            double gemExternal = 0D;

            // gemstones
            for (UUID gemId : hist.getAllGemstones()) {
                StatData d = hist.getGemstoneData(gemId);
                if (d instanceof DoubleData dd) gemExternal += dd.getValue();
            }

            // external data
            for (StatData d : hist.getExternalData()) {
                if (d instanceof DoubleData dd) gemExternal += dd.getValue();
            }

            return new MmoitemsHistoryData(og, gemExternal);
        } catch (Throwable ignored) {
            return new MmoitemsHistoryData(0D, 0D);
        }
    }
}
