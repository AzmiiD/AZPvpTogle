package io.github.AzmiiD.azPvpTogle.until;

import io.github.AzmiiD.azPvpTogle.AzPvpTogle;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {

    private final AzPvpTogle plugin;
    private Map<String, String> messages;

    public MessageUtil(AzPvpTogle plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
        loadMessages();
    }

    private void loadMessages() {
        messages.clear();

        // Load all messages from config
        messages.put("prefix", plugin.getConfig().getString("prefix", "&7[&cPvP&7] "));
        messages.put("enabled", plugin.getConfig().getString("enabled", "&aYour PvP mode is now ENABLED!"));
        messages.put("disabled", plugin.getConfig().getString("disabled", "&cYour PvP mode is now DISABLED!"));
        messages.put("deny-attacker", plugin.getConfig().getString("deny-attacker", "&cThat player has not enabled PvP!"));
        messages.put("deny-victim", plugin.getConfig().getString("deny-victim", "&cYou have not enabled PvP!"));
        messages.put("no-permission", plugin.getConfig().getString("no-permission", "&cYou don't have permission to use this command!"));
        messages.put("status-enabled", plugin.getConfig().getString("status-enabled", "&aYour PvP mode is currently ENABLED"));
        messages.put("status-disabled", plugin.getConfig().getString("status-disabled", "&cYour PvP mode is currently DISABLED"));
        messages.put("usage", plugin.getConfig().getString("usage", "&cUsage: /pvp <on|off>"));
        messages.put("already-enabled", plugin.getConfig().getString("already-enabled", "&cYour PvP mode is already enabled!"));
        messages.put("already-disabled", plugin.getConfig().getString("already-disabled", "&cYour PvP mode is already disabled!"));
        messages.put("error-loading-data", plugin.getConfig().getString("error-loading-data", "&cError loading player data!"));
        messages.put("error-saving-data", plugin.getConfig().getString("error-saving-data", "&cError saving player data!"));
    }

    public String getMessage(String key) {
        String message = messages.get(key);
        if (message == null) {
            plugin.getLogger().warning("Message key '" + key + "' not found in config!");
            return colorize("&cMessage not found: " + key);
        }

        // Add prefix to most messages (except prefix itself and status messages)
        if (!key.equals("prefix") && !key.startsWith("status-")) {
            message = getPrefix() + message;
        } else if (key.startsWith("status-")) {
            message = getPrefix() + message;
        }

        return colorize(message);
    }

    public String getPrefix() {
        return messages.getOrDefault("prefix", "&7[&cPvP&7] ");
    }

    public String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void reloadMessages() {
        loadMessages();
    }

    public String getRawMessage(String key) {
        return messages.get(key);
    }

    public void setMessage(String key, String message) {
        messages.put(key, message);
    }
}
