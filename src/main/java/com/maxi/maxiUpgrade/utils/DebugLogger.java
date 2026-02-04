package com.maxi.maxiUpgrade.utils;

import org.bukkit.plugin.Plugin;

public final class DebugLogger {

    private final Plugin plugin;
    private final boolean enabled;

    public DebugLogger(Plugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
    }

    public void info(String msg) {
        if (!enabled) return;
        plugin.getLogger().info("[DEBUG] " + msg);
    }

    public void warn(String msg) {
        if (!enabled) return;
        plugin.getLogger().warning("[DEBUG] " + msg);
    }

    public void error(String msg, Throwable t) {
        if (!enabled) return;
        plugin.getLogger().severe("[DEBUG] " + msg);
        if (t != null) t.printStackTrace();
    }
}
