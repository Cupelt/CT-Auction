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
import xyz.cheesetown.auction.data.AuctionData;
import xyz.cheesetown.auction.utils.ColorUtil;
import xyz.cheesetown.auction.utils.InventoryUtil;
import xyz.cheesetown.auction.utils.ItemBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AuctionUI extends InventoryBase implements IClickable {
    public final int SLOT_SIZE = 27;
    public final String TITLE = "&c&l경매";

    protected AuctionUI(Player player, @Nullable InventoryHolder backInventory) {
        super(player, backInventory);
    }

    @Override
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null
                ||!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);

        if (clickedItem.isSimilar(getUserInfoItem())) {
            goToBack();
        } else if (clickedItem.isSimilar(getGoToAuctionItem())) {
            player.openInventory(new AuctionTrader(player, this).getInventory());
        } else if (clickedItem.isSimilar(getMyPageItem())) {
            player.openInventory(new AuctionMyPage(player, this).getInventory());
        }
    }

    @Override
    protected Inventory initInventory() {
        return Bukkit.createInventory(this, SLOT_SIZE, ColorUtil.toColorString(TITLE));
    }

    @Override
    protected void initialize() {
        ItemStack green = getOutLineItem();
        ItemStack info = getUserInfoItem();

        ItemStack tuto = new ItemBuilder(Material.ENCHANTED_BOOK)
                .setDisplayName("&b&l경매장 이용 방법.")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&f경매장에 등록할 사용할 아이템을 들고,",
                        "&e./경매장 등록 &6<가격> <메시지(생략가능)> &f를 입력 해 주세요.",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();

        ItemStack auc = getGoToAuctionItem();
        ItemStack myPage = getMyPageItem();

        ItemStack[] preset = {
                green, green, green, green, green, green, green, green, green,
                green, null , auc  , null ,myPage, null , tuto , null , green,
                green, green, green, green, info , green, green, green, green,
        };

        for (int i = 0; i < SLOT_SIZE; i++) {
            inventory.setItem(i, preset[i]);
        }
    }

    public ItemStack getGoToAuctionItem() {
        return new ItemBuilder(Material.EMERALD)
                .setDisplayName("&c&l상품 목록")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&f경매장에 진열된 아이템 품목을 확인하고, 구매합니다.",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    public ItemStack getMyPageItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        lore.add("&f내가 현재 팔고 있는 물품을 확인합니다.");

        int soldItem = AuctionData.getInstance().getUnsafeData().stream()
                .filter(data -> data.owner.equals(player) && data.isSoldOut())
                .toList().size();
        if (soldItem > 0) {
            lore.add("");
            lore.add("&a&l > 판매된 물품이 &e&l" + soldItem + " &a&l개 있습니다! <");
            lore.add("");
        }
        lore.add("&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return new ItemBuilder(Material.CLOCK)
                .setDisplayName("&a&l현재 내가 판매중인 물품.")
                .setLore(lore)
                .build();
    }

    public ItemStack getOutLineItem() {
        return InventoryUtil.createDesignItem(Material.LIME_STAINED_GLASS_PANE);
    }

    public ItemStack getUserInfoItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setOwner(player)
                .setDisplayName("&a" + player.getName() + "님의 정보")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&e■ &f현재 소지금은 &7" + String.format("%,d", (int) CTAuction.getEconomy().getBalance(player)) + "원&f입니다.",
                        "&e■ &f경매에서 다양한 아이템들을 구매하거나 판매해보세요.",
                        "",
                        "&c> 클릭하면 뒤로 갑니다. <",
                        "",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }
}
