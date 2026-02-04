package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.config.ItemsManager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class GuiSlotRules {

    private GuiSlotRules() {}

    public static boolean isItemSlot(int slot, int itemSlot) {
        return slot == itemSlot;
    }

    public static boolean isOrbSlot(int slot, int orbSlot) {
        return slot == orbSlot;
    }

    public static boolean isDustSlot(int slot, int dustSlot) {
        return slot == dustSlot;
    }

    public static boolean isStoneSlot(int slot, List<Integer> stoneSlots) {
        return stoneSlots.contains(slot);
    }

    public static boolean canGoInItemSlot(ItemStack stack) {
        return stack != null && !stack.getType().isAir();
    }

    public static boolean canGoInDustSlot(ItemStack stack, ItemsManager items) {
        return stack != null && !stack.getType().isAir() && items.isLuckDust(stack);
    }

    public static boolean canGoInOrbSlot(ItemStack stack, ItemsManager items) {
        return stack != null && !stack.getType().isAir() && items.isProtectionOrb(stack);
    }

    public static boolean canGoInStoneSlots(ItemStack stack, ItemsManager items) {
        if (stack == null || stack.getType().isAir()) return false;

        // ✅ parşömen slot iptal: hepsi stone slotlarından giriyor
        return items.isUpgradeStone(stack)
                || items.isBlessed(stack)
                || items.isDowngrade(stack)
                || items.isLuckBooster(stack);
    }
}
