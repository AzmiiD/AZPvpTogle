package io.github.AzmiiD.azPvpTogle.listener;

import io.github.AzmiiD.azPvpTogle.AzPvpTogle;
import io.github.AzmiiD.azPvpTogle.data.PvpDataManager;
import io.github.AzmiiD.azPvpTogle.until.MessageUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpListener implements Listener {

    private final AzPvpTogle plugin;
    private final PvpDataManager dataManager;
    private final MessageUtil messageUtil;

    // Cooldown maps for deny messages
    private final Map<UUID, Long> attackerCooldowns = new HashMap<>();
    private final Map<UUID, Long> victimCooldowns = new HashMap<>();

    public PvpListener(AzPvpTogle plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.messageUtil = plugin.getMessageUtil();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if both entities are players
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Don't cancel if it's the same player (self-damage)
        if (victim.getUniqueId().equals(attacker.getUniqueId())) {
            return;
        }

        // Check PvP status of both players
        boolean attackerPvpEnabled = dataManager.getPvpStatus(attacker.getUniqueId());
        boolean victimPvpEnabled = dataManager.getPvpStatus(victim.getUniqueId());

        // Cancel damage if either player has PvP disabled
        if (!attackerPvpEnabled || !victimPvpEnabled) {
            event.setCancelled(true);

            long currentTime = System.currentTimeMillis();
            long cooldownDuration = plugin.getConfig().getLong("message-cooldown", 3) * 1000; // Convert to milliseconds

            // Send message to attacker if not on cooldown
            if (!attackerPvpEnabled || !victimPvpEnabled) {
                Long lastAttackerMessage = attackerCooldowns.get(attacker.getUniqueId());
                if (lastAttackerMessage == null || (currentTime - lastAttackerMessage) >= cooldownDuration) {
                    attacker.sendMessage(messageUtil.getMessage("deny-attacker"));
                    attackerCooldowns.put(attacker.getUniqueId(), currentTime);
                }
            }

            // Send message to victim if not on cooldown (only if victim has PvP disabled)
            if (!victimPvpEnabled) {
                Long lastVictimMessage = victimCooldowns.get(victim.getUniqueId());
                if (lastVictimMessage == null || (currentTime - lastVictimMessage) >= cooldownDuration) {
                    victim.sendMessage(messageUtil.getMessage("deny-victim"));
                    victimCooldowns.put(victim.getUniqueId(), currentTime);
                }
            }

            // Clean up old cooldown entries to prevent memory leak
            cleanupCooldowns(currentTime, cooldownDuration);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Load player data when they join
        dataManager.loadPlayerData(player.getUniqueId());

        // If it's a new player, set default PvP status
        if (!dataManager.hasPlayerData(player.getUniqueId())) {
            boolean defaultStatus = plugin.getConfig().getBoolean("default-pvp-state", false);
            dataManager.setPvpStatus(player.getUniqueId(), defaultStatus);

            // Send welcome message about PvP system
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(messageUtil.colorize(messageUtil.getPrefix() + "&eWelcome! Use &6/pvp on &eor &6/pvp off &eto toggle your PvP mode."));
                String statusMessage = defaultStatus ? "status-enabled" : "status-disabled";
                player.sendMessage(messageUtil.getMessage(statusMessage));
            }, 20L); // Delay by 1 second
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Save player data when they leave
        dataManager.savePlayerData(playerId);

        // Clean up cooldown entries for the leaving player
        attackerCooldowns.remove(playerId);
        victimCooldowns.remove(playerId);
    }

    private void cleanupCooldowns(long currentTime, long cooldownDuration) {
        // Clean up attacker cooldowns
        attackerCooldowns.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) >= cooldownDuration * 2); // Keep entries for 2x cooldown duration

        // Clean up victim cooldowns
        victimCooldowns.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) >= cooldownDuration * 2); // Keep entries for 2x cooldown duration
    }
}