package xyz.cheesetown.auction.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.cheesetown.auction.CTAuction;
import xyz.cheesetown.auction.api.inventory.IClickable;
import xyz.cheesetown.auction.api.inventory.IConfirmable;
import xyz.cheesetown.auction.api.inventory.IPage;
import xyz.cheesetown.auction.data.AuctionData;
import xyz.cheesetown.auction.data.ItemData;
import xyz.cheesetown.auction.exception.CannotGiveableSlotException;
import xyz.cheesetown.auction.exception.NoHaveItemDataException;
import xyz.cheesetown.auction.exception.NotEnoughBalanceException;
import xyz.cheesetown.auction.utils.ColorUtil;
import xyz.cheesetown.auction.utils.InventoryUtil;
import xyz.cheesetown.auction.utils.ItemBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static xyz.cheesetown.auction.CTAuction.PREFIX;

public class AuctionTrader extends InventoryBase implements IClickable, IConfirmable, IPage {

    public final int SLOT_SIZE = 54;
    public final int ITEM_PER_PAGE = 28;
    public final String TITLE = "&c&l경매";

    public int page = 0;

    private ItemData selectedData;

    public AuctionTrader(Player player, @Nullable InventoryHolder backInventory) {
        super(player, backInventory);
    }

    @Override
    protected Inventory initInventory() {
        return Bukkit.createInventory(this, SLOT_SIZE, ColorUtil.toColorString(TITLE));
    }

    @Override
    protected void initialize() {
        inventory.clear();

        ItemStack red = getOutLineItem();
        ItemStack info = getUserInfoItem();

        ItemStack[] preset = {
                red,red ,red ,red ,red ,red ,red ,red ,red,
                red,null,null,null,null,null,null,null,red,
                red,null,null,null,null,null,null,null,red,
                red,null,null,null,null,null,null,null,red,
                red,null,null,null,null,null,null,null,red,
                red,red ,red ,red ,info,red ,red ,red ,red,
        };

        for (int i = 0; i < SLOT_SIZE; i++) {
            inventory.setItem(i, preset[i]);
        }

        if (hasPage(page)) {
            showPage(page);
        } else {
            showPage(0);
        }
    }

    @Override
    public void showConfirmUI(ItemStack item) {
        inventory.clear();
        inventory.setItem(29, getAcceptItem());
        inventory.setItem(33, getDeclineItem());

        inventory.setItem(22, item);
    }

    @Override
    public int getMaxPage() {
        return AuctionData.getInstance().getSize() / ITEM_PER_PAGE + 1;
    }

