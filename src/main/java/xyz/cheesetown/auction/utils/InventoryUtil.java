package xyz.cheesetown.auction.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static ItemStack getPageItem(PageEnum type) {
        ItemBuilder item = new ItemBuilder(Material.ARROW);

        switch (type) {
            case NEXT_PAGE -> item = item.setDisplayName("&e다음 페이지");
            case PREV_PAGE -> item = item.setDisplayName("&e이전 페이지");
        }

        return item.build();
    }

    public static ItemStack createDesignItem(Material mat) {
        return new ItemBuilder(mat).setDisplayName("&a").build();
    }

    public static ItemStack createVerifyItem(VerifyEnum type, String msg) {

        switch (type) {
            case ACCEPT -> {
                return new ItemBuilder(Material.LIME_WOOL)
                        .setDisplayName("&a&l[ "+msg+" ]").build();
            }
            case DECLINE -> {
                return new ItemBuilder(Material.RED_WOOL)
                        .setDisplayName("&c&l[ "+msg+" ]").build();
            }
        }

        return null;
    }


    public enum PageEnum {
        PREV_PAGE, NEXT_PAGE, SELECT_PAGE
    }

    public enum VerifyEnum {
        ACCEPT, DECLINE
    }
}
