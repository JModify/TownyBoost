package me.modify.townyboost.timers;

import me.modify.townyboost.TownyBoost;

/**
 * The TimeKeeper class is a runnable which is triggered every second to progresses all booster queues.
 * Used to avoid booster queues from progressing when the server is offline.
 */
public class TimeKeeper implements Runnable {

    public void run() {
        TownyBoost.getInstance().getBoosterManager().progressQueues();
    }

}
