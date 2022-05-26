package me.modify.townyboost.hooks.papi;

import com.modify.fundamentum.text.PlugLogger;
import org.bukkit.Bukkit;

/**
 * Represents the hook into PlaceholderAPI.
 */
public class PAPIHook {

    private boolean hooked = false;

    public void check(){
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            setHooked(true);
            PlugLogger.logInfo("PlaceholderAPI detected. Plugin successfully hooked.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    private void setHooked(boolean hooked) {
        this.hooked = hooked;
    }

    public void registerExpansion() {
        if (hooked) {
            new BoostPlaceholderExpansion().register();
            PlugLogger.logInfo("TownyBoost placeholder expansion registered.");
        }
    }

}
