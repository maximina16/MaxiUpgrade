// managers/MMOItemsUpgradeManager.java  (listenerların istediği iki method eklendi)
package com.maxi.maxiUpgrade.managers;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.gui.UpgradeGuiHolder;
import com.maxi.maxiUpgrade.services.AutoMigrationService;
import com.maxi.maxiUpgrade.services.MMOItemsLoreService;
import com.maxi.maxiUpgrade.upgrade.UpgradeOutcome;
import com.maxi.maxiUpgrade.upgrade.UpgradeResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class MMOItemsUpgradeManager {

    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeDataService data;
    private final AutoMigrationService migrate;
    private final MMOItemsLoreService lore;

    public MMOItemsUpgradeManager(ConfigManager config,
                                  ItemsManager items,
                                  UpgradeDataService data,
                                  AutoMigrationService migrate,
                                  MMOItemsLoreService lore) {
        this.config = config;
        this.items = items;
        this.data = data;
        this.migrate = migrate;
        this.lore = lore;
    }

    public boolean isMMOItem(ItemStack item) {
        return data.isMMOItem(item);
    }

    public ItemStack checkAndFixRevision(ItemStack item, Player player) {
        // şimdilik: auto migrate + normalize
        migrate.migrateIfNeeded(item);
        return item;
    }

    public UpgradeResult upgrade(UpgradeGuiHolder holder, Player player) {
        ItemStack target = holder.getTargetItem();
        if (target == null || target.getType().isAir()) {
            return new UpgradeResult(UpgradeOutcome.FAIL, 0, 0, 0D, 0, 0D, 1D, false, false, false, false);
        }
        if (!data.isMMOItem(target)) {
            int lvl = data.readAnyUpgradeLevel(target);
            return new UpgradeResult(UpgradeOutcome.FAIL, lvl, lvl, 0D, 0, 0D, 1D, false, false, false, false);
        }

        int current = migrate.migrateIfNeeded(target);
        int maxLevel = config.getMaxUpgradeLevel();
        if (current >= maxLevel) {
            return new UpgradeResult(UpgradeOutcome.FAIL, current, current, 0D, 0, 0D, 1D, false, false, false, false);
        }

        ItemStack mat = holder.findMainUpgradeMaterial();
        if (mat == null || mat.getType().isAir()) {
            return new UpgradeResult(UpgradeOutcome.FAIL, current, current, 0D, 0, 0D, 1D, false, false, false, false);
        }

        int min = items.getStoneMinLevel(mat);
        int max = items.getStoneMaxLevel(mat);
        if (min != 0 || max != Integer.MAX_VALUE) {
            if (current < min || current > max) {
                return new UpgradeResult(UpgradeOutcome.FAIL, current, current, 0D, 0, 0D, 1D, false, false, false, false);
            }
        }

        boolean blessed = holder.hasBlessedParchment();
        boolean downgrade = !blessed && holder.hasDowngradeParchment();
        boolean booster = holder.hasLuckBooster();
        boolean orb = holder.hasProtectionOrb();

        double chance = items.getStoneBaseSuccessRate(mat);
        if (chance < 0D) chance = config.getUpgradeChance(current);

        double boosterMult = 1D;
        if (booster) {
            boosterMult = holder.getLuckBoosterMultiplier();
            chance *= boosterMult;
        }

        int dustAmount = 0;
        double dustBonus = 0D;
        if (holder.hasLuckDust()) {
            dustAmount = holder.getLuckDustAmount();
            dustBonus = dustAmount * config.getLuckDustBonusPerDust();
            dustBonus = Math.min(dustBonus, config.getLuckDustMaxBonus());
            chance += dustBonus;
        }

        chance = Math.max(0D, Math.min(100D, chance));
        boolean success = blessed || ThreadLocalRandom.current().nextDouble(100D) < chance;

        holder.consumeMainUpgradeMaterialOne();
        holder.consumeUsedParchment(blessed, downgrade);
        if (dustAmount > 0) holder.consumeLuckDustAll();
        if (booster) holder.consumeLuckBoosterOne();

        if (success) {
            int newLevel = current + 1;
            data.setUpgradeLevel(target, newLevel);
            holder.setTargetItem(target);
            return new UpgradeResult(UpgradeOutcome.SUCCESS, current, newLevel, chance, dustAmount, dustBonus, boosterMult,
                    blessed, downgrade, booster, false);
        }

        if (downgrade) {
            int newLevel = Math.max(0, current - 1);
            data.setUpgradeLevel(target, newLevel);
            holder.setTargetItem(target);
            return new UpgradeResult(UpgradeOutcome.FAIL_DOWNGRADE, current, newLevel, chance, dustAmount, dustBonus, boosterMult,
                    false, true, booster, false);
        }

        if (orb) {
            holder.consumeProtectionOrbOne();
            return new UpgradeResult(UpgradeOutcome.FAIL_PROTECTED, current, current, chance, dustAmount, dustBonus, boosterMult,
                    false, false, booster, true);
        }

        if (config.isBurnItems() && items.canStoneDestroy(mat)) {
            holder.clearTargetItem();
            return new UpgradeResult(UpgradeOutcome.FAIL_DESTROY, current, 0, chance, dustAmount, dustBonus, boosterMult,
                    false, false, booster, false);
        }

        return new UpgradeResult(UpgradeOutcome.FAIL, current, current, chance, dustAmount, dustBonus, boosterMult,
                false, false, booster, false);
    }
}
