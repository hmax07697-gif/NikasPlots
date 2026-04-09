package com.example.timedplots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotCommand implements CommandExecutor {

    private final TimedPlots plugin;

    public PlotCommand(TimedPlots plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Usage: /plot <create|tp|reset|lock|unlock>");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("create")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("plots.create")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            plugin.getPlotManager().createPlot((Player) sender);
            return true;
        }

        if (sub.equals("tp")) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("plots.tp")) return true;

            Plot plot;
            if (args.length == 1) {
                plot = plugin.getPlotManager().getPlot(((Player) sender).getUniqueId());
            } else {
                plot = plugin.getPlotManager().getPlotByPlayerName(args[1]);
            }

            if (plot == null) {
                sender.sendMessage(ChatColor.RED + "Plot not found.");
            } else {
                plugin.getPlotManager().teleportToPlot((Player) sender, plot);
            }
            return true;
        }

        if (sub.equals("reset") || sub.equals("lock") || sub.equals("unlock")) {
            if (!sender.hasPermission("plots." + sub)) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /plot " + sub + " <player>");
                return true;
            }
            
            Plot plot = plugin.getPlotManager().getPlotByPlayerName(args[1]);
            if (plot == null) {
                sender.sendMessage(ChatColor.RED + "Player does not have a plot.");
                return true;
            }

            if (sub.equals("reset")) {
                plugin.getPlotManager().resetPlotTime(plot);
                sender.sendMessage(ChatColor.GREEN + "Plot time reset.");
            } else if (sub.equals("lock")) {
                plugin.getPlotManager().lockPlot(plot);
                sender.sendMessage(ChatColor.YELLOW + "Plot manually locked.");
            } else {
                plugin.getPlotManager().unlockPlot(plot);
                sender.sendMessage(ChatColor.GREEN + "Plot unlocked.");
            }
            return true;
        }

        return false;
    }
}