// src/main/java/com/maxi/maxiUpgrade/config/FeatureToggles.java
package com.maxi.maxiUpgrade.config;

import org.bukkit.configuration.file.FileConfiguration;

public record FeatureToggles(
        boolean autoMigrationEnabled,
        boolean pickupMigrationEnabled,
        boolean inventoryMigrationEnabled,
        boolean hotbarMigrationEnabled,
        boolean guiRebuildOnOpen,
        boolean rebuildOnCommandSetLevel,
        boolean enableRevisionMigration,
        boolean debug
) {
    public FeatureToggles(FileConfiguration cfg) {
        this(
                cfg.getBoolean("features.auto-migration.enabled", true),
                cfg.getBoolean("features.auto-migration.on-pickup", true),
                cfg.getBoolean("features.auto-migration.on-inventory-open", true),
                cfg.getBoolean("features.auto-migration.on-hotbar-change", true),
                cfg.getBoolean("features.gui.rebuild-on-open", true),
                cfg.getBoolean("features.commands.rebuild-after-setlevel", true),
                cfg.getBoolean("features.revision-migration.enabled", true),
                cfg.getBoolean("features.debug", false)
        );
    }
}
