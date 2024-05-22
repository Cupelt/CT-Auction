package xyz.cheesetown.auction.data;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.cheesetown.auction.utils.DateUtil;
import xyz.cheesetown.auction.utils.ItemBuilder;

import java.util.List;

public class ItemData {

    public final OfflinePlayer owner;
    public final ItemStack item;
    public final int price;

    public long timestamp;
    public final String message;

    public boolean isSoldOut = false;
    private boolean updateTimestampBefore = false;


    public ItemData(Player owner, ItemStack item, int price, String message) {
        this.owner = owner;
        this.item = item;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
        this.message = message;
    }

    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
        this.updateTimestampBefore = true;
    }

    public boolean isUpdateTimestampBefore() {
        return updateTimestampBefore;
    }

    public ItemStack getFormattedItem() {
        return new ItemBuilder(item.clone()).setLore(List.of(
                "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "",
                "&7■ &f판매자: &a" + owner.getName(),
                "&7■ &f상품 가격: &r" + String.format("%,d", price) +"&f원 ",
                "&7■ &f" + (!updateTimestampBefore ? "등록 일자: &7" : "마지막 끌어올리기: &b") + DateUtil.getDateStr(timestamp, "yyyy.MM.dd/HH:mm:ss"),
                "",
                "&8&m━━━━━━━━━━━━━━━━&8유저 메시지&8&m━━━━━━━━━━━━━━━━━",
                "",
                "&f&o"+(message.isEmpty() ? "(메시지 없음.)" : message),
                "",
                "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        )).build();
    }
}
