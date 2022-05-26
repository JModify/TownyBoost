package me.modify.townyboost.events;

import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * BoosterActivateEvent represents the activation of a booster in-game.
 * The event is called whenever a booster becomes active.
 */
public class BoostActivateEvent extends Event {

    private Boost booster;
    private BoostType boosterType;

    public BoostActivateEvent(Boost booster, BoostType boostType) {
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
