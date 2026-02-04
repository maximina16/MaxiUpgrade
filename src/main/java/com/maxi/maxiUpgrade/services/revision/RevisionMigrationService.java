// src/main/java/com/maxi/maxiUpgrade/services/revision/RevisionMigrationService.java
package com.maxi.maxiUpgrade.services.revision;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.services.MMOItemsLoreService;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.inventory.ItemStack;

public final class RevisionMigrationService {

    private final ConfigManager config;
    private final UpgradeDataService data;
    private final MMOItemsLoreService lore;

    // ✅ artık FreshItemBuilder var
    private final FreshItemBuilder freshBuilder;

    public RevisionMigrationService(Object ignoredPlugin,
                                    ConfigManager config,
                                    UpgradeDataService data,
                                    MMOItemsRebuildService rebuild,
                                    MMOItemsLoreService lore) {
        this.config = config;
        this.data = data;
        this.lore = lore;
        this.freshBuilder = new DefaultFreshItemBuilder(rebuild);
    }

    public RevisionMigrationResult migrateIfRevisionChanged(ItemStack item) {
        if (item == null || item.getType().isAir()) return new RevisionMigrationResult(false, item);
        if (!data.isMMOItem(item)) return new RevisionMigrationResult(false, item);
        if (!config.toggles().enableRevisionMigration()) return new RevisionMigrationResult(false, item);

        int current = data.getCurrentRevision();
        int onItem = data.getRevision(item);
        if (onItem == current) return new RevisionMigrationResult(false, item);

        // ✅ revision değişmişse: fresh build + lore + revision set
        ItemStack fresh = freshBuilder.buildFresh(item);
        fresh = lore.reapplyLore(fresh, null);
        data.setRevision(fresh, current);

        return new RevisionMigrationResult(true, fresh);
    }
}
