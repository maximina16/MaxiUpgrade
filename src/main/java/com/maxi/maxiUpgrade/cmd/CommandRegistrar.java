// cmd/CommandRegistrar.java
package com.maxi.maxiUpgrade.cmd;

import com.maxi.maxiUpgrade.MaxiUpgradePlugin;
import org.bukkit.command.PluginCommand;

public final class CommandRegistrar {

    private final MaxiUpgradePlugin plugin;

    public CommandRegistrar(MaxiUpgradePlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        PluginCommand cmd = plugin.getCommand("upgradeableitems");
        if (cmd == null) return;

        MaxiUpgradeCommand exec = new MaxiUpgradeCommand(
                plugin,
                plugin.guiManager(),
                plugin.msg(),
                plugin.configManager(),
                plugin.itemsManager(),
                plugin.getUpgradeDataService(),
                plugin.rebuildService()
        );

        cmd.setExecutor(exec);
        cmd.setTabCompleter(exec);
    }
}
