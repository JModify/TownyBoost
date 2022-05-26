package me.modify.townyboost.queue;

import me.modify.townyboost.data.ConfigHelper;

/**
 * MCMMO boost queue. Stores MCMMO boosters and their placement in a queue.
 */
public class MCMMOBoostQueue extends BoostQueue {

    @Override
    public boolean isAtCapacity() {
        return boosts.size() >= ConfigHelper.getMaxMCMMOQueueSize();
    }

}
