package me.modify.townyboost.queue;

import me.modify.townyboost.data.ConfigHelper;

/**
 * Jobs boost queue. Stores Jobs boosters and their placement in a queue.
 */
public class JobsBoostQueue extends BoostQueue {

    @Override
    public boolean isAtCapacity() {
        return boosts.size() >= ConfigHelper.getMaxJobsQueueSize();
    }

}
