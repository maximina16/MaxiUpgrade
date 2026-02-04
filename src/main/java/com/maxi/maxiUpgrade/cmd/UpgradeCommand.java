package com.maxi.maxiUpgrade.cmd;

import com.maxi.maxiUpgrade.gui.UpgradeGuiManager;
import com.maxi.maxiUpgrade.utils.ItemStackUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class UpgradeCommand implements CommandExecutor {

    private final UpgradeGuiManager gui;

    public UpgradeCommand(UpgradeGuiManager gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Cursor item doluyken GUI açmak dup riskini artırır → açma
        try {
            ItemStack cursor = p.getItemOnCursor();
            if (!ItemStackUtil.isAir(cursor)) {
                p.sendMessage("§cElinde (cursor) item varken upgrade menüsü açılamaz.");
                return true;
            }
        } catch (Throwable ignored) {}

        gui.open(p);
        return true;
    }
}
