package me.modify.townyboost.listeners;

import com.modify.fundamentum.menu.Menu;
import com.modify.fundamentum.text.PlugLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Listener which listens to all inventory-related events.
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null || e.getCurrentItem() == null) return;

        InventoryHolder holder = e.getClickedInventory().getHolder();

        if (holder instanceof Menu menu) {
            e.setCancelled(true);
            menu.handleMenu(e);
        }
    }
}
