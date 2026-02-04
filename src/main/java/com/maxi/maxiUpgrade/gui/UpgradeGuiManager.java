// gui/UpgradeGuiManager.java  (holder ctor + migration.apply ItemStack)
package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class UpgradeGuiManager {

    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeGuiFactory factory;
    private final ItemMigrationFacade migration;

    public UpgradeGuiManager(ConfigManager config,
                             ItemsManager items,
                             UpgradeGuiFactory factory,
                             ItemMigrationFacade migration) {
        this.config = config;
        this.items = items;
        this.factory = factory;
        this.migration = migration;
    }

    public void open(Player p) {
        UpgradeGuiHolder holder = new UpgradeGuiHolder(
                p,
                config,
                items,
                config.getItemSlot(),
                config.getOrbSlot(),
                config.getDustSlot(),
                config.getUpgradeButtonSlot(),
                config.getStoneSlots()
        );

        Inventory inv = factory.createInventory(holder);
        holder.setInventory(inv);

        ItemStack main = p.getInventory().getItemInMainHand();
        if (!ItemStackUtil.isAir(main) && ItemStackUtil.isAir(holder.getTargetItem())) {
            ItemStack migrated = migration.apply(p, main);
            holder.setTargetItem(migrated);
            p.getInventory().setItemInMainHand(null);
        } else {
            ItemStack off = p.getInventory().getItemInOffHand();
            if (!ItemStackUtil.isAir(off) && ItemStackUtil.isAir(holder.getTargetItem())) {
                ItemStack migrated = migration.apply(p, off);
                holder.setTargetItem(migrated);
                p.getInventory().setItemInOffHand(null);
            }
        }

        p.openInventory(inv);
    }
}
