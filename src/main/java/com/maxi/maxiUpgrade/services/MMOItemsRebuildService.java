package com.maxi.maxiUpgrade.services;

import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.services.history.MmoitemsHistoryData;
import com.maxi.maxiUpgrade.services.history.MmoitemsHistoryService;
import com.maxi.maxiUpgrade.utils.LoreMetaUtil;
import com.maxi.maxiUpgrade.utils.Text;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MMOItemsRebuildService {

    private static final Pattern NUMBER = Pattern.compile("-?\\d+(?:[\\.,]\\d+)?");
    private static final Pattern BONUS_SUFFIX_TEXT = Pattern.compile("\\s*\\[\\+\\s*-?\\d+(?:[\\.,]\\d+)?\\s*\\]");
    private static final Pattern TEXT_FIELD = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"");
    private static final double BONUS_EPS = 0.05D;

    private final ConfigManager config;
    private final UpgradeDataService data;
    private final AutoMigrationService autoMigration;
    private final MMOItemsLoreService loreService;
    private final MmoitemsHistoryService history;

    public MMOItemsRebuildService(ConfigManager config,
                                  UpgradeDataService data,
                                  AutoMigrationService autoMigration,
                                  MMOItemsLoreService loreService,
                                  MmoitemsHistoryService history) {
        this.config = config;
        this.data = data;
        this.autoMigration = autoMigration;
        this.loreService = loreService;
        this.history = history;
    }

    public MMOItemsRebuildService(ConfigManager config,
                                  UpgradeDataService data,
                                  AutoMigrationService autoMigration,
                                  MMOItemsLoreService loreService) {
        this(config, data, autoMigration, loreService, new MmoitemsHistoryService());
    }

    public ItemStack rebuild(ItemStack item, Player player) {
        if (item == null || item.getType().isAir()) return item;

        autoMigration.migrateIfNeeded(item);
        if (!data.isMMOItem(item)) return item;

        final int level = Math.max(0, data.readAnyUpgradeLevel(item));
        final Map<String, ConfigManager.StatMultiplier> mults = config.getAllStatMultipliers();

        if (mults != null && !mults.isEmpty()) {
            for (ConfigManager.StatMultiplier m : mults.values()) {
                if (m == null) continue;
                String nbtKey = m.nbtKey();
                if (nbtKey == null || nbtKey.isBlank()) continue;

                data.ensureBaseSnapshot(item, nbtKey);
                double base = data.getBaseStat(item, nbtKey);

                MmoitemsHistoryData h = history.readHistory(item, nbtKey);
                double gem = Math.max(0D, h.gemValue);
                double og = h.ogValue;
                double external = (og > 0D) ? Math.max(0D, og - base) : 0D;

                double upgraded;
                if (m.type() == ConfigManager.StatType.PERCENTAGE) {
                    double perLevel = m.value() <= 0 ? 1D : m.value();
                    upgraded = base * Math.pow(perLevel, level);
                } else {
                    upgraded = base + (m.value() * level);
                }

                double out = upgraded + external + gem;
                final double finalOut = out;

                NBT.modify(item, nbt -> {
                    nbt.setDouble(nbtKey, finalOut);
                });
            }
        }

        try {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String baseName = data.getString(item, "MMOITEMS_NAME");
                if (baseName != null && !baseName.isBlank()) {
                    String full = (level > 0) ? (baseName + " +" + level) : baseName;
                    meta.displayName(Text.parse(full));
                    item.setItemMeta(meta);
                }
            }
        } catch (Throwable ignored) {}

        try {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> loreLines = LoreMetaUtil.getLoreAsJsonStrings(meta);
                if (loreLines != null && !loreLines.isEmpty() && mults != null && !mults.isEmpty()) {

                    for (ConfigManager.StatMultiplier m : mults.values()) {
                        if (m == null) continue;
                        String nbtKey = m.nbtKey();
                        if (nbtKey == null || nbtKey.isBlank()) continue;

                        List<String> names = m.loreNames();
                        if (names == null || names.isEmpty()) continue;

                        String baseKey = "RUPGRADEABLE_BASE_" + nbtKey;
                        double current = readDoubleTag(item, nbtKey);
                        double base = readDoubleTag(item, baseKey);
                        double bonus = current - base;

                        for (int i = 0; i < loreLines.size(); i++) {
                            String json = loreLines.get(i);
                            if (json == null) continue;

                            String lower = json.toLowerCase(Locale.ROOT);
                            String matched = null;
                            for (String n : names) {
                                if (n == null || n.isBlank()) continue;
                                if (lower.contains(n.toLowerCase(Locale.ROOT))) {
                                    matched = n;
                                    break;
                                }
                            }
                            if (matched == null) continue;

                            String cleaned = removeBonusSuffixFromJson(json);
                            cleaned = replaceLastNumberInTextSegments(cleaned, fmt1(current));

                            if (level > 0 && bonus > BONUS_EPS) {
                                cleaned = appendGreenBonusSuffixJson(cleaned, fmt1(bonus));
                            }

                            loreLines.set(i, cleaned);
                        }
                    }

                    for (ConfigManager.StatMultiplier m : mults.values()) {
                        if (m == null) continue;
                        String nbtKey = m.nbtKey();
                        if (nbtKey == null || nbtKey.isBlank()) continue;
                        loreLines = loreService.applyUpgradeSuffix(item, loreLines, nbtKey, level);
                    }

                    LoreMetaUtil.setLoreFromJsonStrings(meta, loreLines);
                    item.setItemMeta(meta);
                }
            }
        } catch (Throwable ignored) {}

        return item;
    }

    public ItemStack rebuildKeepingData(ItemStack item) {
        return rebuild(item, null);
    }

    public ItemStack rebuildFor(ItemStack item, Player player) {
        return rebuild(item, player);
    }

    private static double readDoubleTag(ItemStack item, String nbtKey) {
        if (item == null || item.getType().isAir()) return 0D;
        if (nbtKey == null || nbtKey.isBlank()) return 0D;

        Double v = NBT.get(item, (ReadableItemNBT nbt) -> {
            if (!nbt.hasTag(nbtKey)) return null;
            try { return nbt.getDouble(nbtKey); }
            catch (Throwable ignored) {
                try { return (double) nbt.getInteger(nbtKey); }
                catch (Throwable ignored2) { return null; }
            }
        });
        return v == null ? 0D : v;
    }

    private static String removeBonusSuffixFromJson(String json) {
        if (json == null) return null;
        Matcher m = TEXT_FIELD.matcher(json);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String text = unescapeJson(m.group(1));
            String cleaned = BONUS_SUFFIX_TEXT.matcher(text).replaceAll("");
            m.appendReplacement(sb, Matcher.quoteReplacement("\"text\":\"" + escapeJson(cleaned) + "\""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String replaceLastNumberInTextSegments(String json, String replacement) {
        if (json == null) return null;

        Matcher m = TEXT_FIELD.matcher(json);

        int lastTextStart = -1;
        int lastTextEnd = -1;
        String lastTextValue = null;

        while (m.find()) {
            String text = unescapeJson(m.group(1));
            Matcher nm = NUMBER.matcher(text);
            boolean has = false;
            while (nm.find()) has = true;
            if (has) {
                lastTextStart = m.start(1);
                lastTextEnd = m.end(1);
                lastTextValue = text;
            }
        }

        if (lastTextValue == null || lastTextStart < 0) return json;

        Matcher nm = NUMBER.matcher(lastTextValue);
        int lastStart = -1;
        int lastEnd = -1;
        while (nm.find()) {
            lastStart = nm.start();
            lastEnd = nm.end();
        }
        if (lastStart < 0) return json;

        String updatedText = lastTextValue.substring(0, lastStart) + replacement + lastTextValue.substring(lastEnd);
        String before = json.substring(0, lastTextStart);
        String after = json.substring(lastTextEnd);
        return before + escapeJson(updatedText) + after;
    }

    private static String appendGreenBonusSuffixJson(String json, String bonus) {
        if (json == null) return null;

        int idx = json.lastIndexOf("]}");
        if (idx < 0) return json;

        String suffix = "{\"color\":\"green\",\"italic\":false,\"text\":\" [+" + escapeJson(bonus) + "]\"}";
        String insert = "," + suffix;

        return json.substring(0, idx) + insert + json.substring(idx);
    }

    private static String fmt1(double v) {
        String s = String.format(Locale.ROOT, "%.1f", v).replace(",", ".");
        if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
        return s;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
