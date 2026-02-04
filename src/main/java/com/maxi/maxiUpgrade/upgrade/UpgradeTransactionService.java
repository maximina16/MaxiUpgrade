// upgrade/UpgradeTransactionService.java
package com.maxi.maxiUpgrade.upgrade;

import com.maxi.maxiUpgrade.gui.UpgradeGuiHolder;
import com.maxi.maxiUpgrade.managers.MMOItemsUpgradeManager;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class UpgradeTransactionService {

    private final MMOItemsUpgradeManager upgradeManager;
    private final MMOItemsRebuildService rebuildService;

    public UpgradeTransactionService(MMOItemsUpgradeManager upgradeManager, MMOItemsRebuildService rebuildService) {
        this.upgradeManager = upgradeManager;
        this.rebuildService = rebuildService;
    }

    public UpgradeResult perform(UpgradeGuiHolder holder, Player player) {
        UpgradeResult result = upgradeManager.upgrade(holder, player);

        ItemStack target = holder.getTargetItem();
        if (target != null && !target.getType().isAir()) {
            target = rebuildService.rebuild(target, player);
            holder.setTargetItem(target);
        }

        holder.refresh();
        return result;
    }
}
