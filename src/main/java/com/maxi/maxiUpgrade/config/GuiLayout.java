package com.maxi.maxiUpgrade.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class GuiLayout {

    private final int size;
    private final String title;

    private final int targetSlot;
    private final int upgradeButtonSlot;

    private final int mainStoneSlot;
    private final List<Integer> stoneSlots;

    public GuiLayout(FileConfiguration gui) {
        this.size = gui.getInt("gui.size", 54);
        this.title = gui.getString("gui.title", "<gray>Upgrade</gray>");

        this.targetSlot = gui.getInt("gui.slots.target", 13);
        this.upgradeButtonSlot = gui.getInt("gui.slots.upgrade-button", 31);

        this.mainStoneSlot = gui.getInt("gui.slots.main-stone", 22);

        List<Integer> def = List.of(20, 21, 22, 23, 24);
        List<Integer> from = gui.getIntegerList("gui.slots.stones");
        this.stoneSlots = from == null || from.isEmpty() ? new ArrayList<>(def) : new ArrayList<>(from);
    }

    public int size() { return size; }
    public String title() { return title; }

    public int targetSlot() { return targetSlot; }
    public int upgradeButtonSlot() { return upgradeButtonSlot; }

    public int mainStoneSlot() { return mainStoneSlot; }
    public List<Integer> stoneSlots() { return stoneSlots; }
}
