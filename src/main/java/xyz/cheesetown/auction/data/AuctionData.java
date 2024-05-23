package xyz.cheesetown.auction.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class AuctionData {

    private static AuctionData instance;
    private final List<ItemData> items = new ArrayList<>();

    private AuctionData(String dataPath) {

        instance = this;
    }

    public void registerItem(Player owner, ItemStack item, int price, String msg) {
        items.add(new ItemData(owner, item, price, msg));
    }

    public List<ItemData> getUserItems(Player player) {
        return items.stream()
                .filter(itemData -> player.equals(itemData.owner))
                .sorted(Comparator.comparing(ItemData::isSoldOut).reversed()
                        .thenComparing(ItemData::getTimestamp, Comparator.reverseOrder()))
                .toList();
    }

    public List<ItemData> getUnsafeData() {
        return items.stream()
                .sorted((p1, p2) -> (int)(p2.timestamp - p1.timestamp))
                .toList();
    }

    public List<ItemData> getData() {
        return getUnsafeData().stream()
                .filter(item -> !item.isSoldOut())
                .toList();
    }

    public List<ItemData> find(Predicate<ItemData> pred) {
        return getData().stream()
                .filter(pred)
                .toList();
    }

    @Nullable
    public ItemData findFirstData(Predicate<ItemData> pred) {
        return getData().stream()
                .filter(pred)
                .findFirst().orElse(null);
    }

    public int getUnsafeSize() {
        return items.size();
    }

    public int getSize() {
        return getData().size();
    }

    public boolean makeSoldOut(ItemData data) {
        int index = items.indexOf(data);

        if (index == -1) {
            return false;
        }

        items.get(index).makeSoldOut();
        return true;
    }

    public static AuctionData getInstance() {
        return instance;
    }

    @Nullable
    public static AuctionData createInstance(String path) {
        if (instance != null) {
            return null;
        }

        return new AuctionData(path);
    }
}
