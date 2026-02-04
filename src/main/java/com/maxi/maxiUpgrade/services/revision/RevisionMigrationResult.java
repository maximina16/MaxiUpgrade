// src/main/java/com/maxi/maxiUpgrade/services/revision/RevisionMigrationResult.java
package com.maxi.maxiUpgrade.services.revision;

import org.bukkit.inventory.ItemStack;

public record RevisionMigrationResult(boolean migrated, ItemStack item) {
}
