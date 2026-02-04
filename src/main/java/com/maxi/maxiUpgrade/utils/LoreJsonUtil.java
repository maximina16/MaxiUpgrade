package com.maxi.maxiUpgrade.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LoreJsonUtil {
    private LoreJsonUtil() {}

    // JSON component satırlarında yeşil suffix extra objesini silmek için kaba ama pratik regex
    // {"color":"green",...,"text":" [+34.4]"}
    private static final Pattern GREEN_SUFFIX_OBJECT =
            Pattern.compile("\\{[^{}]*\"color\"\\s*:\\s*\"green\"[^{}]*\"text\"\\s*:\\s*\"\\s*\\[\\+.*?\\]\"[^{}]*\\}");

    // legacy style suffix: " §a[+34.4]"
    private static final Pattern LEGACY_SUFFIX =
            Pattern.compile(" \\u00A7a\\[\\+.*?\\]");

    public static boolean looksLikeJsonComponent(String line) {
        if (line == null) return false;
        String s = line.trim();
        return s.startsWith("{") && s.contains("\"extra\"") && s.contains("\"text\"");
    }

    /**
     * JSON component satırından suffix'i kaldırır:
     * - extra içindeki green suffix objesini siler
     * - legacy " §a[+...]" stringi varsa onu da kaldırır
     */
    public static String stripSuffix(String jsonLine) {
        if (jsonLine == null) return null;

        String out = jsonLine;

        // 1) extra içindeki green object’i sil
        out = removeJsonObjectFromExtraArray(out, GREEN_SUFFIX_OBJECT);

        // 2) her ihtimale karşı legacy suffix varsa
        out = LEGACY_SUFFIX.matcher(out).replaceAll("");

        return out;
    }

    /**
     * JSON component satırına suffix ekler (extra array’e append):
     * {"color":"green","italic":false,"text":" [+X]"}
     */
    public static String appendSuffix(String jsonLine, String suffixText) {
        if (jsonLine == null) return null;
        if (suffixText == null || suffixText.isBlank()) return jsonLine;

        String cleaned = stripSuffix(jsonLine);

        // extra array’i bulup en sona ekle
        int extraIdx = cleaned.indexOf("\"extra\"");
        if (extraIdx < 0) {
            // extra yoksa dokunma (riskli)
            return cleaned;
        }

        int arrayStart = cleaned.indexOf('[', extraIdx);
        if (arrayStart < 0) return cleaned;

        int arrayEnd = findMatchingBracket(cleaned, arrayStart, '[', ']');
        if (arrayEnd < 0) return cleaned;

        String insertObj = "{\"color\":\"green\",\"italic\":false,\"text\":\" " + escapeJson(suffixText) + "\"}";

        String before = cleaned.substring(0, arrayEnd);
        String after = cleaned.substring(arrayEnd);

        // boş mu dolu mu?
        boolean hasAny = hasAnyNonWhitespaceBetween(cleaned, arrayStart + 1, arrayEnd);

        String merged;
        if (hasAny) {
            merged = before + "," + insertObj + after;
        } else {
            merged = before + insertObj + after;
        }
        return merged;
    }

    private static String removeJsonObjectFromExtraArray(String json, Pattern objPattern) {
        int extraIdx = json.indexOf("\"extra\"");
        if (extraIdx < 0) return json;

        int arrayStart = json.indexOf('[', extraIdx);
        if (arrayStart < 0) return json;

        int arrayEnd = findMatchingBracket(json, arrayStart, '[', ']');
        if (arrayEnd < 0) return json;

        String arrayContent = json.substring(arrayStart + 1, arrayEnd);
        Matcher m = objPattern.matcher(arrayContent);

        // remove ALL matches (possible multiple)
        String cleanedArray = m.replaceAll("");

        // cleanup double commas / edge commas
        cleanedArray = cleanupCommas(cleanedArray);

        return json.substring(0, arrayStart + 1) + cleanedArray + json.substring(arrayEnd);
    }

    private static String cleanupCommas(String s) {
        // , , -> ,
        s = s.replaceAll(",\\s*,", ",");
        // leading comma
        s = s.replaceAll("^\\s*,\\s*", "");
        // trailing comma
        s = s.replaceAll("\\s*,\\s*$", "");
        return s;
    }

    private static int findMatchingBracket(String s, int start, char open, char close) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == open) depth++;
            else if (c == close) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static boolean hasAnyNonWhitespaceBetween(String s, int from, int to) {
        for (int i = from; i < to; i++) {
            if (!Character.isWhitespace(s.charAt(i))) return true;
        }
        return false;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
