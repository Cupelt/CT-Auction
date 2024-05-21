package xyz.cheesetown.auction.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.cheesetown.auction.CTAuction;
import xyz.cheesetown.auction.data.ItemData;
import xyz.cheesetown.auction.exception.CannotGiveableSlotException;
import xyz.cheesetown.auction.exception.NoHaveItemDataException;
import xyz.cheesetown.auction.exception.NotEnoughBalanceException;
import xyz.cheesetown.auction.utils.ColorUtil;
import xyz.cheesetown.auction.utils.InventoryUtil;
import xyz.cheesetown.auction.utils.InventoryUtil.VerifyEnum;
import xyz.cheesetown.auction.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AuctionInventory implements InventoryHolder, Listener {

    public final int SLOT_SIZE = 54;
    public final int ITEM_PER_PAGE = 24;
    public final String PREFIX = "&f&l[&c&l경매&f&f] &f";

    public final String TITLE = "&c&l경매";


    private final Inventory inventory;
    private final Player player;
    public int page = 0;

    private ItemData selectedData;

    ItemStack acceptItem = InventoryUtil.createVerifyItem(VerifyEnum.ACCEPT, "구매");
    ItemStack declineItem = InventoryUtil.createVerifyItem(VerifyEnum.DECLINE, "취소");

    public AuctionInventory(Player player) {
        this.inventory = Bukkit.createInventory(this, SLOT_SIZE, ColorUtil.toColorString(TITLE));
        this.player = player;

        init();
    }

    private void init() {
        inventory.clear();

        ItemStack red = InventoryUtil.createDesignItem(Material.RED_STAINED_GLASS_PANE);
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

        showPageItems(page);
    }

    private void showConfirmUI(ItemData data) {
        inventory.clear();
        inventory.setItem(29, acceptItem);
        inventory.setItem(33, declineItem);

        inventory.setItem(22, new ItemBuilder(data.item.clone())
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&e■ &f상품 가격: &r"+String.format("%,d", data.price)+"&f원",
                        "&e■ &b이 아이템을 구매하는것이 확실한가요?",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build());

        selectedData = data;
    }

    private void showPageItems(int page) {
        List<ItemData> itemList = CTAuction.auctions.stream()
                .filter(item -> !item.isSoldOut)
                .sorted((p1, p2) -> (int)(p2.timestamp - p1.timestamp))
                .toList();

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

    public int getMaxPage() {
        return CTAuction.auctions.size() / ITEM_PER_PAGE;
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionInventory holder) {

            if (event.getCurrentItem() == null
                ||!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            Player player = (Player)event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            event.setCancelled(true);

            // 아이템 구매 확정
            boolean isDeclined = false;
            if (clickedItem.isSimilar(acceptItem) && selectedData != null) {
                try {
                    int index = IntStream.range(0, CTAuction.auctions.size())
                            .filter(i -> selectedData.item.isSimilar(CTAuction.auctions.get(i).item))
                            .findFirst().orElse(-1);

                    tryPayment(CTAuction.auctions.get(index));

                    if (!isItemGivable(player, selectedData.item)) {
                        throw new CannotGiveableSlotException();
                    }

                    ItemData data = CTAuction.auctions.remove(index);
                    player.getInventory().addItem(data.item);


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

                // destroy inventory
                for (HumanEntity p : new ArrayList<>(inventory.getViewers())) {
                    p.closeInventory();
                }
                return;
            }

            if (clickedItem.isSimilar(declineItem)) {
                selectedData = null;
                holder.init();
                return;
            }

            // TODO 페이지 기능

            // 아이템 선택
            CTAuction.auctions.stream()
                    .filter(item -> clickedItem.isSimilar(item.getFormattedItem()))
                    .findFirst().ifPresent(this::showConfirmUI);

        }
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

    private boolean isItemGivable(Player target, ItemStack itemStack) {
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

    public ItemStack getUserInfoItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setOwner(player)
                .setDisplayName("&a" + player.getName() + "님의 정보")
                .setLore(List.of(
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "&e■ &f현재 소지금은 &7" + String.format("%,d", (int) CTAuction.getEconomy().getBalance(player)) + "원&f입니다.",
                        "&e■ &f경매에서 다양한 아이템들을 구매하거나 판매해보세요.",
                        "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }
}
