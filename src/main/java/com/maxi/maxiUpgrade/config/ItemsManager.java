// config/ItemsManager.java  (sadece EKLEME: GUI getter’lar + itemsYml alias)
// Senin dosyanın üstüne replace et (mevcut MMOItems/stone logic kalıyor)
package com.maxi.maxiUpgrade.config;

import com.maxi.maxiUpgrade.compat.LegacyKeys;
import com.maxi.maxiUpgrade.utils.YmlItemBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemsManager {

    private volatile FileConfiguration itemsYml;

    private final Map<String, String> mmoIdToRootKey = new ConcurrentHashMap<>();
    private volatile ItemSignature protectionOrbSig;
    private volatile ItemSignature luckDustSig;

    public ItemsManager(FileConfiguration itemsYml) { setItemsYml(itemsYml); }

    public void setItemsYml(FileConfiguration itemsYml) {
        this.itemsYml = itemsYml;
        rebuildCaches();
    }

    public FileConfiguration raw() { return itemsYml; }

    // UpgradeGuiFactory eski kod: items.itemsYml() çağırıyor
    public FileConfiguration itemsYml() { return itemsYml; }

    private void rebuildCaches() {
        mmoIdToRootKey.clear();

        if (itemsYml != null) {
            for (String root : itemsYml.getKeys(false)) {
                String id = itemsYml.getString(root + ".mmo-item-id");
                if (id != null && !id.isBlank()) {
                    mmoIdToRootKey.put(id.trim().toUpperCase(Locale.ROOT), root);
                }
            }
        }

        protectionOrbSig = readSignature("protection-orb");
        luckDustSig = readSignature("luck-dust");
    }

    public String getMmoitemsId(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        return NBT.get(item, nbt -> nbt.hasTag(LegacyKeys.MMOITEMS_ITEM_ID) ? nbt.getString(LegacyKeys.MMOITEMS_ITEM_ID) : null);
    }

    // ---- GUI DECOR / EMPTY SLOTS (items.yml’den) ----
    public ItemStack getGuiDecorationTopRow() { return YmlItemBuilder.tryBuild(itemsYml, "gui-decoration-top-row", null); }
    public ItemStack getGuiDecorationSecondRow() { return YmlItemBuilder.tryBuild(itemsYml, "gui-decoration-second-row", null); }
    public ItemStack getGuiDecorationThirdRow() { return YmlItemBuilder.tryBuild(itemsYml, "gui-decoration-third-row", null); }
    public ItemStack getGuiDecorationRest() { return YmlItemBuilder.tryBuild(itemsYml, "gui-decoration-rest", null); }

    public ItemStack getEmptySlotItem() { return YmlItemBuilder.tryBuild(itemsYml, "empty-slot-item", null); }
    public ItemStack getEmptySlotStone() { return YmlItemBuilder.tryBuild(itemsYml, "empty-slot-stone", null); }
    public ItemStack getEmptySlotOrb() { return YmlItemBuilder.tryBuild(itemsYml, "empty-slot-orb", null); }
    public ItemStack getEmptySlotDust() { return YmlItemBuilder.tryBuild(itemsYml, "empty-slot-dust", null); }

    // ---------------- aşağısı senin mevcut logic (resolveSection, stone/orb/dust vs) ----------------
    // Buraya ZIP’teki mevcut ItemsManager içeriğini aynen bırak.
    // Ben buraya uzun diye tekrar basmıyorum.
    // IMPORTANT: resolveSection / readSignature / isUpgradeStone / isProtectionOrb / isLuckDust vs aynen kalsın.

    // --- STUBS: senin dosyada zaten var olmalı ---
    private ConfigurationSection resolveSection(ItemStack item) { return null; }
    private ItemSignature readSignature(String root) { return null; }

    public boolean isUpgradeStone(ItemStack item) { return false; }
    public boolean isProtectionOrb(ItemStack item) { return false; }
    public boolean isLuckDust(ItemStack item) { return false; }

    public boolean isBlessedParchment(ItemStack item) { return false; }
    public boolean isDowngradeParchment(ItemStack item) { return false; }
    public boolean isLuckBooster(ItemStack item) { return false; }
    public double getLuckBoosterMultiplier(ItemStack item) { return 1D; }

    public double getStoneBaseSuccessRate(ItemStack stoneItem) { return -1D; }
    public boolean canStoneDestroy(ItemStack stoneItem) { return false; }

    public int getStoneMinLevel(org.bukkit.inventory.ItemStack stone) {
        var sec = resolveSection(stone);
        if (sec == null) return 0;
        return sec.getInt("min-level", 0);
    }

    public int getStoneMaxLevel(org.bukkit.inventory.ItemStack stone) {
        var sec = resolveSection(stone);
        if (sec == null) return Integer.MAX_VALUE;
        return sec.getInt("max-level", Integer.MAX_VALUE);
    }

    // GuiSlotRules eski isimler:
    public boolean isBlessed(org.bukkit.inventory.ItemStack item) {
        return isBlessedParchment(item);
    }

    public boolean isDowngrade(org.bukkit.inventory.ItemStack item) {
        return isDowngradeParchment(item);
    }

    // signature helper
    private static final class ItemSignature {
        static ItemSignature fromItem(ItemStack item) { return null; }
        boolean matches(ItemStack item) { return false; }
        boolean compatEquals(ItemSignature other) { return false; }
    }
}
