package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import com.maxi.maxiUpgrade.utils.YmlItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class UpgradeButtonRenderer {

    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeDataService data;

    private volatile FileConfiguration itemsYml;
    private volatile FileConfiguration messagesYml;

    public UpgradeButtonRenderer(ConfigManager config, ItemsManager items, UpgradeDataService data,
                                 FileConfiguration itemsYml, FileConfiguration messagesYml) {
        this.config = config;
        this.items = items;
        this.data = data;
        this.itemsYml = itemsYml;
        this.messagesYml = messagesYml;
    }

    public void setItemsYml(FileConfiguration itemsYml) { this.itemsYml = itemsYml; }
    public void setMessagesYml(FileConfiguration messagesYml) { this.messagesYml = messagesYml; }

    public ItemStack render(UpgradeGuiHolder holder) {
        ItemStack target = holder.getTargetItem();

        int maxLevel = config.getMaxUpgradeLevel();
        boolean hasItem = target != null && !target.getType().isAir() && !ItemStackUtil.isGuiDecor(target);
        int level = hasItem ? data.readAnyUpgradeLevel(target) : 0;
        boolean isMax = hasItem && level >= maxLevel;

        boolean hasMainMat = holder.hasMainUpgradeMaterial();

        String key;
        if (!hasItem) key = "gui-upgrade-button-no-item";
        else if (isMax) key = "gui-upgrade-button-max-level";
        else if (!hasMainMat) key = "gui-upgrade-button-no-material";
        else key = "gui-upgrade-button-ready";

        double chance = computeDisplayedChance(holder, level);

        Map<String, String> ph = new HashMap<>();
        ph.put("{level}", String.valueOf(level));
        ph.put("{target_level}", String.valueOf(Math.min(maxLevel, level + 1)));
        ph.put("{max_level}", String.valueOf(maxLevel));
        ph.put("{chance}", formatChance(chance));

        boolean orb = holder.hasProtectionOrb();
        boolean dust = holder.hasLuckDust();
        boolean booster = holder.hasLuckBooster();

        ph.put("{orb_status}", orb ? status("gui.button.status.orb-present", "&a✔ Koruma Küresi")
                : status("gui.button.status.orb-absent", "&7○ Koruma Küresi (Opsiyonel)"));

        double dustBonus = dust ? Math.min(holder.getLuckDustAmount() * config.getLuckDustBonusPerDust(), config.getLuckDustMaxBonus()) : 0D;
        ph.put("{dust_status}", dust ? status("gui.button.status.dust-present", "&a✔ Şans Tozu (+{bonus}%)").replace("{bonus}", formatChance(dustBonus))
                : status("gui.button.status.dust-absent", "&7○ Şans Tozu (Opsiyonel)"));

        ph.put("{luck_booster_status}", booster ? status("gui.button.status.luck-booster-present", "&a✔ Şans Parşömeni (+%50)")
                : status("gui.button.status.luck-booster-absent", "&7○ Şans Parşömeni (Opsiyonel)"));

        ItemStack built = YmlItemBuilder.tryBuild(itemsYml, key, ph);
        if (built == null) return null;
        return ItemStackUtil.markGuiDecor(built);
    }

    private double computeDisplayedChance(UpgradeGuiHolder holder, int currentLevel) {
        if (holder.hasBlessedParchment()) return 100D;

        ItemStack mat = holder.findMainUpgradeMaterial();
        if (mat == null || mat.getType().isAir()) return 0D;

        double chance = items.getStoneBaseSuccessRate(mat);
        if (chance < 0D) chance = config.getUpgradeChance(currentLevel);

        if (holder.hasLuckBooster()) chance *= holder.getLuckBoosterMultiplier();

        if (holder.hasLuckDust()) {
            double bonus = holder.getLuckDustAmount() * config.getLuckDustBonusPerDust();
            bonus = Math.min(bonus, config.getLuckDustMaxBonus());
            chance += bonus;
        }

        return Math.max(0D, Math.min(100D, chance));
    }

    private String status(String path, String fallback) {
        if (messagesYml == null) return fallback.replace("&", "§");
        String s = messagesYml.getString(path);
        if (s == null || s.isBlank()) return fallback.replace("&", "§");
        return s.replace("&", "§");
    }

    private String formatChance(double d) {
        double x = Math.round(d * 10D) / 10D;
        if (Math.abs(x - Math.rint(x)) < 0.0001) return String.valueOf((int) x);
        return String.valueOf(x);
    }
}
