package me.modify.townyboost.data;

import com.modify.fundamentum.config.UserData;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.exceptions.BoostFormatException;
import me.modify.townyboost.exceptions.BoostTokenFormatException;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helper class used to read and write values to user data files.
 * UserData files are cleared upon a player leaving the server if their booster data is empty.
 */
public class UserDataHelper {

    public static void asyncAddBoosterToken(UUID playerId, BoostToken token) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UserData userData = UserData.getConfig(playerId);
                if (userData.exists()) {
                    FileConfiguration fileConfiguration = userData.getConfig();

                    List<String> boosts = fileConfiguration.getStringList("tokens");
                    boosts.add(token.toString());

                    fileConfiguration.set("tokens", boosts);
                    userData.saveConfig();
                } else {
                    FileConfiguration f = userData.getConfig();
                    List<String> boosts = new ArrayList<>();
                    boosts.add(token.toString());
                    f.set("tokens", boosts);
                    userData.saveConfig();
                }
            }
        }.runTaskAsynchronously(TownyBoost.getInstance());
    }

    public static void asyncAddBoosterTokenMultiple(UUID playerId, List<BoostToken> tokens) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UserData userData = UserData.getConfig(playerId);
                if (userData.exists()) {
                    FileConfiguration fileConfiguration = userData.getConfig();

                    List<String> boosts = fileConfiguration.getStringList("tokens");
                    for (BoostToken token : tokens) {
                        boosts.add(token.toString());
                    }

                    fileConfiguration.set("tokens", boosts);
                    userData.saveConfig();
                } else {
                    FileConfiguration f = userData.getConfig();
                    List<String> boosts = new ArrayList<>();
                    for (BoostToken token : tokens) {
                        boosts.add(token.toString());
                    }
                    f.set("tokens", boosts);
                    userData.saveConfig();
                }
            }
        }.runTaskAsynchronously(TownyBoost.getInstance());
    }

    public static void asyncRemoveBoostToken(UUID playerId, UUID tokenId) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UserData userData = UserData.getConfig(playerId);
                if (userData.exists()) {
                    FileConfiguration fileConfiguration = userData.getConfig();

                    List<String> encodedBoosts = fileConfiguration.getStringList("tokens");
                    encodedBoosts.removeIf(encodedBoost -> encodedBoost.contains(tokenId.toString()));

                    fileConfiguration.set("tokens", encodedBoosts);
                    userData.saveConfig();
                } else {
                    TownyBoost.getInstance().getDebugger().sendDebugError("Failed to remove boost token from " + tokenId.toString() + ". User has no boosters.");
                }
            }
        }.runTaskAsynchronously(TownyBoost.getInstance());
    }

    public static boolean hasBoosters(UUID playerId) {
        UserData userData = UserData.getConfig(playerId);
        if (userData.exists()) {
            FileConfiguration f = userData.getConfig();
            if (f.isSet("tokens")) {
                List<String> boosts = f.getStringList("tokens");
                return !boosts.isEmpty();
            }
            return false;
        }
        return false;
    }

    public static void deleteUserFile(UUID playerId) {
        UserData userData = UserData.getConfig(playerId);
        if (userData.exists()) {
            userData.delete();
        }
    }

    public static UUID getPlayerId(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (offlinePlayer != null) {
            return offlinePlayer.getUniqueId();
        }
        return null;
    }

    public static List<BoostToken> getBoostTokens(UUID uuid) {
        UserData userData = UserData.getConfig(uuid);
        List<BoostToken> boostTokens = new ArrayList<>();
        if (userData.exists()) {
            FileConfiguration f = userData.getConfig();

            List<String> encodedBoosts = f.getStringList("tokens");
            for (String encodedBoost : encodedBoosts) {
                try {
                    boostTokens.add(BoostToken.fromString(encodedBoost));
                } catch (BoostTokenFormatException e) {
                    e.printStackTrace();
                }
            }

        }
        return boostTokens;
    }



}
