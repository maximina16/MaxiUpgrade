// src/main/java/com/maxi/maxiUpgrade/MaxiUpgradePlugin.java
package com.maxi.maxiUpgrade;

import com.maxi.maxiUpgrade.cmd.CommandRegistrar;
import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.gui.UpgradeGuiFactory;
import com.maxi.maxiUpgrade.gui.UpgradeGuiManager;
import com.maxi.maxiUpgrade.listeners.PrepareItemListeners;
import com.maxi.maxiUpgrade.managers.MMOItemsUpgradeManager;
import com.maxi.maxiUpgrade.services.ItemMigrationFacade;
import com.maxi.maxiUpgrade.services.MMOItemsLoreService;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import com.maxi.maxiUpgrade.services.revision.RevisionMigrationService;
import com.maxi.maxiUpgrade.upgrade.UpgradeMessageService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MaxiUpgradePlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ItemsManager itemsManager;

    private UpgradeDataService dataService;
    private MMOItemsRebuildService rebuildService;
    private MMOItemsLoreService loreService;

    private RevisionMigrationService revisionMigrationService;
    private ItemMigrationFacade migrationFacade;

    private UpgradeMessageService messageService;
    private MMOItemsUpgradeManager upgradeManager;

    private UpgradeGuiFactory guiFactory;
    private UpgradeGuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        // items.yml + messages.yml
        saveResource("items.yml", false);
        saveResource("messages.yml", false);

        this.configManager = new ConfigManager(this);
        this.itemsManager = new ItemsManager(this);

        this.dataService = new UpgradeDataService(this, configManager);
        this.rebuildService = new MMOItemsRebuildService(this, configManager, dataService);
        this.loreService = new MMOItemsLoreService(this, configManager, dataService);

        this.revisionMigrationService = new RevisionMigrationService(this, configManager, dataService, rebuildService, loreService);
        this.migrationFacade = new ItemMigrationFacade(configManager, dataService, revisionMigrationService, rebuildService, loreService);

        this.messageService = new UpgradeMessageService(this);
        FileConfiguration msgYml = itemsManager.messagesConfig();
        this.messageService.setMessages(msgYml);

        this.upgradeManager = new MMOItemsUpgradeManager(this, configManager, itemsManager, dataService, migrationFacade, rebuildService, loreService);

        this.guiFactory = new UpgradeGuiFactory(this, configManager, itemsManager, dataService, migrationFacade, rebuildService, loreService);
        this.guiManager = new UpgradeGuiManager(this, guiFactory, migrationFacade);

        new CommandRegistrar(this, configManager, itemsManager, dataService, migrationFacade, rebuildService, loreService, messageService, upgradeManager, guiManager).register();

        new PrepareItemListeners(this, configManager, dataService, migrationFacade, revisionMigrationService).register();

        Bukkit.getLogger().info("[MaxiUpgrade] Enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[MaxiUpgrade] Disabled.");
    }

    public ConfigManager configManager() { return configManager; }
    public ItemsManager itemsManager() { return itemsManager; }

    public UpgradeDataService dataService() { return dataService; }
    public MMOItemsRebuildService rebuildService() { return rebuildService; }
    public MMOItemsLoreService loreService() { return loreService; }

    public RevisionMigrationService revisionMigrationService() { return revisionMigrationService; }
    public ItemMigrationFacade migrationFacade() { return migrationFacade; }

    public UpgradeMessageService messageService() { return messageService; }
    public MMOItemsUpgradeManager upgradeManager() { return upgradeManager; }

    public UpgradeGuiManager guiManager() { return guiManager; }
}
