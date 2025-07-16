package io.github.AzmiiD.azPvpTogle;

import io.github.AzmiiD.azPvpTogle.command.PvpCommand;
import io.github.AzmiiD.azPvpTogle.listener.PvpListener;
import io.github.AzmiiD.azPvpTogle.data.PvpDataManager;
import io.github.AzmiiD.azPvpTogle.until.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class AzPvpTogle extends JavaPlugin {

    private static AzPvpTogle instance;
    private PvpDataManager dataManager;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Initialize utilities
        messageUtil = new MessageUtil(this);

        // Initialize data manager
        dataManager = new PvpDataManager(this);

        // Register command
        PvpCommand pvpCommand = new PvpCommand(this);
        getCommand("pvp").setExecutor(pvpCommand);
        getCommand("pvp").setTabCompleter(pvpCommand);

        // Register listener
        getServer().getPluginManager().registerEvents(new PvpListener(this), this);

        // Plugin startup message
        getLogger().info("AzPvpTogle has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        if (dataManager != null) {
            dataManager.saveAllData();
        }

        getLogger().info("AzPvpTogle has been disabled!");
    }

    public static AzPvpTogle getInstance() {
        return instance;
    }

    public PvpDataManager getDataManager() {
        return dataManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        messageUtil.reloadMessages();
        getLogger().info("Configuration reloaded!");
    }
}