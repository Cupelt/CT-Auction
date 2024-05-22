package xyz.cheesetown.auction.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class InventoryBase implements InventoryHolder {

    protected final Player player;
    protected final Inventory inventory;

    protected InventoryBase(Player player) {
        this.player = player;
        this.inventory = initInventory();

        initialize();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected abstract Inventory initInventory();

    protected abstract void initialize();

}
