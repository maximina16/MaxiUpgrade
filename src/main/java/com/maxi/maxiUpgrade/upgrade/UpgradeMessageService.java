// src/main/java/com/maxi/maxiUpgrade/upgrade/UpgradeMessageService.java
package com.maxi.maxiUpgrade.upgrade;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class UpgradeMessageService {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private FileConfiguration messages;

    public UpgradeMessageService(Object ignored) {
        // plugin paramı lazım değil; eski imzaları bozmamak için
    }

    public void setMessages(FileConfiguration messages) {
        this.messages = messages;
    }

    public String get(String path) {
        if (messages == null) return "";
        return messages.getString(path, "");
    }

    public void send(Player p, String path) {
        send(p, path, Map.of());
    }

    public void sendCooldown(Player p, long ms) {
        Map<String, String> ph = Map.of("cooldown", String.valueOf(ms));
        send(p, "messages.cooldown", ph);
    }

    public void send(Player p, String path, Map<String, String> placeholders) {
        String raw = get(path);
        if (raw == null || raw.isBlank()) return;
        String rendered = applyPlaceholders(raw, placeholders);
        p.sendMessage(mm.deserialize(rendered));
    }

    public Map<String, String> placeholdersFromResult(UpgradeResult r) {
        Map<String, String> ph = new HashMap<>();
        ph.put("old_level", String.valueOf(r.oldLevel()));
        ph.put("new_level", String.valueOf(r.newLevel()));
        ph.put("chance", String.valueOf(r.chance()));
        ph.put("outcome", String.valueOf(r.outcome()));
        ph.put("used_protection", String.valueOf(r.usedProtectionOrb()));
        ph.put("used_blessed", String.valueOf(r.usedBlessedParchment()));
        ph.put("used_downgrade", String.valueOf(r.usedDowngradeParchment()));
        ph.put("dust", String.valueOf(r.dustAmount()));
        ph.put("dust_bonus", String.valueOf(r.dustBonus()));
        ph.put("used_booster", String.valueOf(r.usedLuckBooster()));
        ph.put("booster_mult", String.valueOf(r.boosterMult()));
        return ph;
    }

    private String applyPlaceholders(String s, Map<String, String> ph) {
        String out = s;
        for (Map.Entry<String, String> e : ph.entrySet()) {
            out = out.replace("%" + e.getKey() + "%", e.getValue() == null ? "" : e.getValue());
        }
        return out;
    }
}
