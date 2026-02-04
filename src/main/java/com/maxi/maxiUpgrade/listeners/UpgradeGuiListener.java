package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.gui.UpgradeGuiFactory;
import com.maxi.maxiUpgrade.gui.UpgradeGuiHolder;
import com.maxi.maxiUpgrade.upgrade.*;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Map;

public final class UpgradeGuiListener implements Listener {

    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeGuiFactory guiFactory;

    private final UpgradeCooldownService cooldown;
    private final UpgradeMessageService messages;
    private final UpgradeTransactionService tx;

    private final JavaPlugin plugin;

    public UpgradeGuiListener(ConfigManager config,
                              ItemsManager items,
                              UpgradeGuiFactory guiFactory,
                              UpgradeCooldownService cooldown,
                              UpgradeMessageService messages,
                              UpgradeTransactionService tx,
                              JavaPlugin plugin) {
        this.config = config;
        this.items = items;
        this.guiFactory = guiFactory;
        this.cooldown = cooldown;
        this.messages = messages;
        this.tx = tx;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(e.getInventory().getHolder() instanceof UpgradeGuiHolder holder)) return;

        int raw = e.getRawSlot();
        int topSize = e.getView().getTopInventory().getSize();
        boolean top = raw >= 0 && raw < topSize;
        if (!top) return;

        // ✅ GUI üst envanter tamamen kilit
        e.setCancelled(true);

        // dekor taşınamaz
        ItemStack cur = e.getCurrentItem();
        if (!ItemStackUtil.isAir(cur) && ItemStackUtil.isGuiDecor(cur)) return;

        // upgrade butonu sadece sağ tık
        if (raw != holder.getUpgradeButtonSlot()) return;
        if (e.getClick() != ClickType.RIGHT) return;

        long left = cooldown.getRemainingMillis(player);
        if (left > 0) {
            messages.sendCooldown(player, left);
            return;
        }
        cooldown.mark(player);

        ItemStack target = holder.getTargetItem();
        if (ItemStackUtil.isAir(target) || ItemStackUtil.isGuiDecor(target)) {
            messages.send(player, "upgrade.materials-missing");
            refreshNextTick(holder);
            return;
        }
        if (!holder.hasMainUpgradeMaterial()) {
            messages.send(player, "upgrade.materials-missing");
            refreshNextTick(holder);
            return;
        }

        UpgradeResult result = tx.perform(holder, player);
        Map<String, String> ph = messages.placeholdersFromResult(result);

        // ✅ enum isimleri sende farklı olabilir: string normalize ile map’liyoruz
        String outcome = String.valueOf(result.outcome()).toUpperCase(Locale.ROOT);

        if (outcome.contains("SUCCESS") || outcome.equals("OK") || outcome.equals("UPGRADED")) {
            messages.send(player, "upgrade.success", ph);
        } else if (outcome.contains("DOWN")) { // DOWNGRADE / DOWNGRADED / FAIL_DOWNGRADE
            messages.send(player, "upgrade.downgrade", ph);
        } else if (outcome.contains("DEST") || outcome.contains("BREAK") || outcome.contains("DELETE")) { // DESTROY / DESTROYED / BROKE
            messages.send(player, "upgrade.destroy", ph);
        } else {
            messages.send(player, "upgrade.fail", ph);
        }

        refreshNextTick(holder);
    }

    private void refreshNextTick(UpgradeGuiHolder holder) {
        plugin.getServer().getScheduler().runTask(plugin, () -> guiFactory.refreshUpgradeButton(holder));
    }
}
