package xyz.cheesetown.auction.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {

    public static ItemStack createDesignItem(Material mat) {
        return new ItemBuilder(mat).setDisplayName("&a").build();
    }

    public static boolean canItemGivable(Player target, ItemStack itemStack) {
        PlayerInventory playerInventory = target.getInventory();
        int size = 0;
        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack item = playerInventory.getItem(i);
            if (item == null || item.getType().isAir()) {
                size += itemStack.getMaxStackSize();
            } else if (item.isSimilar(itemStack)) {
                size += item.getMaxStackSize() - item.getAmount();
            }
            if (size >= itemStack.getAmount())
                return true;
        }
        return false;
    }
}
