package me.modify.townyboost.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.queue.BoosterManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Expands PlaceholderAPI to recognize booster placeholders controlled by this plugin.
 */
public class BoostPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "booster";
    }

    @Override
    public @NotNull String getAuthor() {
        return "modify";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equals("mcmmo")) {
            BoosterManager manager = TownyBoost.getInstance().getBoosterManager();

            if (manager.getMcmmoBoostQueue().isBoosterActive()) {
                Boost activeBoost = manager.getMcmmoBoostQueue().getActiveBooster();
                return ConfigHelper.getPlaceholderReplacement("mcmmo", true, activeBoost.getPlayerId(), activeBoost.getMultiplier());
            } else {
                return ConfigHelper.getPlaceholderReplacement("mcmmo", false, null, -1);
            }
        }

        if (params.equals("jobs")) {
            BoosterManager manager = TownyBoost.getInstance().getBoosterManager();

            if (manager.getJobsBoostQueue().isBoosterActive()) {
                Boost activeBoost = manager.getJobsBoostQueue().getActiveBooster();
                return ConfigHelper.getPlaceholderReplacement("jobs", true, activeBoost.getPlayerId(), activeBoost.getMultiplier());
            } else {
                return ConfigHelper.getPlaceholderReplacement("jobs", false, null, -1);
            }
        }

        return null;
    }
}
