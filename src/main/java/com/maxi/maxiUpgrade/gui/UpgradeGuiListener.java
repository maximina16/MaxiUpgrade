package com.maxi.maxiUpgrade.gui;

import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.config.MessageManager;
import com.maxi.maxiUpgrade.managers.MMOItemsUpgradeManager;
import com.maxi.maxiUpgrade.services.CooldownService;
import com.maxi.maxiUpgrade.upgrade.UpgradeResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public final class UpgradeGuiListener implements Listener {

    private final MMOItemsUpgradeManager upgrade;
    private final MessageManager messages;
    private final CooldownService cooldown;
    private final ItemsManager items;

    public UpgradeGuiListener(MMOItemsUpgradeManager upgrade, MessageManager messages, CooldownService cooldown, ItemsManager items) {
        this.upgrade = upgrade;
        this.messages = messages;
        this.cooldown = cooldown;
        this.items = items;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!(e.getInventory().getHolder() instanceof UpgradeGuiHolder holder)) return;

        e.setCancelled(true);

        int raw = e.getRawSlot();
        if (raw < 0) return;

        boolean inTop = raw < e.getInventory().getSize();

        if (!inTop) {
            if (e.isShiftClick()) {
                ItemStack move = e.getCurrentItem();
                if (move == null || move.getType().isAir()) return;
                shiftMoveIntoGui(holder, move);
            }
            return;
        }

        if (holder.isUpgradeButton(raw)) {
            if (cooldown.isOnCooldown(p)) {
                messages.send(p, "upgrade.cooldown", "%time%", String.valueOf(cooldown.getRemaining(p) / 1000.0));
                return;
            }

            cooldown.trigger(p);

            UpgradeResult result = upgrade.upgrade(holder, p);

            switch (result.getOutcome()) {
                case SUCCESS -> messages.send(p, "upgrade.success");
                case FAIL -> messages.send(p, "upgrade.fail");
                case FAIL_PROTECTED -> messages.send(p, "upgrade.fail-protected");
                case FAIL_DESTROY -> messages.send(p, "upgrade.fail-destroyed");
                case FAIL_DOWNGRADE -> {
                    if (result.getNewLevel() == 0) messages.send(p, "upgrade.fail-downgraded-zero");
                    else messages.send(p, "upgrade.fail-downgraded");
                }
            }

            holder.refresh();
            return;
        }

        if (e.isShiftClick()) {
            ItemStack move = e.getCurrentItem();
            if (move == null || move.getType().isAir()) return;
            shiftMoveIntoGui(holder, move);
            return;
        }

        ItemStack cursor = e.getCursor();
        if (cursor == null || cursor.getType().isAir()) return;

        if (canPlace(holder, raw, cursor)) {
            e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!(e.getInventory().getHolder() instanceof UpgradeGuiHolder holder)) return;

        ItemStack cursor = e.getOldCursor();
        for (int raw : e.getRawSlots()) {
            if (raw >= e.getInventory().getSize()) continue;
            if (!canPlace(holder, raw, cursor)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    private boolean canPlace(UpgradeGuiHolder holder, int slot, ItemStack stack) {
        if (slot == holder.getUpgradeButtonSlot()) return false;

        if (slot == holder.getItemSlot()) {
            return stack != null && !stack.getType().isAir();
        }

        if (slot == holder.getDustSlot()) {
            return items.isLuckDust(stack);
        }

        if (slot == holder.getOrbSlot()) {
            return items.isProtectionOrb(stack);
        }

        if (holder.getStoneSlots().contains(slot)) {
            return items.isUpgradeStone(stack) || items.isBlessed(stack) || items.isDowngrade(stack) || items.isLuckBooster(stack);
        }

        return false;
    }

    private void shiftMoveIntoGui(UpgradeGuiHolder holder, ItemStack from) {
        if (items.isLuckDust(from)) {
            placeOne(holder, holder.getDustSlot(), from);
            return;
        }

        if (items.isProtectionOrb(from)) {
            placeOne(holder, holder.getOrbSlot(), from);
            return;
        }

        if (items.isUpgradeStone(from) || items.isBlessed(from) || items.isDowngrade(from) || items.isLuckBooster(from)) {
            for (int s : holder.getStoneSlots()) {
                if (placeOne(holder, s, from)) return;
            }
            return;
        }

        placeOne(holder, holder.getItemSlot(), from);
    }

    private boolean placeOne(UpgradeGuiHolder holder, int slot, ItemStack from) {
        ItemStack cur = holder.getInventory().getItem(slot);
        if (cur != null && !cur.getType().isAir()) return false;

        ItemStack one = from.clone();
        one.setAmount(1);
        holder.getInventory().setItem(slot, one);

        from.setAmount(from.getAmount() - 1);
        if (from.getAmount() <= 0) from.setAmount(0);
        return true;
    }
}
