package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.gui.UpgradeGuiFactory;
import com.maxi.maxiUpgrade.gui.UpgradeGuiHolder;
import com.maxi.maxiUpgrade.upgrade.UpgradeMessageService;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class UpgradeGuiInputListener implements Listener {

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeDataService data;
    private final UpgradeMessageService messages;
    private final UpgradeGuiFactory guiFactory;

    public UpgradeGuiInputListener(JavaPlugin plugin,
                                   ConfigManager config,
                                   ItemsManager items,
                                   UpgradeDataService data,
                                   UpgradeMessageService messages,
                                   UpgradeGuiFactory guiFactory) {
        this.plugin = plugin;
        this.config = config;
        this.items = items;
        this.data = data;
        this.messages = messages;
        this.guiFactory = guiFactory;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!(e.getInventory().getHolder() instanceof UpgradeGuiHolder holder)) return;

        int raw = e.getRawSlot();
        int topSize = e.getView().getTopInventory().getSize();
        boolean top = raw >= 0 && raw < topSize;

        // ✅ GUI açıkken dupe klasiklerini tamamen kapat (top/bottom fark etmez)
        if (e.getClick() == ClickType.DOUBLE_CLICK
                || e.getAction() == InventoryAction.COLLECT_TO_CURSOR
                || e.getClick() == ClickType.NUMBER_KEY
                || e.getClick() == ClickType.SWAP_OFFHAND) {
            e.setCancelled(true);
            return;
        }

        // ✅ Top inventory = tamamen bizim kontrolümüzde
        if (top) e.setCancelled(true);

        // Player inv shift-click -> GUI’ye 1 adet yerleştir
        if (!top && e.isShiftClick()) {
            ItemStack from = e.getCurrentItem();
            if (ItemStackUtil.isAir(from) || ItemStackUtil.isGuiDecor(from)) return;

            e.setCancelled(true);

            ItemStack one = from.clone();
            one.setAmount(1);

            if (!tryPlaceOne(holder, one, p)) return;

            int newAmt = from.getAmount() - 1;
            if (newAmt <= 0) e.setCurrentItem(null);
            else from.setAmount(newAmt);

            refreshNextTick(holder);
            return;
        }

        if (!top) return;

        // dekor item alınamaz
        ItemStack current = e.getCurrentItem();
        if (!ItemStackUtil.isAir(current) && ItemStackUtil.isGuiDecor(current)) return;

        // upgrade/close/info buton slotlarını input listener yönetmez
        if (raw == holder.getUpgradeButtonSlot()
                || raw == config.getInfoStarSlot()
                || raw == config.getCloseButtonSlot()) return;

        ItemStack cursor = e.getCursor();

        // Cursor boşsa: slot itemini eline al (sadece upgrade slotları)
        if (ItemStackUtil.isAir(cursor)) {
            if (!isEditableUpgradeSlot(holder, raw)) return;
            if (ItemStackUtil.isAir(current)) return;

            e.setCursor(current);
            e.setCurrentItem(null);
            refreshNextTick(holder);
            return;
        }

        // Cursor doluysa: slot boşsa koy
        if (!isEditableUpgradeSlot(holder, raw)) return;
        if (!isAllowedInSlot(raw, cursor, holder, p)) return;

        // target item slotu: 1 adet kabul et
        ItemStack one = cursor.clone();
        one.setAmount(1);
        e.setCurrentItem(one);

        int left = cursor.getAmount() - 1;
        if (left <= 0) e.setCursor(null);
        else cursor.setAmount(left);

        refreshNextTick(holder);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!(e.getInventory().getHolder() instanceof UpgradeGuiHolder)) return;

        int topSize = e.getView().getTopInventory().getSize();
        for (int raw : e.getRawSlots()) {
            if (raw >= 0 && raw < topSize) {
                e.setCancelled(true);
                return;
            }
        }
    }

    private boolean isEditableUpgradeSlot(UpgradeGuiHolder holder, int raw) {
        if (raw == holder.getItemSlot()) return true;
        if (raw == holder.getOrbSlot()) return true;
        if (raw == holder.getDustSlot()) return true;
        for (int s : holder.getStoneSlots()) if (raw == s) return true;
        return false;
    }

    private boolean isAllowedUpgradeMaterial(ItemStack it) {
        return items.isUpgradeStone(it) || items.isBlessed(it) || items.isDowngrade(it) || items.isLuckBooster(it);
    }

    private boolean isAllowedInSlot(int raw, ItemStack cursor, UpgradeGuiHolder holder, Player p) {
        if (raw == holder.getOrbSlot()) return items.isProtectionOrb(cursor);
        if (raw == holder.getDustSlot()) return items.isLuckDust(cursor);

        for (int s : holder.getStoneSlots()) {
            if (raw == s) return isAllowedUpgradeMaterial(cursor);
        }

        if (raw == holder.getItemSlot()) {
            if (config.isVanillaMode()) return true;
            if (data.isMMOItem(cursor)) return true;
            messages.send(p, "upgrade.not-upgradeable");
            return false;
        }
        return false;
    }

    private boolean tryPlaceOne(UpgradeGuiHolder holder, ItemStack one, Player p) {
        if (items.isProtectionOrb(one)) {
            if (ItemStackUtil.isAir(holder.getInventory().getItem(holder.getOrbSlot()))) {
                holder.getInventory().setItem(holder.getOrbSlot(), one);
                return true;
            }
            return false;
        }

        if (items.isLuckDust(one)) {
            if (ItemStackUtil.isAir(holder.getInventory().getItem(holder.getDustSlot()))) {
                holder.getInventory().setItem(holder.getDustSlot(), one);
                return true;
            }
            return false;
        }

        if (isAllowedUpgradeMaterial(one)) {
            for (int s : holder.getStoneSlots()) {
                if (ItemStackUtil.isAir(holder.getInventory().getItem(s))) {
                    holder.getInventory().setItem(s, one);
                    return true;
                }
            }
            return false;
        }

        if (ItemStackUtil.isAir(holder.getInventory().getItem(holder.getItemSlot()))) {
            if (config.isVanillaMode() || data.isMMOItem(one)) {
                holder.getInventory().setItem(holder.getItemSlot(), one);
                return true;
            }
            messages.send(p, "upgrade.not-upgradeable");
            return false;
        }

        return false;
    }

    private void refreshNextTick(UpgradeGuiHolder holder) {
        plugin.getServer().getScheduler().runTask(plugin, () -> guiFactory.refreshUpgradeButton(holder));
    }
}
