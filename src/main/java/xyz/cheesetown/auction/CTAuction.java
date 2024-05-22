package xyz.cheesetown.auction;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.cheesetown.auction.command.AuctionCommand;
import xyz.cheesetown.auction.data.AuctionData;
import xyz.cheesetown.auction.data.ItemData;
import xyz.cheesetown.auction.listeners.InventoryEventListener;

import java.util.ArrayList;
import java.util.List;

public final class CTAuction extends JavaPlugin {

    private static CTAuction instance;
    private static Economy econ = null;

    public static final String PREFIX = "&f&l[&c&l경매&f&f] &f";

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupEconomy() ) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new InventoryEventListener(), this);

        this.getCommand("auction").setExecutor(new AuctionCommand());

        AuctionData.createInstance("");

        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static CTAuction getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
