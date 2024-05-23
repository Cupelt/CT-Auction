package xyz.cheesetown.auction.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;

public abstract class InventoryBase implements InventoryHolder {

    protected final Player player;
    protected final Inventory inventory;
    protected final InventoryHolder backInventory;

    protected InventoryBase(Player player, @Nullable InventoryHolder backInventory) {
        this.player = player;
        this.inventory = initInventory();
        this.backInventory = backInventory;

        initialize();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected abstract Inventory initInventory();

    protected abstract void initialize();

    public void goToBack() {
        if (backInventory == null)
            player.closeInventory();
        else
            player.openInventory(backInventory.getInventory());
    }

}
