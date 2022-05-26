package me.modify.townyboost.broadcast;

import com.modify.fundamentum.text.ColorUtil;
import com.modify.fundamentum.text.PlugLogger;
import com.modify.fundamentum.util.PlugUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.util.DiscordUtil;
import lombok.Getter;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.w3c.dom.events.EventException;

import java.util.Arrays;

/**
 * BoosterBroadcast class represents a booster information message that can be sent to a target audience.
 */
public class BoosterBroadcast {

    /** Boost to broadcast information */
    @Getter private Boost boost;

    /** Type of boost being broadcast */
    @Getter private BoostType boostType;

    /**
     * Constructs a new BoosterBroadcast object.
     * @param boost boost to broadcast information.
     * @param boostType type of boost being broadcast.
     */
    public BoosterBroadcast(Boost boost, BoostType boostType) {
        this.boost = boost;
        this.boostType = boostType;
    }

    /**
     * Broadcasts message to a target platform based on the type of broadcast required.
     * @param configKey config key to retrieve configuration values in config.yml "broadcasts" section.
     * @param broadcastType type of broadcast
     */
    public void broadcast(String configKey, BroadcastType broadcastType) {
        FileConfiguration f = TownyBoost.getInstance().getDataManager().getConfigFile();

        String configSection = "broadcasts." + broadcastType.name().toLowerCase() + "." + configKey;

        boolean shouldBroadcast = f.getBoolean(configSection + ".use");
        if (shouldBroadcast) {
            String message = f.getString(configSection + ".message");
            OfflinePlayer player = Bukkit.getOfflinePlayer(boost.getPlayerId());

            if (player.getName() == null) {
                PlugLogger.logError("Failed to replace booster broadcast placeholder. Player replacement not found.");
                return;
            }

            try {
                message = message.replace("{PLAYER}", player.getName());
                message = message.replace("{MULTIPLIER}", String.valueOf(boost.getMultiplier()));
                message = message.replace("{TYPE}", PlugUtil.toTitleCase(boostType.name().toLowerCase()));
            } catch (NullPointerException ignored) {}

            if (broadcastType == BroadcastType.CHAT) {
                broadcastChat(message);
            } else if (broadcastType == BroadcastType.DISCORD) {
                broadcastDiscord(f, configSection, message);
            }
        }
    }

    /**
     * Helper method used to broadcast a chat message to all players online in-game.
     * @param message message to broadcast
     */
    private void broadcastChat(String message) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(ColorUtil.format(message));
        }
    }

    /**
     * Helper method used to broadcast a message to discord if the DiscordSRV hook is present.
     * @param fileConfiguration file configuration of config.yml
     * @param configSection config section to retrieve channel id from.
     * @param message message to send.
     */
    private void broadcastDiscord(FileConfiguration fileConfiguration, String configSection, String message) {
        if (TownyBoost.getInstance().getHookManager().getDiscordSRVHook().isHooked()) {
            String textChannelId = fileConfiguration.getString(configSection + ".text_channel_id");

            TextChannel textChannel = DiscordUtil.getTextChannelById(textChannelId);
            if (textChannel != null) {
                DiscordUtil.queueMessage(textChannel, message);
                return;
            }

            PlugLogger.logInfo("Failed to broadcast " + configSection + " to Discord. Text channel not found!");
        } else {
            TownyBoost.getInstance().getDebugger().sendDebugInfo("Failed to broadcast " + configSection + " to discord. DiscordSRV not hooked.");
        }
    }
}
