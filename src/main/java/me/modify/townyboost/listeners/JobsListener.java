package me.modify.townyboost.listeners;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.queue.JobsBoostQueue;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

/**
 * Listener which listens to all Jobs-related events.
 */
public class JobsListener implements Listener {

    @EventHandler
    public void onJobsExpGain(JobsExpGainEvent event) {
        JobsBoostQueue jobsBoostQueue = TownyBoost.getInstance().getBoosterManager().getJobsBoostQueue();
        if (ConfigHelper.shouldJobsBoostEXP()) {
            if (jobsBoostQueue.isBoosterActive()) {
                Boost activeBoost = jobsBoostQueue.getActiveBooster();
                TownyBoost.getInstance().getDebugger().sendDebugInfo("Normal exp: " + event.getExp() + ". Multiplied exp: " + (event.getExp() * activeBoost.getMultiplier()));
                event.setExp(event.getExp() * activeBoost.getMultiplier());
            }
        }
    }

    @EventHandler
    public void onJobsPayout(JobsPaymentEvent event) {
        JobsBoostQueue jobsBoostQueue = TownyBoost.getInstance().getBoosterManager().getJobsBoostQueue();
        if (ConfigHelper.shouldJobsBoostPayout()) {
            if (jobsBoostQueue.isBoosterActive()) {
                Boost activeBoost = jobsBoostQueue.getActiveBooster();

                Map<CurrencyType, Double> payment = event.getPayment();
                double pay = payment.get(CurrencyType.MONEY);

                event.set(CurrencyType.MONEY, pay * activeBoost.getMultiplier());
            }
        }
    }



}
