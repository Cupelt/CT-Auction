package xyz.cheesetown.auction.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.cheesetown.auction.CTAuction;
import xyz.cheesetown.auction.api.inventory.IClickable;
import xyz.cheesetown.auction.api.inventory.IPage;
import xyz.cheesetown.auction.data.AuctionData;
import xyz.cheesetown.auction.data.ItemData;
import xyz.cheesetown.auction.utils.ItemBuilder;

import javax.annotation.Nullable;
import java.util.List;

public class AuctionMyPage extends InventoryBase implements IClickable, IPage {

    public final int SLOT_SIZE = 36;
    public final int ITEM_PER_PAGE = 27;
    public final String TITLE = "&c&l경매";

    private int page = 0;

    protected AuctionMyPage(Player player, @Nullable InventoryHolder backInventory) {
        super(player, backInventory);
    }

    @Override
    public void onClickEvent(InventoryClickEvent event) {

    }

    @Override
    public ItemStack prevPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setDisplayName("&e이전 페이지")
                .build();
    }

    @Override
    public ItemStack nextPageItem() {
        return new ItemBuilder(Material.ARROW)
                .setDisplayName("&e다음 페이지")
                .build();
    }

    @Override
    public int getMaxPage() {
        return AuctionData.getInstance().getUserItems(player).size() / ITEM_PER_PAGE + 1;
    }

    @Override
    public void showPage(int page) {
        List<ItemData> itemList = AuctionData.getInstance().getUserItems(player);

        for (int i = 0; i < ITEM_PER_PAGE; i++) {
            int index = page * ITEM_PER_PAGE + i;
            if (index >= itemList.size()) {
                inventory.setItem(i, null);
                continue;
            }
            inventory.setItem(i, itemList.get(index).getFormattedItem());
        }
    }

    @Override
    protected Inventory initInventory() {
        return Bukkit.createInventory(this, SLOT_SIZE, TITLE);
    }

    @Override
    protected void initialize() {
        // update ui
        ItemStack next = hasPage(page + 1) ? nextPageItem() : null;
        ItemStack prev = hasPage(page - 1) ? prevPageItem() : null;

        inventory.setItem(30, prev);
        inventory.setItem(32, next);
        inventory.setItem(31, getUserInfoItem());
        inventory.setItem(35, getBatchItem());

        if (hasPage(page)) {
            showPage(page);
        } else {
            showPage(0);
        }
    }

    public ItemStack getBatchItem() {
        return new ItemBuilder(Material.EMERALD)
                .setDisplayName("&b&l일괄 처리")
                .setLore(List.of(
                        "판매 완료된 아이템 금액 회수를, 일괄 처리합니다."
                ))
                .build();
    }

    public ItemStack getUserInfoItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setOwner(player)
                .setDisplayName("&a" + player.getName() + "님의 정보")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&e■ &f현재 소지금은 &7" + String.format("%,d", (int) CTAuction.getEconomy().getBalance(player)) + "원&f입니다.",
                        "&e■ &f현재 페이지: ( &7" + (page + 1) + " &f/ &8" + getMaxPage() +" &f)",
                        "",
                        "&c> 클릭하면 뒤로 갑니다. <",
                        "",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }
}
