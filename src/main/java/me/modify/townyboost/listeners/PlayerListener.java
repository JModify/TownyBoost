package me.modify.townyboost.listeners;

import me.modify.townyboost.data.UserDataHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Listener which listens to all player-related events.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!UserDataHelper.hasBoosters(playerId)) {
            UserDataHelper.deleteUserFile(playerId);
        }
    }


}
