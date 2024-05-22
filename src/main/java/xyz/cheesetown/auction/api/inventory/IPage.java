package xyz.cheesetown.auction.api.inventory;

import org.bukkit.inventory.ItemStack;

public interface IPage {

    public ItemStack prevPageItem();
    public ItemStack nextPageItem();

    public int getMaxPage();
    public void showPage(int page);

    public default boolean hasPage(int page) {
        return page >= 0 && page < getMaxPage();
    }

    public default Type hasClickedPage(ItemStack current) {
        if (current.isSimilar(prevPageItem())) {
            return Type.PREVIOUS;
        } else if (current.isSimilar(nextPageItem())) {
            return Type.NEXT;
        } else {
            return Type.NOT_MATCHED;
        }
    }

    public enum Type {
        PREVIOUS, NEXT, OTHER, NOT_MATCHED
    }
}
