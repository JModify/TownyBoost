package me.modify.townyboost.events;

import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * BoosterExpireEvent represents the expiry of a booster in-game.
 * The event is called when a booster's duration reaches 0.
 */
public class BoostExpireEvent extends Event {

    private Boost booster;
    private BoostType boosterType;

    public BoostExpireEvent(Boost booster, BoostType boostType) {
        this.booster = booster;
        this.boosterType = boostType;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Boost getBooster() {
        return this.booster;
    }

    public BoostType getBoosterType () {
        return this.boosterType;
    }
}
