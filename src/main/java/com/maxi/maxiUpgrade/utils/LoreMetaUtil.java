package com.maxi.maxiUpgrade.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Lore’u JSON string list olarak okuyup/yazmak için Paper-safe helper.
 * - meta.getLore() varsa onu kullanır (string)
 * - yoksa meta.lore() (Component) -> JSON string list’e serialize eder
 */
public final class LoreMetaUtil {
    private LoreMetaUtil() {}

    public static List<String> getLoreAsJsonStrings(ItemMeta meta) {
        if (meta == null) return null;

        // legacy string lore (Paper hala destekliyor)
        try {
            List<String> lore = meta.getLore();
            if (lore != null) return new ArrayList<>(lore);
        } catch (Throwable ignored) {}

        // component lore
        try {
            List<Component> loreComp = meta.lore();
            if (loreComp == null) return null;

            List<String> out = new ArrayList<>(loreComp.size());
            for (Component c : loreComp) {
                out.add(GsonComponentSerializer.gson().serialize(c));
            }
            return out;
        } catch (Throwable ignored) {}

        return null;
    }

    public static void setLoreFromJsonStrings(ItemMeta meta, List<String> jsonLines) {
        if (meta == null) return;

        // component lore setter (tercih)
        try {
            if (jsonLines == null) {
                meta.lore(null);
                return;
            }

            List<Component> comps = new ArrayList<>(jsonLines.size());
            for (String s : jsonLines) {
                if (s == null) continue;
                comps.add(GsonComponentSerializer.gson().deserialize(s));
            }
            meta.lore(comps);
            return;
        } catch (Throwable ignored) {}

        // fallback: legacy string lore
        try {
            meta.setLore(jsonLines);
        } catch (Throwable ignored) {}
    }
}
