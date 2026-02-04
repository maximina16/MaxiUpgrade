// services/CachePurger.java
package com.maxi.maxiUpgrade.services;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CachePurger {

    private final JavaPlugin plugin;
    private final ItemMigrationFacade migration;
    private final long intervalTicks;
    private final long maxAgeMillis;

    private int taskId = -1;

    public CachePurger(JavaPlugin plugin, ItemMigrationFacade migration, long intervalTicks, long maxAgeMillis) {
        this.plugin = plugin;
        this.migration = migration;
        this.intervalTicks = intervalTicks;
        this.maxAgeMillis = maxAgeMillis;
    }

    public void start() {
        stop();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            // Facade içinde gerçek cache varsa burada purge edin.
            // Şimdilik no-op (compile + hook).
        }, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public long maxAgeMillis() { return maxAgeMillis; }
}
