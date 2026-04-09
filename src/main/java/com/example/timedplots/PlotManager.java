package com.example.timedplots;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {

    private final TimedPlots plugin;
    private final Map<UUID, Plot> plots = new HashMap<>();
    private final String worldName;
    private final int plotSize = 100;
    private final int spacing = 50; // Buffer between plots
    private int nextPlotId = 0;

    public PlotManager(TimedPlots plugin) {
        this.plugin = plugin;
        this.worldName = plugin.getConfig().getString("plots-world", "plots");
        
        // Load data from disk
        plugin.getDataManager().loadPlots(plots);
        // Determine next ID based on loaded plots
        nextPlotId = plots.values().stream().mapToInt(Plot::getId).max().orElse(-1) + 1;
    }

    public void createPlot(Player player) {
        if (plots.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have a plot!");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Plot world is not loaded.");
            return;
        }

        // Grid Math
        int totalWidth = 1000; // How many plots per row before wrapping
        int xIndex = nextPlotId % totalWidth;
        int zIndex = nextPlotId / totalWidth;

        int realX = xIndex * (plotSize + spacing);
        int realZ = zIndex * (plotSize + spacing);

        // Create WorldGuard Region
        if (!createRegion(player, world, realX, realZ, nextPlotId)) {
            player.sendMessage(ChatColor.RED + "Failed to create region protection.");
            return;
        }

        // Save Data
        Plot newPlot = new Plot(player.getUniqueId(), nextPlotId, realX, realZ, System.currentTimeMillis(), false);
        plots.put(player.getUniqueId(), newPlot);
        nextPlotId++;
        plugin.getDataManager().savePlots();

        // Teleport
        teleportToPlot(player, newPlot);
        player.sendMessage(ChatColor.GREEN + "Plot created! You have 30 minutes to build.");
    }

    private boolean createRegion(Player owner, World world, int x, int z, int id) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) return false;

        String regionName = "plot_" + owner.getUniqueId().toString();
        
        // Defines corners: -50 to +50 from center
        BlockVector3 min = BlockVector3.at(x - (plotSize / 2), world.getMinHeight(), z - (plotSize / 2));
        BlockVector3 max = BlockVector3.at(x + (plotSize / 2), world.getMaxHeight(), z + (plotSize / 2));

        ProtectedRegion region = new ProtectedCuboidRegion(regionName, min, max);
        
        // Set Owner
        region.getOwners().addPlayer(owner.getUniqueId());
        
        // Set Flags
        region.setFlag(Flags.GREET_MESSAGE, ChatColor.AQUA + "Welcome to " + owner.getName() + "'s plot.");
        region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.GRAY + "Leaving plot.");
        
        // Ensure Build is Allowed initially
        region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);

        regions.addRegion(region);
        return true;
    }

    public void checkPlotExpirations() {
        long now = System.currentTimeMillis();
        long limit = 30 * 60 * 1000; // 30 minutes in ms

        for (Plot plot : plots.values()) {
            if (!plot.isLocked() && (now - plot.getCreationTime() > limit)) {
                lockPlot(plot);
            }
        }
    }

    public void lockPlot(Plot plot) {
        if (plot.isLocked()) return;

        plot.setLocked(true);
        updateRegionLock(plot, true);
        plugin.getDataManager().savePlots();

        Player p = Bukkit.getPlayer(plot.getOwner());
        if (p != null && p.isOnline()) {
            p.sendMessage(ChatColor.RED + "Your 30-minute build time has expired! Plot locked.");
        }
    }

    public void unlockPlot(Plot plot) {
        plot.setLocked(false);
        updateRegionLock(plot, false);
        plugin.getDataManager().savePlots();
    }

    public void resetPlotTime(Plot plot) {
        plot.setCreationTime(System.currentTimeMillis());
        unlockPlot(plot); 
    }

    private void updateRegionLock(Plot plot, boolean lock) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) return;

        ProtectedRegion region = regions.getRegion("plot_" + plot.getOwner().toString());
        if (region != null) {
            if (lock) {
                region.setFlag(Flags.BUILD, StateFlag.State.DENY);
            } else {
                region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            }
        }
    }

    public void teleportToPlot(Player player, Plot plot) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Location loc = new Location(world, plot.getCenterX(), world.getHighestBlockYAt(plot.getCenterX(), plot.getCenterZ()) + 1, plot.getCenterZ());
            player.teleport(loc);
            player.sendMessage(ChatColor.YELLOW + "Teleported to plot.");
        }
    }
    
    public Plot getPlot(UUID uuid) {
        return plots.get(uuid);
    }
    
    public Plot getPlotByPlayerName(String name) {
        for(Map.Entry<UUID, Plot> entry : plots.entrySet()) {
             if(Bukkit.getOfflinePlayer(entry.getKey()).getName().equalsIgnoreCase(name)) {
                 return entry.getValue();
             }
        }
        return null;
    }

    public Map<UUID, Plot> getPlotsMap() {
        return plots;
    }
}