    @Override
    public void showPage(int page) {
        List<ItemData> itemList = AuctionData.getInstance().getData();

        // update ui
        ItemStack next = hasPage(page + 1) ? nextPageItem() : getOutLineItem();
        ItemStack prev = hasPage(page - 1) ? prevPageItem() : getOutLineItem();

        inventory.setItem(48, prev);
        inventory.setItem(50, next);
        inventory.setItem(49, getUserInfoItem());

        // fill items
        int pos = 9;
        for (int i = 0; i < ITEM_PER_PAGE; i++) {

            while (pos % 9 == 0 || pos % 9 == 8) {
                pos += 1;
            }

            if (itemList.size() <= i + (ITEM_PER_PAGE * (page))) {
                inventory.setItem(pos, null);
                pos++;
                continue;
            }

            ItemData item = itemList.get(i + (ITEM_PER_PAGE * page));
            inventory.setItem(pos, item.getFormattedItem());

            pos++;
        }
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClickEvent(InventoryClickEvent event) {

        if (event.getCurrentItem() == null
            ||!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);

        // 아이템 구매 확정
        IConfirmable.Type checkConfirm = isConfirmItem(clickedItem);
        if (checkConfirm == IConfirmable.Type.ACCEPT && selectedData != null) {
            callAccept();
            return;
        } else if (checkConfirm == IConfirmable.Type.DECLINE) {
            callDecline();
            return;
        }

        // 페이지 기능
        IPage.Type checkPageMove = hasClickedPage(clickedItem);
        if (checkPageMove != IPage.Type.NOT_MATCHED) {
            int pageToMove = page;
            if (checkPageMove == IPage.Type.NEXT) {
                page++;
            } else if (checkPageMove == IPage.Type.PREVIOUS) {
                page--;
            }

            if(hasPage(page)) {
                showPage(page);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                return;
            }
        }

        // 아이템 선택
        ItemData data = AuctionData.getInstance()
                .findFirstData(item -> clickedItem.isSimilar(item.getFormattedItem()));

        if (data != null) {
            ItemStack confirmItem = new ItemBuilder(data.item.clone())
                    .setLore(List.of(
                            "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                            "&e■ &f상품 가격: &r"+String.format("%,d", data.price)+"&f원",
                            "&e■ &b이 아이템을 구매하는것이 확실한가요?",
                            "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    ))
                    .build();

            showConfirmUI(confirmItem);
            selectedData = data;
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
        }
    }

    @Override
    public void callAccept() {
        try {
            if (!InventoryUtil.canItemGivable(player, selectedData.item)) {
                throw new CannotGiveableSlotException();
            }

            tryPayment(selectedData);

            AuctionData.getInstance()
                    .makeSoldOut(selectedData);
            player.getInventory().addItem(selectedData.item);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);

        } catch (NoHaveItemDataException e) {
            player.sendMessage(ColorUtil.toColorString(PREFIX + "&7아이템이 이미 구매되었거나 잘못 되었습니다."));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
        } catch (NotEnoughBalanceException e) {
            player.sendMessage(ColorUtil.toColorString(PREFIX + "&7소지하고 있는 금액이 부족합니다."));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
        } catch (CannotGiveableSlotException e) {
            player.sendMessage(ColorUtil.toColorString(PREFIX + "&7인벤토리 공간이 부족합니다."));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
        }

        for (HumanEntity p : new ArrayList<>(inventory.getViewers())) {
            p.closeInventory();
        }
    }

    @Override
    public void callDecline() {
        selectedData = null;
        this.initialize();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
    }

    private void tryPayment(ItemData data) throws NotEnoughBalanceException, NoHaveItemDataException {

        if (data == null) {
            throw new NoHaveItemDataException();
        }

        int balance = (int) CTAuction.getEconomy().getBalance(player);
        if (balance < data.price) {
            throw new NotEnoughBalanceException();
        }

        CTAuction.getEconomy().withdrawPlayer(player, data.price);
    }

    public ItemStack getOutLineItem() {
        return InventoryUtil.createDesignItem(Material.RED_STAINED_GLASS_PANE);
    }

    @Override
    public ItemStack getAcceptItem() {
        return new ItemBuilder(Material.LIME_WOOL)
                .setDisplayName("&a&l[ 구매 ]")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "",
                        "&e■ 아이템을 구매합니다.",
                        "",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    @Override
    public ItemStack getDeclineItem() {
        return new ItemBuilder(Material.RED_WOOL)
                .setDisplayName("&c&l[ 취소 ]")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "",
                        "&9■ 구매를 취소합니다.",
                        "",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
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

    public ItemStack getUserInfoItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setOwner(player)
                .setDisplayName("&a" + player.getName() + "님의 정보")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&e■ &f현재 소지금은 &7" + String.format("%,d", (int) CTAuction.getEconomy().getBalance(player)) + "원&f입니다.",
                        "&e■ &f경매에서 다양한 아이템들을 구매하거나 판매해보세요.",
                        "&e■ &f현재 페이지: ( &7" + (page + 1) + " &f/ &8" + getMaxPage() +" &f)",
                        "",
                        "&c> 클릭하면 뒤로 갑니다. <",
                        "",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }
}
