package com.example.timedplots;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {
    private final TimedPlots plugin;
    private File file;
    private FileConfiguration config;

    public DataManager(TimedPlots plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "plots.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void savePlots() {
        config.set("plots", null); 

        for (Plot plot : plugin.getPlotManager().getPlotsMap().values()) {
            String path = "plots." + plot.getOwner().toString();
            config.set(path + ".id", plot.getId());
            config.set(path + ".x", plot.getCenterX());
            config.set(path + ".z", plot.getCenterZ());
            config.set(path + ".time", plot.getCreationTime());
            config.set(path + ".locked", plot.isLocked());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save plots.yml", e);
        }
    }

    public void loadPlots(Map<UUID, Plot> plotMap) {
        if (!config.contains("plots")) return;

        ConfigurationSection section = config.getConfigurationSection("plots");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID owner = UUID.fromString(key);
                int id = section.getInt(key + ".id");
                int x = section.getInt(key + ".x");
                int z = section.getInt(key + ".z");
                long time = section.getLong(key + ".time");
                boolean locked = section.getBoolean(key + ".locked");

                Plot plot = new Plot(owner, id, x, z, time, locked);
                plotMap.put(owner, plot);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load plot for: " + key);
            }
        }
    }
}