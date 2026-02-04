// src/main/java/com/maxi/maxiUpgrade/services/revision/FreshItemBuilder.java
package com.maxi.maxiUpgrade.services.revision;

import org.bukkit.inventory.ItemStack;

/**
 * "Fresh item" builder: revision migration sırasında itemi
 * MMOItems üzerinden yeniden build edip geri döndürmek için soyutlama.
 */
public interface FreshItemBuilder {
    ItemStack buildFresh(ItemStack original);
}
