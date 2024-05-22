package xyz.cheesetown.auction.api.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IConfirmable {
    public void showConfirmUI(ItemStack item);

    public ItemStack getAcceptItem();
    public ItemStack getDeclineItem();

    public void callAccept(InventoryClickEvent event);
    public void callDecline(InventoryClickEvent event);

    public default Type isConfirmItem(ItemStack current) {
        if (current.isSimilar(getAcceptItem())) {
            return Type.ACCEPT;
        } else if (current.isSimilar(getDeclineItem())) {
            return Type.DECLINE;
        } else {
            return Type.NOT_MATCHED;
        }
    }

    public enum Type {
        ACCEPT, DECLINE, NOT_MATCHED
    }
}
