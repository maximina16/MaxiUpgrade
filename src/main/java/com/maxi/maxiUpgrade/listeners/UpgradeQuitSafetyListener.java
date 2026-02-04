// listeners/UpgradeQuitSafetyListener.java
package com.maxi.maxiUpgrade.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UpgradeQuitSafetyListener implements Listener {

    private final UpgradeSafetyListener safety;

    public UpgradeQuitSafetyListener(UpgradeSafetyListener safety) {
        this.safety = safety;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        safety.unlock(e.getPlayer());
    }
}
