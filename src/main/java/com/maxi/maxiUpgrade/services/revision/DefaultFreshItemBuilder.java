// src/main/java/com/maxi/maxiUpgrade/services/revision/DefaultFreshItemBuilder.java
package com.maxi.maxiUpgrade.services.revision;

import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.inventory.ItemStack;

/**
 * Varsayılan implementasyon: MMOItemsRebuildService ile rebuild eder.
 */
public final class DefaultFreshItemBuilder implements FreshItemBuilder {

    private final MMOItemsRebuildService rebuild;

    public DefaultFreshItemBuilder(MMOItemsRebuildService rebuild) {
        this.rebuild = rebuild;
    }

    @Override
    public ItemStack buildFresh(ItemStack original) {
        if (original == null) return null;
        return rebuild.rebuild(original, null);
    }
}
