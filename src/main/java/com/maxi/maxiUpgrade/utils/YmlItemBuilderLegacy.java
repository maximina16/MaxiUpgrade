// utils/YmlItemBuilderLegacy.java ✅ BU DOSYAYI BU İSİMLE OLUŞTUR (senin dosyada class adı yanlışmış)
package com.maxi.maxiUpgrade.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class YmlItemBuilderLegacy {

    private YmlItemBuilderLegacy() {}

    // ✅ YmlItemBuilder buildFromSection buraya delegasyon yapıyor
    public static ItemStack buildFromSection(ConfigurationSection sec, Map<String, String> placeholders) {
        // Senin eski implementasyonun neredeyse buraya taşı.
        // Şimdilik compile-safe minimal dönüş:
        try {
            String mat = sec.getString("material", "STONE");
            org.bukkit.Material m = org.bukkit.Material.matchMaterial(mat);
            if (m == null) m = org.bukkit.Material.STONE;
            return new ItemStack(m);
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[MaxiUpgrade] YmlItemBuilderLegacy error: " + t.getMessage());
            return new ItemStack(org.bukkit.Material.STONE);
        }
    }
}
