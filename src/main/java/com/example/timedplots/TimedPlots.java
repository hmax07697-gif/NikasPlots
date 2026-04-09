package com.example.timedplots;

import org.bukkit.plugin.java.JavaPlugin;

public class TimedPlots extends JavaPlugin {

    private static TimedPlots instance;
    private DataManager dataManager;
    private PlotManager plotManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load Config
        saveDefaultConfig();
        
        // Initialize Managers
        this.dataManager = new DataManager(this);
        this.plotManager = new PlotManager(this);

        // Register Commands
        getCommand("plot").setExecutor(new PlotCommand(this));

        // Start Timer Task (Runs every minute)
        getServer().getScheduler().runTaskTimer(this, () -> {
            plotManager.checkPlotExpirations();
        }, 1200L, 1200L); // 1200 ticks = 60 seconds

        getLogger().info("TimedPlots has been enabled!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.savePlots();
        }
    }

    public static TimedPlots getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }
}