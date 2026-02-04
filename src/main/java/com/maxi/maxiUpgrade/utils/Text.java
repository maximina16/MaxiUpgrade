package com.maxi.maxiUpgrade.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Text {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SECTION =
            LegacyComponentSerializer.legacySection();

    private static final LegacyComponentSerializer LEGACY_AMP =
            LegacyComponentSerializer.legacyAmpersand();

    private Text() {}

    /** & -> § çevirir, sonra legacy kodları MiniMessage tag'lerine çevirip MiniMessage parse eder. */
    public static Component parse(String raw) {
        if (raw == null) return Component.empty();
        if (raw.isBlank()) return Component.empty();

        // & renk kodlarını gerçek legacy'ye çevir
        String s = raw.replace('&', '§');

        // İçinde legacy varsa -> minimessage tag'lerine çevir, böylece MM patlamaz.
        if (s.indexOf('§') >= 0) {
            s = legacyToMiniMessage(s);
        }

        // artık safe: sadece minimessage tag'leri + normal text
        try {
            return MM.deserialize(s);
        } catch (Throwable t) {
            // fallback: hiç olmazsa legacy olarak gönder
            try {
                return LEGACY_SECTION.deserialize(raw);
            } catch (Throwable ignored) {
                return Component.text(raw);
            }
        }
    }

    /** Sadece legacy parse (MM yok) gereken yerler için. */
    public static Component legacy(String raw) {
        if (raw == null) return Component.empty();
        String s = raw.replace('&', '§');
        return LEGACY_SECTION.deserialize(s);
    }

    // --- §0-§f, §k..§r, §x hex formatını minimessage'e çevirir
    private static String legacyToMiniMessage(String s) {
        StringBuilder out = new StringBuilder(s.length() + 32);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '§' && i + 1 < s.length()) {
                char code = Character.toLowerCase(s.charAt(i + 1));

                // §x§R§R§G§G§B§B -> <#RRGGBB>
                if (code == 'x' && i + 13 < s.length()) {
                    try {
                        String hex = "" + s.charAt(i + 3) + s.charAt(i + 5)
                                + s.charAt(i + 7) + s.charAt(i + 9)
                                + s.charAt(i + 11) + s.charAt(i + 13);
                        out.append("<#").append(hex).append(">");
                        i += 13;
                        continue;
                    } catch (Throwable ignored) {
                        // düşerse normal akışa devam
                    }
                }

                switch (code) {
                    case '0' -> out.append("<black>");
                    case '1' -> out.append("<dark_blue>");
                    case '2' -> out.append("<dark_green>");
                    case '3' -> out.append("<dark_aqua>");
                    case '4' -> out.append("<dark_red>");
                    case '5' -> out.append("<dark_purple>");
                    case '6' -> out.append("<gold>");
                    case '7' -> out.append("<gray>");
                    case '8' -> out.append("<dark_gray>");
                    case '9' -> out.append("<blue>");
                    case 'a' -> out.append("<green>");
                    case 'b' -> out.append("<aqua>");
                    case 'c' -> out.append("<red>");
                    case 'd' -> out.append("<light_purple>");
                    case 'e' -> out.append("<yellow>");
                    case 'f' -> out.append("<white>");
                    case 'k' -> out.append("<obfuscated>");
                    case 'l' -> out.append("<bold>");
                    case 'm' -> out.append("<strikethrough>");
                    case 'n' -> out.append("<underlined>");
                    case 'o' -> out.append("<italic>");
                    case 'r' -> out.append("<reset>");
                    default -> {
                        // bilinmeyen kod: yok say
                    }
                }

                i++; // kod karakterini de consume ettik
                continue;
            }

            out.append(c);
        }

        return out.toString();
    }
}
