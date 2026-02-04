// listeners/UniversalMigrationListener.java
package com.maxi.maxiUpgrade.listeners;

import com.maxi.maxiUpgrade.config.FeatureToggles;
import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UniversalMigrationListener implements Listener {

    private final ItemMigrationFacade migration;
    private final FeatureToggles toggles;

    public UniversalMigrationListener(ItemMigrationFacade migration, FeatureToggles toggles) {
        this.migration = migration;
        this.toggles = toggles;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!toggles.autoMigrate()) return;
        // join'de komple inv scan istemiyorsan boş bırak; inventory click/held zaten cover ediyor.
    }
}
