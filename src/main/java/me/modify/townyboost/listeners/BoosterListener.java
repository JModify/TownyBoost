package me.modify.townyboost.listeners;

import me.modify.townyboost.broadcast.BoosterBroadcast;
import me.modify.townyboost.broadcast.BroadcastType;
import me.modify.townyboost.events.BoostActivateEvent;
import me.modify.townyboost.events.BoostExpireEvent;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener which listens to all booster-related events.
 */
public class BoosterListener implements Listener {

    @EventHandler
    public void onBoosterActivate(BoostActivateEvent event) {
        Boost booster = event.getBooster();
        BoostType boosterType = event.getBoosterType();

        BoosterBroadcast boosterBroadcast = new BoosterBroadcast(booster, boosterType);
        boosterBroadcast.broadcast("booster_activate", BroadcastType.CHAT);
        boosterBroadcast.broadcast("booster_activate", BroadcastType.DISCORD);
    }

    @EventHandler
    public void onBoosterExpire(BoostExpireEvent event) {
        Boost booster = event.getBooster();
        BoostType boosterType = event.getBoosterType();

        BoosterBroadcast boosterBroadcast = new BoosterBroadcast(booster, boosterType);
        boosterBroadcast.broadcast("booster_expire", BroadcastType.CHAT);
        boosterBroadcast.broadcast("booster_expire", BroadcastType.DISCORD);
    }
}
