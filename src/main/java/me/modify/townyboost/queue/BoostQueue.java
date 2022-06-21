package me.modify.townyboost.queue;

import com.modify.fundamentum.config.Config;
import lombok.Getter;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.events.BoostActivateEvent;
import me.modify.townyboost.events.BoostExpireEvent;
import me.modify.townyboost.exceptions.BoostFormatException;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * BoostQueue represents a boost queue object. Boost queues also include active boosters
 * whereby the Boost object at the top of the queue is the boost currently active.
 */
public abstract class BoostQueue {

    /**
     * Priority queue containing all boosts currently queued.
     * The boost at the top of this queue is considered "active".
     */
    @Getter protected Deque<Boost> boosts;

    /**
     * Class constructor which initializes the booster queue's actual
     * PriorityQueue object.
     */
    public BoostQueue() {
        this.boosts = new LinkedList<>();
    }

    /**
     * Adds a booster to the queue.
     * @param boost booster to add.
     */
    public void add(Boost boost) {
        boosts.offer(boost);
    }

    /**
     * Clears this boost queue.
     * Clearing entire boost queue does not trigger booster expire event.
     */
    public boolean clear() {
        if (!boosts.isEmpty()) {
            boosts.clear();
            return true;
        }
        return false;
    }

    /**
     * Clears this queue's active booster.
     * Clearing active boosters does not trigger booster expire/activate events.
     * @return
     */
    public boolean clearActive() {
        if (!boosts.isEmpty()) {
            boosts.poll();
            return true;
        }
        return false;
    }

    /**
     * Progresses the boost queue by removing a second from the currently active boost (if any).
     * The currently active boost is the boost which is at the top of the queue.
     *
     * If the currently active boost expires, it will be removed from the queue.
     *
     * This method is run using a timer which executes every second.
     */
    public void progress() {
        if (!boosts.isEmpty()) {
            Boost activeBoost = boosts.peek();
            if (!activeBoost.hasExpired()) {
                activeBoost.deductSecond();
            } else {
                Bukkit.getPluginManager().callEvent(new BoostExpireEvent(activeBoost, getType()));

                boosts.poll();

                if (isBoosterActive()) {
                    Bukkit.getPluginManager().callEvent(new BoostActivateEvent(boosts.peek(), getType()));
                }
            }
        }
    }

    /**
     * Retrieves the booster which is currently active.
     *
     * @requires isBoosterActive is true.
     * @ensures valid Boost return value.
     *
     * @return booster which is currently active.
     */
    public Boost getActiveBooster() {
        return boosts.peek();
    }

    /**
     * Determines whether there is currently a server booster active.
     * @return true if the booster queue is not empty, else false.
     */
    public boolean isBoosterActive() {
        return !boosts.isEmpty();
    }

    /**
     * Determines whether this queue is empty or not.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return boosts.isEmpty();
    }

    /**
     * Determines whether this booster queue is at capacity or not.
     * @return true if booster queue is at capacity, else false.
     */
    public abstract boolean isAtCapacity();

    /**
     * Retrieves the type of booster this queue holds.
     * @return type of booster queue.
     */
    public BoostType getType() {
        if (this instanceof JobsBoostQueue) {
            return BoostType.JOBS;
        } else {
            return BoostType.MCMMO;
        }
    }

    /**
     * Loads this booster queue from the queue.yml data file
     * and caches it in this class.
     */
    public void load() {
        Config boostsFile = TownyBoost.getInstance().getDataManager().getQueueFile();
        if (boostsFile.exists()) {
            FileConfiguration f = boostsFile.getConfig();

            List<String> encodedQueue;
            if (getType() == BoostType.JOBS) {
                encodedQueue = f.getStringList("jobs_queue");
            } else {
                encodedQueue = f.getStringList("mcmmo_queue");
            }

            for (String encodedBoost : encodedQueue) {
                try {
                    Boost boost = Boost.fromString(encodedBoost);
                    this.boosts.add(boost);
                } catch (BoostFormatException e) {
                    e.printStackTrace();
                }
            }

        } else {
            TownyBoost.getInstance().getDebugger().sendDebugError("Failed to cache boost queue from queue.yml file. File does not exist?");
        }

    }

    /**
     * Saves this booster queue to the queue.yml data file.
     */
    public void save() {
        Config boostsFile = TownyBoost.getInstance().getDataManager().getQueueFile();

        if (boostsFile.exists()) {
            FileConfiguration f = boostsFile.getConfig();

            List<String> encodedQueue = new ArrayList<>();
            for (Boost boost : boosts) {
                String encodedBoost = boost.toString();
                encodedQueue.add(encodedBoost);
            }

            if (getType() == BoostType.JOBS) {
                f.set("jobs_queue", encodedQueue);
            } else {
                f.set("mcmmo_queue", encodedQueue);
            }
            boostsFile.saveConfig();
        } else {
            TownyBoost.getInstance().getDebugger().sendDebugError("Failed to save boost queue to queue.yml file. File does not exist or is corrupted.");
        }
    }

    /**
     * Determines whether this booster queue is equal to another object.
     * @param o object to check equality.
     * @return true if they are equal, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoostQueue that = (BoostQueue) o;
        return Objects.equals(boosts, that.boosts);
    }
}
