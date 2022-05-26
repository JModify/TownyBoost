package me.modify.townyboost.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.queue.MCMMOBoostQueue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener which listens to all MCMMO-related events.
 */
public class MCMMOListener implements Listener {

    @EventHandler
    public void onMCMMOExpGain(McMMOPlayerXpGainEvent event) {
        MCMMOBoostQueue mcmmoBoostQueue = TownyBoost.getInstance().getBoosterManager().getMcmmoBoostQueue();
        if (mcmmoBoostQueue.isBoosterActive()) {
            Boost activeBoost = mcmmoBoostQueue.getActiveBooster();
            //TownyBoost.getInstance().getDebugger().sendDebugInfo("Normal exp: " + event.getRawXpGained() + ". Multiplied exp: " + (event.getRawXpGained() * activeBoost.getMultiplier()));
            event.setRawXpGained(event.getRawXpGained() * activeBoost.getMultiplier());
        }
    }
}
