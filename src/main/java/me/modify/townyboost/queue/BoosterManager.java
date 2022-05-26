package me.modify.townyboost.queue;

import lombok.Getter;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BoosterManager class is used for simple, simultaneous management of all booster queues.
 */
public class BoosterManager {

    /** Boost queue for Jobs plugin */
    @Getter private JobsBoostQueue jobsBoostQueue;

    /** Boost queue for MCMMO plugin */
    @Getter private MCMMOBoostQueue mcmmoBoostQueue;

    /**
     * Constructs a new booster manager object and initializes all managed queues.
     */
    public BoosterManager() {
        this.jobsBoostQueue = new JobsBoostQueue();
        this.mcmmoBoostQueue = new MCMMOBoostQueue();
    }

    /**
     * Loads all managed queues from the queue.yml file.
     * Called upon server start/reload.
     */
    public void loadQueues() {
        jobsBoostQueue.load();
        mcmmoBoostQueue.load();
    }

    /**
     * Saves all managed queues to the queue.yml file.
     * Called upon server stop/reload.
     */
    public void saveQueues() {
        jobsBoostQueue.save();
        mcmmoBoostQueue.save();
    }

    /**
     * Progresses all managed queues.
     */
    public void progressQueues() {
        jobsBoostQueue.progress();
        mcmmoBoostQueue.progress();
    }

    /**
     * Clears queue if there is anything to clear.
     * @param type booster queue type to clear
     * @return true if there were boosters to be cleared, else false.
     */
    public boolean clearQueue(BoostType type) {
        if (type == BoostType.JOBS) {
            return jobsBoostQueue.clear();
        } else if (type == BoostType.MCMMO){
            return mcmmoBoostQueue.clear();
        }
        return false;
    }

    /**
     * Clears the active booster for queue type if there is anything to clear.
     * @param type type of queue to clear active booster for.
     * @return true if there was an active booster to clear, else false.
     */
    public boolean clearActive(BoostType type) {
        if (type == BoostType.JOBS) {
            return jobsBoostQueue.clearActive();
        } else if (type == BoostType.MCMMO){
            return mcmmoBoostQueue.clearActive();
        }
        return false;
    }

    /**
     * Retrieves a map of active boosters and their boost type.
     * @return hashmap of active boosters.
     */
    public Map<BoostType, Boost> getActiveBoosters() {
        Map<BoostType, Boost> activeBoosters = new HashMap<>();

        if (jobsBoostQueue.isBoosterActive()) {
            activeBoosters.put(BoostType.JOBS, jobsBoostQueue.getActiveBooster());
        }

        if (mcmmoBoostQueue.isBoosterActive()) {
            activeBoosters.put(BoostType.MCMMO, mcmmoBoostQueue.getActiveBooster());
        }

        return activeBoosters;
    }
}
