package me.modify.townyboost.data;

import com.modify.fundamentum.text.ColorUtil;
import com.modify.fundamentum.text.PlugLogger;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.objects.Boost;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

/**
 * Helper class used to read values from the config.yml.
 */
public class ConfigHelper {

    public static int getMaxJobsQueueSize() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        if (f.isSet("general.max_queue_size.jobs")) {
            return f.getInt("general.max_queue_size.jobs");
        }

        PlugLogger.logError("Failed to retrieve general.max_queue_size.jobs value from config.yml. Corrupted config file?");
        return -1;
    }

    public static int getMaxMCMMOQueueSize() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        if (f.isSet("general.max_queue_size.mcmmo")) {
            return f.getInt("general.max_queue_size.mcmmo");
        }

        PlugLogger.logError("Failed to retrieve general.max_queue_size.mcmmo value from config.yml. Corrupted config file?");
        return -1;
    }

    public static int getBoostDuration() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        if (f.isSet("general.boost_duration")) {
            return f.getInt("general.boost_duration");
        }

        PlugLogger.logError("Failed to retrieve boost_duration value from config.yml. Corrupted config file?");
        return -1;
    }

    public static String getPlaceholderReplacement(String configKey, boolean active, UUID playerId, int multiplier) {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        if (active) {
            if (f.isSet("placeholders." + configKey + ".active")) {
                String message = f.getString("placeholders." + configKey + ".active");

                String playerName = Bukkit.getOfflinePlayer(playerId).getName();
                String multiplierStr = String.valueOf(multiplier);
                try {
                    message = message.replace("{PLAYER}", playerName);
                    message = message.replace("{MULTIPLIER}", multiplierStr);
                } catch (NullPointerException e) {
                    PlugLogger.logError("Failed to replace internal placeholders for message placeholders." + configKey + ".active ! Internal placeholders might not be present.");
                }

                return ColorUtil.format(message);
            } else {
                PlugLogger.logError("Failed to replace internal placeholders for message placeholders." + configKey + ".active ! Corrupted config file.");
                return null;
            }
        } else {
            if (f.isSet("placeholders." + configKey + ".non-active")) {
                return ColorUtil.format(f.getString("placeholders." + configKey + ".non-active"));
            } else {
                PlugLogger.logError("Failed to replace internal placeholders for message placeholders." + configKey + ".active ! Corrupted config file.");
                return null;
            }
        }
    }

    public static String getStoreLink() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        if (f.isSet("general.online_store_message")) {
            return ColorUtil.format(f.getString("general.online_store_message"));
        }

        PlugLogger.logError("Failed to retrieve general.online_store_message value from config.yml. Corrupted config file?");
        return null;
    }

    public static boolean shouldJobsBoostEXP() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();
        return f.getBoolean("general.jobs.exp_boost");
    }

    public static boolean shouldJobsBoostPayout() {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();
        return f.getBoolean("general.jobs.payment_boost");
    }

}
