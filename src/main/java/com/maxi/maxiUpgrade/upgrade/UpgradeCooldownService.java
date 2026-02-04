package com.maxi.maxiUpgrade.upgrade;

import com.maxi.maxiUpgrade.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UpgradeCooldownService {

    private final ConfigManager config;
    private final Map<UUID, Long> lastUse = new ConcurrentHashMap<>();

    public UpgradeCooldownService(ConfigManager config) {
        this.config = config;
    }

    public long getRemainingMillis(Player player) {
        if (player == null) return 0L;

        long cd = Math.max(0L, config.getUpgradeCooldown());
        if (cd <= 0L) return 0L;

        Long last = lastUse.get(player.getUniqueId());
        if (last == null) return 0L;

        long now = System.currentTimeMillis();
        long left = (last + cd) - now;
        return Math.max(0L, left);
    }

    public void mark(Player player) {
        if (player == null) return;
        lastUse.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void clear(Player player) {
        if (player == null) return;
        lastUse.remove(player.getUniqueId());
    }

    public void clearAll() {
        lastUse.clear();
    }
}
