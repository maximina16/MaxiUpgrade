// src/main/java/com/maxi/maxiUpgrade/services/ItemMigrationFacade.java
package com.maxi.maxiUpgrade.services;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.services.revision.RevisionMigrationResult;
import com.maxi.maxiUpgrade.services.revision.RevisionMigrationService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemMigrationFacade {

    private final ConfigManager config;
    private final UpgradeDataService data;
    private final RevisionMigrationService revision;
    private final MMOItemsRebuildService rebuild;
    private final MMOItemsLoreService lore;

    public ItemMigrationFacade(ConfigManager config,
                               UpgradeDataService data,
                               RevisionMigrationService revision,
                               MMOItemsRebuildService rebuild,
                               MMOItemsLoreService lore) {
        this.config = config;
        this.data = data;
        this.revision = revision;
        this.rebuild = rebuild;
        this.lore = lore;
    }

    public ItemStack apply(ItemStack item) {
        return apply(null, item);
    }

    public ItemStack apply(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return item;
        if (!data.isMMOItem(item)) return item;

        // 1) normalize + base snapshot (AutoMigrationService mantığı)
        int lvl = data.readAnyUpgradeLevel(item);
        if (lvl < 0) lvl = 0;
        data.setUpgradeLevel(item, lvl);
        data.ensureBaseSnapshotsFromConfig(item);

        // 2) revision migrate
        RevisionMigrationResult r = revision.migrateIfRevisionChanged(item);
        ItemStack out = r.item();

        // 3) rebuild (statlar) + lore (display)
        out = rebuild.rebuild(out, player);
        out = lore.reapplyLore(out, player);

        return out;
    }
}
