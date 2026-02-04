package com.maxi.maxiUpgrade.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class UpgradeGuiSession {

    private final Player player;
    private ItemStack item;
    private ItemStack cursor;
    private boolean closed;

    public UpgradeGuiSession(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setCursor(ItemStack cursor) {
        this.cursor = cursor;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        this.closed = true;
    }
}
