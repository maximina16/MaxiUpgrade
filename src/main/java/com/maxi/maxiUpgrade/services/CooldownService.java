package com.maxi.maxiUpgrade.services;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownService {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownMillis;

    public CooldownService(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public boolean isOnCooldown(Player player) {
        Long last = cooldowns.get(player.getUniqueId());
        if (last == null) return false;
        return System.currentTimeMillis() - last < cooldownMillis;
    }

    public long getRemaining(Player player) {
        Long last = cooldowns.get(player.getUniqueId());
        if (last == null) return 0;
        return Math.max(0, cooldownMillis - (System.currentTimeMillis() - last));
    }

    public void trigger(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void clear(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
