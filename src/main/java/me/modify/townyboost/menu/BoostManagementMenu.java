package me.modify.townyboost.menu;

import com.modify.fundamentum.menu.MenuItem;
import com.modify.fundamentum.menu.MenuUtility;
import com.modify.fundamentum.menu.PaginatedMenu;
import com.modify.fundamentum.text.ColorUtil;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.broadcast.BoosterBroadcast;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.data.UserDataHelper;
import me.modify.townyboost.events.BoostActivateEvent;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostToken;
import me.modify.townyboost.objects.BoostType;
import me.modify.townyboost.queue.BoosterManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * BoosterManagementMenu is a paginated user interface where players are able to
 * view their purchased booster in-game.
 */
public class BoostManagementMenu extends PaginatedMenu {

    private List<BoostToken> boostTokens;

    private BoostMainMenu boostMainMenu;

    public BoostManagementMenu(Player player, BoostMainMenu boostMainMenu) {
        super(player);
        this.boostMainMenu = boostMainMenu;
        boostTokens = UserDataHelper.getBoostTokens(getPlayer().getUniqueId());
    }

    @Override
    public void addMenuBorder() {

        ItemStack previousPage = MenuUtility.getLeftArrow("&aPrevious Page",  "&7Click to go to previous page.");
        ItemStack homePage = MenuUtility.getChestHead("&aHome Page",  "&7Click to return to main menu.");
        ItemStack nextPage = MenuUtility.getRightArrow("&aNext Page",  "&7Click to go to next page.");

        inventory.setItem(48, previousPage);
        inventory.setItem(49, homePage);
        inventory.setItem(50, nextPage);

        ItemStack fillerGlass = new MenuItem(" ", Material.BLACK_STAINED_GLASS_PANE, false).get();

        MenuUtility.addFillers(inventory, fillerGlass, 0, 9);
        MenuUtility.addFillers(inventory, fillerGlass, 44, 53);

        int[] slotsToReplace = {17, 18, 26, 27, 35, 36};
        for (int i : slotsToReplace) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerGlass);
            }
        }
    }

    @Override
    public String getMenuName() {
        return ColorUtil.format("&4&lBooster Management &r&7(Page " + (page + 1) + ")");
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        if (!boostTokens.isEmpty()) {
            for(int i = 0; i < maxItemsPerPage; i++) {
                index = maxItemsPerPage * page + i;
                if(index >= boostTokens.size()) break;
                if (boostTokens.get(index) != null) {

                    BoostToken boost = boostTokens.get(i);

                    UUID id = boost.getId();
                    BoostType boosterType = boost.getType();
                    int multiplier = boost.getMultiplier();

                    ItemStack boostItem = new MenuItem("&b" + toTitleCase(boosterType.toString().toLowerCase()) + " Booster", Material.DIAMOND,
                            true, Arrays.asList("&7Multiplier: " + multiplier, "&7Duration: 4 hours", " ", "&8" + id.toString(), "&aRight click to activate this booster")).get();


                    inventory.addItem(boostItem);
                }
            }
        }

        ItemStack empty = new MenuItem(" ", Material.BARRIER, false).get();
        MenuUtility.addFillers(inventory, empty);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

        int slot = event.getSlot();
        Player player = getPlayer();

        if (slot == 48) {
            if (page > 0) {
                page = page - 1;
                super.open();
            }
        } else if (slot == 49) {
            boostMainMenu.open();
        } else if (slot == 50) {
            if (!((index + 1) >= boostTokens.size())){
                page = page + 1;
                super.open();
            }
        } else {

            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem.getType() == Material.DIAMOND) {
                if (event.getClick() == ClickType.RIGHT) {
                    ItemMeta meta = clickedItem.getItemMeta();
                    String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                    String[] displayNameParts = displayName.split(" ");
                    BoostType type = BoostType.valueOf(displayNameParts[0].toUpperCase());

                    BoosterManager boosterManager = TownyBoost.getInstance().getBoosterManager();
                    if (type == BoostType.JOBS) {
                        if (boosterManager.getJobsBoostQueue().isAtCapacity()) {
                            player.sendMessage(ColorUtil.format("&4&l(!) &r&cJobs booster queue is currently at capacity. Try again later."));
                            player.closeInventory();
                            return;
                        }
                    } else if (type == BoostType.MCMMO) {
                        if (boosterManager.getMcmmoBoostQueue().isAtCapacity()) {
                            player.sendMessage(ColorUtil.format("&4&l(!) &r&cMCMMO booster queue is currently at capacity. Try again later."));
                            player.closeInventory();
                            return;
                        }
                    }

                    List<String> lore = meta.getLore();
                    int multiplier = 0;
                    try {
                        multiplier = Integer.parseInt(ChatColor.stripColor(lore.get(0)).split(" ")[1]);
                    } catch (NumberFormatException ignored) {}

                    UUID id = UUID.fromString(ChatColor.stripColor(lore.get(3)));

                    int duration = ConfigHelper.getBoostDuration();

                    Boost boost = new Boost(id, player.getUniqueId(), multiplier, duration);
                    UserDataHelper.asyncRemoveBoostToken(player.getUniqueId(), id);

                    if (type == BoostType.JOBS) {
                        if (boosterManager.getJobsBoostQueue().isBoosterActive()) {
                            player.sendMessage(ColorUtil.format("&2&l(✓) &r&aBooster successfully added to the queue."));
                        } else {
                            TownyBoost.getInstance().getServer().getPluginManager().callEvent(new BoostActivateEvent(boost, type));
                        }

                        boosterManager.getJobsBoostQueue().add(boost);
                    } else if (type == BoostType.MCMMO) {
                        if (boosterManager.getMcmmoBoostQueue().isBoosterActive()) {
                            player.sendMessage(ColorUtil.format("&2&l(✓) &r&aBooster successfully added to the queue."));
                        } else {
                            TownyBoost.getInstance().getServer().getPluginManager().callEvent(new BoostActivateEvent(boost, type));
                        }
                        boosterManager.getMcmmoBoostQueue().add(boost);
                    }
                    player.closeInventory();
                }
            }

        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }

        return sb.toString().trim();
    }
}
