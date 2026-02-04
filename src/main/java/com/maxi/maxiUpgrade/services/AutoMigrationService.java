// services/AutoMigrationService.java
package com.maxi.maxiUpgrade.services;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class AutoMigrationService {

    private final ConfigManager config;
    private final UpgradeDataService data;

    public AutoMigrationService(ConfigManager config, UpgradeDataService data) {
        this.config = config;
        this.data = data;
    }

    public int migrateIfNeeded(ItemStack item) {
        if (item == null || item.getType().isAir()) return 0;
        if (!data.isMMOItem(item)) return 0;

        int level = data.readAnyUpgradeLevel(item);
        if (level < 0) level = 0;

        data.setUpgradeLevel(item, level);

        Map<String, String> statKeys = config.getStatNbtKeys();
        for (String statId : statKeys.keySet()) {
            String nbtKey = statKeys.get(statId);
            if (nbtKey == null || nbtKey.isBlank()) continue;
            data.ensureBaseSnapshot(item, nbtKey);
        }

        data.cleanupLegacyNoise(item);
        return level;
    }
}
