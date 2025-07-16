package io.github.AzmiiD.azPvpTogle.data;

import io.github.AzmiiD.azPvpTogle.AzPvpTogle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PvpDataManager {

    private final AzPvpTogle plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Boolean> pvpStatusCache;

    public PvpDataManager(AzPvpTogle plugin) {
        this.plugin = plugin;
        this.pvpStatusCache = new HashMap<>();

        // Create data directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Initialize data file
        this.dataFile = new File(plugin.getDataFolder(), "pvpdata.yml");
        loadDataFile();
    }

    private void loadDataFile() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                plugin.getLogger().info("Created new pvpdata.yml file");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create pvpdata.yml file", e);
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Load all player data into cache
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    boolean pvpStatus = dataConfig.getBoolean("players." + uuidString + ".pvp-enabled", false);
                    pvpStatusCache.put(uuid, pvpStatus);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID found in data file: " + uuidString);
                }
            }
        }

        plugin.getLogger().info("Loaded " + pvpStatusCache.size() + " player records from pvpdata.yml");
    }

    public boolean getPvpStatus(UUID uuid) {
        return pvpStatusCache.getOrDefault(uuid, plugin.getConfig().getBoolean("default-pvp-state", false));
    }

    public void setPvpStatus(UUID uuid, boolean enabled) {
        pvpStatusCache.put(uuid, enabled);

        // Save to file immediately
        savePlayerData(uuid);
    }

    public boolean hasPlayerData(UUID uuid) {
        return pvpStatusCache.containsKey(uuid);
    }

    public void loadPlayerData(UUID uuid) {
        if (dataConfig.contains("players." + uuid.toString())) {
            boolean pvpStatus = dataConfig.getBoolean("players." + uuid.toString() + ".pvp-enabled", false);
            pvpStatusCache.put(uuid, pvpStatus);
        }
    }

    public void savePlayerData(UUID uuid) {
        if (!pvpStatusCache.containsKey(uuid)) {
            return;
        }

        boolean pvpStatus = pvpStatusCache.get(uuid);
        dataConfig.set("players." + uuid.toString() + ".pvp-enabled", pvpStatus);
        dataConfig.set("players." + uuid.toString() + ".last-updated", System.currentTimeMillis());

        saveDataFile();
    }

    public void saveAllData() {
        for (UUID uuid : pvpStatusCache.keySet()) {
            boolean pvpStatus = pvpStatusCache.get(uuid);
            dataConfig.set("players." + uuid.toString() + ".pvp-enabled", pvpStatus);
            dataConfig.set("players." + uuid.toString() + ".last-updated", System.currentTimeMillis());
        }

        saveDataFile();
        plugin.getLogger().info("Saved all player data to pvpdata.yml");
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save pvpdata.yml", e);
        }
    }

    public void removePlayerData(UUID uuid) {
        pvpStatusCache.remove(uuid);
        dataConfig.set("players." + uuid.toString(), null);
        saveDataFile();
    }

    public Map<UUID, Boolean> getAllPlayerData() {
        return new HashMap<>(pvpStatusCache);
    }

    public int getPlayerCount() {
        return pvpStatusCache.size();
    }

    public void reloadData() {
        pvpStatusCache.clear();
        loadDataFile();
        plugin.getLogger().info("Reloaded player data from pvpdata.yml");
    }
}
