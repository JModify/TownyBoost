package me.modify.townyboost.data;

import com.modify.fundamentum.config.Config;
import com.modify.fundamentum.config.UserData;
import com.modify.fundamentum.text.PlugLogger;
import lombok.Getter;
import me.modify.townyboost.TownyBoost;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.InputStream;

/**
 * DataManager class represents the management of all data that is to
 * be read/written internally for the plugin.
 */
public class DataManager {

    /** Queue.yml file, stores active and queued boosters **/
    private Config queueFile;

    /** Config.yml file, stores configurable plugin settings */
    private Config configFile;

    /**
     * Initializes new Config objects and copies defaults for these
     * objects if a physical file does not already exist. Also clears
     * configuration caches.
     */
    public void initialize() {
        UserData.cache.clear();
        Config.cache.clear();

        queueFile = new Config("queue");
        copyDefault(queueFile);

        configFile = new Config("config");
        copyDefault(configFile);
    }

    /**
     * Copy default configuration file from resource to plugin data folder.
     * @param file configuration file to copy
     */
    private void copyDefault(Config file) {
        if (!file.exists()) {
            TownyBoost plugin = TownyBoost.getInstance();
            InputStream stream = plugin.getResource(file.getName() + ".yml");
            Config.copy(stream, file.getFile());
        }
    }

    /**
     * Retrieves the Config object for the queue.yml
     * Used to read and write to the queue.yml.
     * @return Config object for queue.yml.
     */
    public Config getQueueFile() {
        return this.queueFile;
    }

    /**
     * Retrieves the file configuration for the config.yml.
     * Used to read config.yml values.
     * @return config.yml file configuration.
     */
    public FileConfiguration getConfigFile() {
        if (configFile.exists()) {
            return configFile.getConfig();
        } else {
            PlugLogger.logError("Failed to retrieve config.yml file. File does not exist?");
            return null;
        }
    }

    /**
     * Reloads all configuration files
     */
    public void reloadConfigurations() {
        initialize();
    }

}
