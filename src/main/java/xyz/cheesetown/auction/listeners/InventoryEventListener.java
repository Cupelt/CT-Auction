package xyz.cheesetown.auction.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import xyz.cheesetown.auction.api.inventory.IClickable;

public class InventoryEventListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof IClickable) {
            ((IClickable) holder).onClickEvent(event);
        }
    }
}
