// listeners/UpgradeSafetyListener.java
package com.maxi.maxiUpgrade.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class UpgradeSafetyListener implements Listener {

    private final Set<UUID> locked = new HashSet<>();

    public void lock(Player p) { if (p != null) locked.add(p.getUniqueId()); }
    public void unlock(Player p) { if (p != null) locked.remove(p.getUniqueId()); }
    public boolean isLocked(Player p) { return p != null && locked.contains(p.getUniqueId()); }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        // fail-safe unlock
        if (e.getPlayer() instanceof Player p) unlock(p);
    }
}
