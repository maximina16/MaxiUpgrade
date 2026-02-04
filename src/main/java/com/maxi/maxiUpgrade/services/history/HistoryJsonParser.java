package com.maxi.maxiUpgrade.services.history;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HistoryJsonParser {

    private HistoryJsonParser() {}

    public static double extractOgValue(String json, String nbtKey) {
        if (json == null || json.isBlank() || nbtKey == null) return 0D;

        String keyPrefix = Pattern.quote(nbtKey) + "_ñ";
        Pattern p = Pattern.compile("\"OGStory\"\\s*:\\s*\\[\\s*\\{\\s*\"" + keyPrefix + "[^\"]*\"\\s*:\\s*([-0-9.]+)");
        Matcher m = p.matcher(json);
        if (!m.find()) return 0D;

        return safeDouble(m.group(1));
    }

    public static double extractGemSum(String json, String nbtKey) {
        if (json == null || json.isBlank() || nbtKey == null) return 0D;

        int idx = json.indexOf("\"Gemstory\"");
        if (idx < 0) return 0D;

        String tail = json.substring(idx);

        String keyPrefix = Pattern.quote(nbtKey) + "_ñ";
        Pattern p = Pattern.compile("\"" + keyPrefix + "[^\"]*\"\\s*:\\s*([-0-9.]+)");
        Matcher m = p.matcher(tail);

        double sum = 0D;
        while (m.find()) sum += safeDouble(m.group(1));
        return sum;
    }

    private static double safeDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception ignored) { return 0D; }
    }
}
