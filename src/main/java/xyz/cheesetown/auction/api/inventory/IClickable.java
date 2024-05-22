package xyz.cheesetown.auction.api.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface IClickable {

    public void onClickEvent(InventoryClickEvent event);
}
