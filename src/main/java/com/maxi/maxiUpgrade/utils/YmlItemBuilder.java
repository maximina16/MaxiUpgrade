// utils/YmlItemBuilder.java  ✅ ItemsManager + GUI'nin aradığı tryBuild(...) SIGNATURE'LARI BURADA
package com.maxi.maxiUpgrade.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public final class YmlItemBuilder {

    private YmlItemBuilder() {}

    // ✅ ItemsManager/GUI bunu arıyor (placeholders null gelebiliyor)
    public static ItemStack tryBuild(FileConfiguration cfg, String path, Map<String, String> placeholders) {
        if (cfg == null || path == null || path.isBlank()) return null;

        try {
            ConfigurationSection sec = cfg.getConfigurationSection(path);
            if (sec == null) return null;

            Map<String, String> ph = placeholders == null ? Collections.emptyMap() : placeholders;

            // önce legacy builder dene
            try {
                return YmlItemBuilderLegacy.buildFromSection(sec, ph);
            } catch (Throwable ignored) {}

            // sonra generic fallback (legacy yoksa)
            return buildFromSection(sec, ph);
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[MaxiUpgrade] YmlItemBuilder.tryBuild failed: " + t.getMessage());
            return null;
        }
    }

    // ✅ bazı yerler placeholders'sız çağırabilir
    public static ItemStack tryBuild(FileConfiguration cfg, String path) {
        return tryBuild(cfg, path, null);
    }

    // ✅ internal fallback
    public static ItemStack buildFromSection(ConfigurationSection sec, Map<String, String> placeholders) {
        if (sec == null) return null;

        try {
            String mat = sec.getString("material", "STONE");
            org.bukkit.Material m = org.bukkit.Material.matchMaterial(mat);
            if (m == null) m = org.bukkit.Material.STONE;
            return new ItemStack(m);
        } catch (Throwable t) {
            return new ItemStack(org.bukkit.Material.STONE);
        }
    }
}
