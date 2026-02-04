// gui/UpgradeGuiFactory.java
package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import com.maxi.maxiUpgrade.utils.YmlItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class UpgradeGuiFactory {

    private final ConfigManager config;
    private final ItemsManager items;

    public UpgradeGuiFactory(ConfigManager config, ItemsManager items) {
        this.config = config;
        this.items = items;
    }

    public Inventory createInventory(UpgradeGuiHolder holder) {
        Player p = holder.getPlayer();
        String title = (config.messages() != null)
                ? config.messages().getString("gui.title", "&8Upgrade")
                : "&8Upgrade";

        Inventory inv = Bukkit.createInventory(holder, 54, title);

        placeDecoration(inv);

        ItemStack info = YmlItemBuilder.tryBuild(items.itemsYml(), "gui-info-star", null);
        if (info != null) inv.setItem(config.guiSlotInfoStar(), ItemStackUtil.markGuiDecor(info));

        ItemStack close = YmlItemBuilder.tryBuild(items.itemsYml(), "gui-close-button", null);
        if (close != null) inv.setItem(config.guiSlotCloseButton(), ItemStackUtil.markGuiDecor(close));

        ItemStack emptyItem = items.getEmptySlotItem();
        if (emptyItem != null) inv.setItem(config.guiSlotItem(), ItemStackUtil.markGuiDecor(emptyItem));

        ItemStack emptyStone = items.getEmptySlotStone();
        if (emptyStone != null) inv.setItem(config.guiSlotStone(), ItemStackUtil.markGuiDecor(emptyStone));

        if (config.useStoneSystem()) {
            ItemStack emptyOrb = items.getEmptySlotOrb();
            if (emptyOrb != null) inv.setItem(config.guiSlotOrb(), ItemStackUtil.markGuiDecor(emptyOrb));

            // dust placeholder (bonus_per_dust/max_dust placeholder için build)
            ItemStack emptyDust = YmlItemBuilder.tryBuild(items.itemsYml(), "empty-slot-dust",
                    java.util.Map.of(
                            "{bonus_per_dust}", String.valueOf(config.getLuckDustBonusPerDust()),
                            "{max_dust}", String.valueOf((int) Math.floor(config.getLuckDustMaxBonus() / Math.max(0.0001, config.getLuckDustBonusPerDust())))
                    )
            );
            if (emptyDust != null) inv.setItem(config.guiSlotDust(), ItemStackUtil.markGuiDecor(emptyDust));
        }

        // upgrade button
        refreshUpgradeButton(holder);

        return inv;
    }

    public boolean isUpgradeGui(InventoryView view) {
        if (view == null) return false;
        return view.getTopInventory().getHolder() instanceof UpgradeGuiHolder;
    }

    public void refreshUpgradeButton(UpgradeGuiHolder holder) {
        if (holder == null || holder.getInventory() == null) return;

        UpgradeButtonRenderer r = new UpgradeButtonRenderer(
                config,
                items,
                new UpgradeDataService(),
                items.itemsYml(),
                config.messages()
        );

        ItemStack button = r.render(holder);
        if (button != null) holder.getInventory().setItem(holder.getUpgradeButtonSlot(), button);
    }

    private void placeDecoration(Inventory inv) {
        ItemStack top = items.getGuiDecorationTopRow();
        ItemStack second = items.getGuiDecorationSecondRow();
        ItemStack third = items.getGuiDecorationThirdRow();
        ItemStack rest = items.getGuiDecorationRest();

        for (int slot = 0; slot < inv.getSize(); slot++) {
            int row = slot / 9;
            ItemStack deco = (row == 0) ? top : (row == 1) ? second : (row == 2) ? third : rest;
            if (deco != null) inv.setItem(slot, ItemStackUtil.markGuiDecor(deco.clone()));
        }
    }
}
