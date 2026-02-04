// services/MMOItemsLoreService.java ✅ (ConfigManager.getMultiplierByNbtKey yok diye: map üzerinden bul)
package com.maxi.maxiUpgrade.services;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class MMOItemsLoreService {

    private static final Pattern GREEN_BONUS_SUFFIX = Pattern.compile("\\s*\\[\\+\\s*-?\\d+(?:[\\.,]\\d+)?\\s*\\]");

    private final ConfigManager config;
    private final UpgradeDataService data;
    private final UpgradeBonusCalculator bonusCalculator;

    public MMOItemsLoreService(ConfigManager config, UpgradeDataService data, UpgradeBonusCalculator bonusCalculator) {
        this.config = config;
        this.data = data;
        this.bonusCalculator = bonusCalculator;
    }

    public List<String> applyUpgradeSuffix(ItemStack item, List<String> jsonLore, String nbtKey, int level) {
        if (jsonLore == null || jsonLore.isEmpty()) return jsonLore;
        if (item == null || item.getType().isAir()) return jsonLore;
        if (nbtKey == null || nbtKey.isBlank()) return jsonLore;

        ConfigManager.StatMultiplier mult = null;
        for (ConfigManager.StatMultiplier m : config.getAllStatMultipliers().values()) {
            if (m == null) continue;
            if (nbtKey.equalsIgnoreCase(m.nbtKey())) { mult = m; break; }
        }
        if (mult == null) return jsonLore;

        List<String> names = mult.loreNames();
        if (names == null || names.isEmpty()) return jsonLore;

        double bonus = bonusCalculator.computeUpgradeBonus(item, nbtKey);
        if (level <= 0 || bonus <= 0.05D) return jsonLore;

        String bonusStr = fmt1(bonus);

        List<String> out = new ArrayList<>(jsonLore.size());
        for (String line : jsonLore) {
            if (line == null) { out.add(null); continue; }

            String lower = line.toLowerCase(Locale.ROOT);
            boolean match = false;
            for (String n : names) {
                if (n == null || n.isBlank()) continue;
                if (lower.contains(n.toLowerCase(Locale.ROOT))) { match = true; break; }
            }
            if (!match) { out.add(line); continue; }

            String cleaned = GREEN_BONUS_SUFFIX.matcher(line).replaceAll("");
            out.add(appendGreenJson(cleaned, bonusStr));
        }
        return out;
    }

    private static String appendGreenJson(String json, String bonus) {
        if (json == null) return null;
        int idx = json.lastIndexOf("]}");
        if (idx < 0) return json;
        String suffix = "{\"color\":\"green\",\"italic\":false,\"text\":\" [+" + escape(bonus) + "]\"}";
        return json.substring(0, idx) + "," + suffix + json.substring(idx);
    }

    private static String fmt1(double v) {
        String s = String.format(Locale.ROOT, "%.1f", v).replace(",", ".");
        if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
        return s;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
