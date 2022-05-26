package me.modify.townyboost.menu;

import com.modify.fundamentum.menu.Menu;
import com.modify.fundamentum.menu.MenuItem;
import com.modify.fundamentum.menu.MenuUtility;
import com.modify.fundamentum.text.ColorUtil;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.data.UserDataHelper;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostType;
import me.modify.townyboost.queue.BoosterManager;
import me.modify.townyboost.queue.JobsBoostQueue;
import me.modify.townyboost.queue.MCMMOBoostQueue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.units.qual.K;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * BoosterMainMenu represents the user interface where players are able to view boosters
 * both active and queued. Also connects to the BoosterManagementMenu.
 */
public class BoostMainMenu extends Menu {

    public BoostMainMenu(Player player) {
        super(player);
    }

    @Override
    public String getMenuName() {
        return ColorUtil.format("&4&lBooster Menu");
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void setMenuItems() {

        Player player = getPlayer();

        MenuItem jobsBoosterItem = getQueueItem(BoostType.JOBS);
        MenuItem mcmmoBoosterItem = getQueueItem(BoostType.MCMMO);

        List<String> activeBoosterLore = new ArrayList<>();
        BoosterManager boosterManager = TownyBoost.getInstance().getBoosterManager();

        if (!boosterManager.getActiveBoosters().isEmpty()) {

            for (Map.Entry<BoostType, Boost> entry : boosterManager.getActiveBoosters().entrySet()) {
                BoostType type = entry.getKey();
                Boost boost = entry.getValue();

                String name = Bukkit.getOfflinePlayer(boost.getPlayerId()).getName();
                int multiplier = boost.getMultiplier();
                double duration = boost.getDuration();

                activeBoosterLore.add("&a" + name +  "'s " + multiplier + "x " + type.name() + " booster");
                activeBoosterLore.add("&7Ends in " + formatTime((long) duration));
                activeBoosterLore.add(" ");
            }
            activeBoosterLore.remove(activeBoosterLore.size() - 1);

        } else {
            activeBoosterLore.add("&7No active boosters.");
        }
        MenuItem activeBoosterItem = new MenuItem("&2&lActive Boosters", Material.PAPER, true, activeBoosterLore);
        MenuItem boostManagementItem = new MenuItem("&9&lBoost Management", Material.ENDER_EYE, false, Arrays.asList("&7Opens the boost management menu", " ", "&bClick to view or activate your boosters!"));
        MenuItem purchaseBoosters = new MenuItem("&2&lPurchase Boosters", Material.EMERALD, true, Arrays.asList("&aClick here to purchase!"));

        inventory.setItem(4, activeBoosterItem.get());
        inventory.setItem(11, jobsBoosterItem.get());
        inventory.setItem(15, mcmmoBoosterItem.get());
        inventory.setItem(22, boostManagementItem.get());
        inventory.setItem(31, purchaseBoosters.get());

        MenuItem fillerItem = new MenuItem(" ", Material.BLACK_STAINED_GLASS_PANE, false);
        MenuUtility.addFillers(inventory, fillerItem.get());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

        int slot = event.getSlot();
        Player player = getPlayer();

        if (slot == 22) {
            BoostManagementMenu boostManagementMenu = new BoostManagementMenu(player, this);
            boostManagementMenu.open();
        } else if (slot == 31) {
            player.sendMessage(ConfigHelper.getStoreLink());
            player.closeInventory();
        }

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Format time in seconds to a clean string display.
     * E.g 120 seconds would display as 0d 0hr 2m
     * @param time
     * @return
     */
    private String formatTime(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        time -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(time);
        time -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(time);

        return days + "d " + hours + "hr " + minutes + "m";
    }

    /**
     * Retrieves the queue icon and starting times for queued boosters
     * Method can be used for both the jobs queue and the mcmmo queue items.
     * @param type
     * @return
     */
    private MenuItem getQueueItem(BoostType type) {
        /*
         Retrieves either the Jobs booster queue item or MCMMO booster
         queue item based on BoostType value.
         */
        if (type == BoostType.JOBS) {

            JobsBoostQueue jobsBoostQueue = TownyBoost.getInstance().getBoosterManager().getJobsBoostQueue();
            Deque<Boost> boosts = jobsBoostQueue.getBoosts();

            List<String> jobsBoostQueueLore = new ArrayList<>();

            // If the jobs boost queue is empty or only contains 1 booster (therefore
            // making it the "active" booster, then display jobs booster queue as empty on icon.
            if (boosts.isEmpty() || boosts.size() <= 1) {
                jobsBoostQueueLore.add("&7No Jobs boosters currently queued");
            } else {
                long prevDuration = 0;
                for (Boost boost : boosts) {

                    String name = Bukkit.getOfflinePlayer(boost.getPlayerId()).getName();
                    int multiplier = boost.getMultiplier();
                    double duration = boost.getDuration();

                    if (jobsBoostQueue.getActiveBooster().equals(boost)) {
                        prevDuration = (long) duration;
                    } else {
                        long startTime = prevDuration + (long) duration;
                        jobsBoostQueueLore.add("&e" + name + "'s " + multiplier + "x " + type.name() + " booster");
                        jobsBoostQueueLore.add("&7Starts in " + formatTime(prevDuration));
                        jobsBoostQueueLore.add(" ");
                        prevDuration = startTime;
                    }
                }
                jobsBoostQueueLore.remove(jobsBoostQueueLore.size() - 1);
            }
            return new MenuItem("&6&lJobs Booster Queue", Material.ANVIL, false, jobsBoostQueueLore);
        // MCMMO booster queue item
        } else {
            MCMMOBoostQueue mcmmoBoostQueue = TownyBoost.getInstance().getBoosterManager().getMcmmoBoostQueue();
            Deque<Boost> boosts = mcmmoBoostQueue.getBoosts();

            List<String> mcmmoBoostQueueLore = new ArrayList<>();

            // If the jobs boost queue is empty or only contains 1 booster (therefore
            // making it the "active" booster, then display jobs booster queue as empty on icon.
            if (boosts.isEmpty() || boosts.size() <= 1) {
                mcmmoBoostQueueLore.add("&7No MCMMO boosters currently queued");
            } else {
                long prevDuration = 0;
                for (Boost boost : boosts) {

                    String name = Bukkit.getOfflinePlayer(boost.getPlayerId()).getName();
                    int multiplier = boost.getMultiplier();
                    double duration = boost.getDuration();

                    if (mcmmoBoostQueue.getActiveBooster().equals(boost)) {
                        prevDuration = (long) duration;
                    } else {
                        long startTime = prevDuration + (long) duration;
                        mcmmoBoostQueueLore.add("&e" + name + "'s " + multiplier + "x " + type.name() + " booster");
                        mcmmoBoostQueueLore.add("&7Starts in " + formatTime(prevDuration));
                        mcmmoBoostQueueLore.add(" ");
                        prevDuration = startTime;
                    }
                }
                mcmmoBoostQueueLore.remove(mcmmoBoostQueueLore.size() - 1);
            }
            return new MenuItem("&6&lMCMMO Booster Queue", Material.GRASS_BLOCK, false, mcmmoBoostQueueLore);
        }


    }
}
