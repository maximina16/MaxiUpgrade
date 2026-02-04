// listeners/MMOItemsGemListener.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.MaxiUpgradePlugin;
import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class MMOItemsGemListener implements Listener {

    private final MaxiUpgradePlugin plugin;
    private final MMOItemsRebuildService rebuild;
    private final UpgradeDataService data;

    public MMOItemsGemListener(MaxiUpgradePlugin plugin, MMOItemsRebuildService rebuild, UpgradeDataService data) {
        this.plugin = plugin;
        this.rebuild = rebuild;
        this.data = data;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        // Gem socket işlemleri sonrası lore/stat refresh için "best effort"
        ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) return;
        if (!data.isMMOItem(it)) return;

        try {
            ItemStack rebuilt = rebuild.rebuild(it, e.getWhoClicked() instanceof org.bukkit.entity.Player p ? p : null);
            e.setCurrentItem(rebuilt);
        } catch (Throwable t) {
            plugin.getLogger().warning("MMOItemsGemListener error: " + t.getMessage());
        }
    }
}
