package xyz.cheesetown.auction.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack itemstack) {
        this.item = itemstack.clone();
        this.meta = item.getItemMeta();
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setOwner(Player player) {
        if (item.getType() != Material.PLAYER_HEAD) {
            return this;
        }

        ((SkullMeta) meta).setOwningPlayer(player);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(ColorUtil.toColorList(lore));
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        meta.setDisplayName(ColorUtil.toColorString(displayName));

        return this;
    }

}
