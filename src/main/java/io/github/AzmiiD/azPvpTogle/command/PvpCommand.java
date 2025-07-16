package io.github.AzmiiD.azPvpTogle.command;

import io.github.AzmiiD.azPvpTogle.AzPvpTogle;
import io.github.AzmiiD.azPvpTogle.data.PvpDataManager;
import io.github.AzmiiD.azPvpTogle.until.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PvpCommand implements CommandExecutor, TabCompleter {

    private final AzPvpTogle plugin;
    private final PvpDataManager dataManager;
    private final MessageUtil messageUtil;

    public PvpCommand(AzPvpTogle plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.messageUtil = plugin.getMessageUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageUtil.colorize("&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("azpvptogle.use")) {
            player.sendMessage(messageUtil.getMessage("no-permission"));
            return true;
        }

        // Handle reload subcommand (for ops/admins)
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("azpvptogle.admin") || player.isOp()) {
                plugin.reloadPluginConfig();
                player.sendMessage(messageUtil.colorize(messageUtil.getPrefix() + "&aConfiguration reloaded!"));
                return true;
            } else {
                player.sendMessage(messageUtil.getMessage("no-permission"));
                return true;
            }
        }

        // Handle status subcommand
        if (args.length > 0 && args[0].equalsIgnoreCase("status")) {
            boolean currentStatus = dataManager.getPvpStatus(player.getUniqueId());
            String statusMessage = currentStatus ? "status-enabled" : "status-disabled";
            player.sendMessage(messageUtil.getMessage(statusMessage));
            return true;
        }

        // Check if arguments are provided
        if (args.length == 0) {
            player.sendMessage(messageUtil.getMessage("usage"));
            return true;
        }

        String argument = args[0].toLowerCase();
        boolean currentStatus = dataManager.getPvpStatus(player.getUniqueId());

        switch (argument) {
            case "on":
            case "enable":
            case "true":
                if (currentStatus) {
                    player.sendMessage(messageUtil.getMessage("already-enabled"));
                } else {
                    dataManager.setPvpStatus(player.getUniqueId(), true);
                    player.sendMessage(messageUtil.getMessage("enabled"));
                }
                break;

            case "off":
            case "disable":
            case "false":
                if (!currentStatus) {
                    player.sendMessage(messageUtil.getMessage("already-disabled"));
                } else {
                    dataManager.setPvpStatus(player.getUniqueId(), false);
                    player.sendMessage(messageUtil.getMessage("disabled"));
                }
                break;

            default:
                player.sendMessage(messageUtil.getMessage("usage"));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            // Add basic completions
            if ("on".startsWith(partial)) {
                completions.add("on");
            }
            if ("off".startsWith(partial)) {
                completions.add("off");
            }

            // Add admin completions
            if (sender.hasPermission("azpvptogle.admin") || sender.isOp()) {
                if ("reload".startsWith(partial)) {
                    completions.add("reload");
                }
                if ("status".startsWith(partial)) {
                    completions.add("status");
                }
            }
        }

        return completions;
    }
}
