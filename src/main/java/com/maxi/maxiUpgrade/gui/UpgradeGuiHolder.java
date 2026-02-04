// gui/UpgradeGuiHolder.java  (isUpgradeButton missing fix)
package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeGuiHolder implements InventoryHolder {

    private final Player player;
    private final ConfigManager config;
    private final ItemsManager items;

    private Inventory inventory;

    private final int itemSlot;
    private final int orbSlot;
    private final int dustSlot;
    private final int upgradeButtonSlot;
    private final List<Integer> stoneSlots;

    public UpgradeGuiHolder(Player player,
                            ConfigManager config,
                            ItemsManager items,
                            int itemSlot,
                            int orbSlot,
                            int dustSlot,
                            int upgradeButtonSlot,
                            List<Integer> stoneSlots) {
        this.player = player;
        this.config = config;
        this.items = items;
        this.itemSlot = itemSlot;
        this.orbSlot = orbSlot;
        this.dustSlot = dustSlot;
        this.upgradeButtonSlot = upgradeButtonSlot;
        this.stoneSlots = Collections.unmodifiableList(new ArrayList<>(stoneSlots));
    }

    public Player getPlayer() { return player; }

    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    @Override
    public Inventory getInventory() { return inventory; }

    public int getItemSlot() { return itemSlot; }
    public int getOrbSlot() { return orbSlot; }
    public int getDustSlot() { return dustSlot; }
    public int getUpgradeButtonSlot() { return upgradeButtonSlot; }
    public List<Integer> getStoneSlots() { return stoneSlots; }

    public boolean isUpgradeButton(int slot) { return slot == upgradeButtonSlot; }

    public ItemStack getTargetItem() { return inventory == null ? null : inventory.getItem(itemSlot); }

    public void setTargetItem(ItemStack item) {
        if (inventory == null) return;
        inventory.setItem(itemSlot, item);
    }

    public void clearTargetItem() {
        if (inventory == null) return;
        inventory.setItem(itemSlot, null);
    }

    public boolean hasMainUpgradeMaterial() { return !ItemStackUtil.isAir(findMainUpgradeMaterial()); }

    public ItemStack findMainUpgradeMaterial() {
        if (inventory == null) return null;
        for (int s : stoneSlots) {
            ItemStack it = inventory.getItem(s);
            if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) continue;
            if (items.isUpgradeStone(it)) return it;
        }
        for (int s : stoneSlots) {
            ItemStack it = inventory.getItem(s);
            if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) continue;
            return it;
        }
        return null;
    }

    public boolean hasBlessedParchment() { return findFirst(items::isBlessedParchment) != null; }
    public boolean hasDowngradeParchment() { return findFirst(items::isDowngradeParchment) != null; }
    public boolean hasLuckBooster() { return findFirst(items::isLuckBooster) != null; }

    public double getLuckBoosterMultiplier() {
        ItemStack b = findFirst(items::isLuckBooster);
        return b == null ? 1D : items.getLuckBoosterMultiplier(b);
    }

    public boolean hasProtectionOrb() {
        if (inventory == null) return false;
        ItemStack it = inventory.getItem(orbSlot);
        return !ItemStackUtil.isAir(it) && !ItemStackUtil.isGuiDecor(it) && items.isProtectionOrb(it);
    }

    public boolean hasLuckDust() {
        if (inventory == null) return false;
        ItemStack it = inventory.getItem(dustSlot);
        return !ItemStackUtil.isAir(it) && !ItemStackUtil.isGuiDecor(it) && items.isLuckDust(it);
    }

    public int getLuckDustAmount() {
        if (!hasLuckDust()) return 0;
        ItemStack it = inventory.getItem(dustSlot);
        return it == null ? 0 : it.getAmount();
    }

    public void consumeMainUpgradeMaterialOne() {
        if (inventory == null) return;
        for (int s : stoneSlots) {
            ItemStack it = inventory.getItem(s);
            if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) continue;
            decOne(s);
            return;
        }
    }

    public void consumeUsedParchment(boolean blessedUsed, boolean downgradeUsed) {
        if (inventory == null) return;
        if (blessedUsed) consumeFirst(items::isBlessedParchment);
        else if (downgradeUsed) consumeFirst(items::isDowngradeParchment);
    }

    public void consumeLuckDustAll() {
        if (inventory == null) return;
        inventory.setItem(dustSlot, null);
    }

    public void consumeLuckBoosterOne() {
        if (inventory == null) return;
        consumeFirst(items::isLuckBooster);
    }

    public void consumeProtectionOrbOne() {
        if (inventory == null) return;
        decOne(orbSlot);
    }

    private interface ItemPredicate { boolean test(ItemStack it); }

    private ItemStack findFirst(ItemPredicate pred) {
        if (inventory == null) return null;
        for (int s : stoneSlots) {
            ItemStack it = inventory.getItem(s);
            if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) continue;
            if (pred.test(it)) return it;
        }
        return null;
    }

    private void consumeFirst(ItemPredicate pred) {
        for (int s : stoneSlots) {
            ItemStack it = inventory.getItem(s);
            if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) continue;
            if (!pred.test(it)) continue;
            decOne(s);
            return;
        }
    }

    private void decOne(int slot) {
        ItemStack it = inventory.getItem(slot);
        if (ItemStackUtil.isAir(it) || ItemStackUtil.isGuiDecor(it)) return;
        int amt = it.getAmount();
        if (amt <= 1) {
            inventory.setItem(slot, null);
            return;
        }
        it.setAmount(amt - 1);
        inventory.setItem(slot, it);
    }

    public void refresh() {
        // NO-OP
    }
}
