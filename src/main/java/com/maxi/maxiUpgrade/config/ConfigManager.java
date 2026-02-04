// config/ConfigManager.java
package com.maxi.maxiUpgrade.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public final class ConfigManager {

    public enum StatType { PERCENTAGE, FLAT }

    public record StatMultiplier(String id, String nbtKey, List<String> loreNames, StatType type, double value) {}

    private volatile FileConfiguration config;
    private volatile FileConfiguration messages;

    public ConfigManager(FileConfiguration config) {
        this(config, null);
    }

    public ConfigManager(FileConfiguration config, FileConfiguration messages) {
        this.config = config;
        this.messages = messages;
    }

    public void setConfig(FileConfiguration config) { this.config = config; }
    public void setMessages(FileConfiguration messages) { this.messages = messages; }

    public FileConfiguration raw() { return config; }
    public FileConfiguration messages() { return messages; }

    // ---------------- basic flags ----------------
    public boolean isVanillaMode() { return config.getBoolean("vanilla-mode", false); }
    public boolean isBurnItems() { return config.getBoolean("burn-items", true); }
    public boolean isDebug() { return config.getBoolean("debug", false); }

    public long getUpgradeCooldown() { return config.getLong("upgrade-cooldown", 1000L); }
    public int getMaxUpgradeLevel() { return config.getInt("max-upgrade-level", 25); }

    public boolean isStoneSystem() { return config.getBoolean("use-stone-system", true); }
    public boolean useStoneSystem() { return isStoneSystem(); }

    public boolean isCloseInventoryOnUpgrade() { return config.getBoolean("gui.close-inventory-on-upgrade", true); }
    public boolean closeInventoryOnUpgrade() { return isCloseInventoryOnUpgrade(); }

    public boolean isMultiSlotEnabled() { return config.getBoolean("gui.use-multi-slot", false); }

    // ---------------- GUI slots (get + alias) ----------------
    public int getItemSlot() { return config.getInt("gui.slots.item", 31); }
    public int getStoneSlot() { return config.getInt("gui.slots.stone", 33); }
    public int getOrbSlot() { return config.getInt("gui.slots.orb", 38); }
    public int getDustSlot() { return config.getInt("gui.slots.dust", 29); }
    public int getUpgradeButtonSlot() { return config.getInt("gui.slots.upgrade-button", 42); }
    public int getInfoStarSlot() { return config.getInt("gui.slots.info-star", 4); }
    public int getCloseButtonSlot() { return config.getInt("gui.slots.close-button", 0); }

    // ZIP’te bazı sınıflar guiSlotX isimlerini çağırıyor
    public int guiSlotItem() { return getItemSlot(); }
    public int guiSlotStone() { return getStoneSlot(); }
    public int guiSlotOrb() { return getOrbSlot(); }
    public int guiSlotDust() { return getDustSlot(); }
    public int guiSlotUpgradeButton() { return getUpgradeButtonSlot(); }
    public int guiSlotInfoStar() { return getInfoStarSlot(); }
    public int guiSlotCloseButton() { return getCloseButtonSlot(); }

    public List<Integer> getStoneSlots() {
        List<Integer> list = config.getIntegerList("gui.slots.stone-slots");
        if (list == null || list.isEmpty()) return List.of(getStoneSlot());
        return List.copyOf(list);
    }

    // ---------------- chances / dust ----------------
    public double getLuckDustBonusPerDust() { return config.getDouble("luck-dust.bonus-per-dust", 2D); }
    public double getLuckDustMaxBonus() { return config.getDouble("luck-dust.max-bonus", 50D); }

    // bazı GUI kodları bu alias’ı çağırıyor
    public double luckDustBonusPerDust() { return getLuckDustBonusPerDust(); }

    public double getUpgradeChance(int currentLevel) {
        if (!config.getBoolean("upgrade-chances.enabled", true)) {
            return config.getDouble("upgrade-chances.default-chance", 100D);
        }
        ConfigurationSection sec = config.getConfigurationSection("upgrade-chances.chances");
        if (sec != null && sec.contains(String.valueOf(currentLevel))) {
            return sec.getDouble(String.valueOf(currentLevel), config.getDouble("upgrade-chances.default-chance", 100D));
        }
        return config.getDouble("upgrade-chances.default-chance", 100D);
    }

    // ---------------- migration ----------------
    public FeatureToggles loadFeatureToggles() {
        boolean enabled = config.getBoolean("migration.enabled", true);

        ConfigurationSection cov = config.getConfigurationSection("migration.coverage");
        boolean interact = cov == null || cov.getBoolean("interact", true);
        boolean swap = cov == null || cov.getBoolean("swap", true);
        boolean click = cov == null || cov.getBoolean("click", true);
        boolean drag = cov == null || cov.getBoolean("drag", true);
        boolean entityPickup = cov == null || cov.getBoolean("entity-pickup", true);
        boolean invMove = cov != null && cov.getBoolean("inventory-move", false);
        boolean invPickup = cov != null && cov.getBoolean("inventory-pickup", false);

        return new FeatureToggles(enabled, interact, swap, click, drag, entityPickup, invMove, invPickup);
    }

    public long getMicroCacheMs() { return config.getLong("migration.micro-cache-ms", 300L); }

    // ---------------- stat-multipliers ----------------
    public Map<String, String> getStatNbtKeys() {
        Map<String, String> out = new LinkedHashMap<>();
        ConfigurationSection sec = config.getConfigurationSection("stat-multipliers");
        if (sec == null) return out;

        for (String id : sec.getKeys(false)) {
            String key = sec.getString(id + ".nbt-key");
            if (key != null && !key.isBlank()) out.put(id, key);
        }
        return out;
    }

    public java.util.Map<String, String> getStatLoreNames() {
        java.util.Map<String, String> out = new java.util.LinkedHashMap<>();
        var sec = config.getConfigurationSection("stat-multipliers");
        if (sec == null) return out;

        for (String id : sec.getKeys(false)) {
            // sende list yerine tek string label kullanılıyor
            String label = sec.getString(id + ".lore-name");
            if (label == null || label.isBlank()) {
                // geriye uyum: lore-names list varsa ilkini al
                java.util.List<String> list = sec.getStringList(id + ".lore-names");
                if (list != null && !list.isEmpty()) label = list.get(0);
            }
            if (label != null && !label.isBlank()) out.put(id, label);
        }
        return out;
    }


    public String getStatNbtKey(String statId) {
        if (statId == null) return null;
        return config.getString("stat-multipliers." + statId + ".nbt-key");
    }

    public Map<String, StatMultiplier> getAllStatMultipliers() {
        Map<String, StatMultiplier> out = new LinkedHashMap<>();
        ConfigurationSection sec = config.getConfigurationSection("stat-multipliers");
        if (sec == null) return out;

        for (String id : sec.getKeys(false)) {
            String nbtKey = sec.getString(id + ".nbt-key");
            if (nbtKey == null || nbtKey.isBlank()) continue;

            List<String> loreNames = sec.getStringList(id + ".lore-names");
            String typeRaw = sec.getString(id + ".type", "PERCENTAGE");
            StatType type;
            try { type = StatType.valueOf(typeRaw.trim().toUpperCase(Locale.ROOT)); }
            catch (Throwable ignored) { type = StatType.PERCENTAGE; }

            double value = sec.getDouble(id + ".value", 1D);

            out.put(id, new StatMultiplier(id, nbtKey, loreNames == null ? List.of() : List.copyOf(loreNames), type, value));
        }
        return out;
    }
}
