package me.modify.townyboost;

import com.modify.fundamentum.Fundamentum;
import com.modify.fundamentum.text.PlugLogger;
import com.modify.fundamentum.util.PlugDebugger;
import com.modify.fundamentum.util.PlugUtil;
import lombok.Getter;
import lombok.Setter;
import me.modify.townyboost.commands.TownyBoostAdminCommand;
import me.modify.townyboost.commands.TownyBoostCommand;
import me.modify.townyboost.data.DataManager;
import me.modify.townyboost.hooks.HookManager;
import me.modify.townyboost.listeners.*;
import me.modify.townyboost.queue.BoosterManager;
import me.modify.townyboost.timers.TimeKeeper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class which connects all aspects of the plugin.
 */
public class TownyBoost extends JavaPlugin {

    @Getter @Setter private static TownyBoost instance;

    @Getter private DataManager dataManager;

    @Getter private PlugDebugger debugger;

    @Getter BoosterManager boosterManager;

    @Getter HookManager hookManager;

    @Override
    public void onEnable() {
        setInstance(this);
        Fundamentum.setPlugin(this);

        initialize();

        registerCommands();
        registerListeners();

        startRepeatingTasks();
        PlugLogger.logInfo("TownyBoost successfully enabled!");
    }

    @Override
    public void onDisable() {
        boosterManager.saveQueues();
        setInstance(null);
    }

    /**
     * Initializes all objects which the plugin uses internally to run properly.
     * Also triggers required initialization methods.
     */
    private void initialize() {
        dataManager = new DataManager();
        dataManager.initialize();

        debugger = new PlugDebugger();
        debugger.setDebugMode(false);

        boosterManager = new BoosterManager();
        boosterManager.loadQueues();

        hookManager = new HookManager();
        hookManager.checkHooks();
        hookManager.registerPAPIExpansion();
    }

    /**
     * Starts repeating tasks duh
     */
    private void startRepeatingTasks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeKeeper(), 0L, 20L);
    }

    /**
     * Registers all commands players/staff can use in-game.
     */
    private void registerCommands() {
        PlugUtil.registerCommand(new TownyBoostAdminCommand());
        PlugUtil.registerCommand(new TownyBoostCommand());
        //PlugUtil.registerCommand(new LTownyBoostAdminCommand());
    }

    /**
     * Registers listeners the plugin requires, to run properly.
     */
    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new JobsListener(), this);
        pluginManager.registerEvents(new MCMMOListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new BoosterListener(), this);
    }

}
