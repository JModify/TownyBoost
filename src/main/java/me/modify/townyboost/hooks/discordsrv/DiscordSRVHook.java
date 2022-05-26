package me.modify.townyboost.hooks.discordsrv;

import com.modify.fundamentum.text.PlugLogger;
import me.modify.townyboost.TownyBoost;
import org.bukkit.Bukkit;

/**
 * Represents the hook into DiscordSRV
 */
public class DiscordSRVHook {

    private boolean hooked = false;

    public void check(){
        if (Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            setHooked(true);
            PlugLogger.logInfo("DiscordSRV detected. Plugin successfully hooked.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    private void setHooked(boolean hooked) {
        this.hooked = hooked;
    }

}
