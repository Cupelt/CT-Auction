package xyz.cheesetown.auction.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.cheesetown.auction.CTAuction;
import xyz.cheesetown.auction.data.ItemData;
import xyz.cheesetown.auction.inventory.AuctionInventory;
import xyz.cheesetown.auction.utils.ColorUtil;

public class AuctionCommand implements CommandExecutor {
    String PREFIX = "&f&l[&c&l경매&f&l] &f";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {

            if ((args.length > 1) && args[0].equals("등록")) {

                try {
                    int price = Integer.parseInt(args[1]);

                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem.getType() == Material.AIR){
                        sender.sendMessage(ColorUtil.toColorString(PREFIX + "&4아이템을 들고 입력해 주세요."));
                        return true;
                    }

                    StringBuilder msg = new StringBuilder();
                    if (args.length > 2) {

                        for (int i = 2; i < args.length; i++) {
                            msg.append(" ").append(args[i]);
                        }
                    }

                    CTAuction.auctions.add(new ItemData(player, heldItem, price, msg.toString()));
                    player.getInventory().setItemInMainHand(null);
                    sender.sendMessage(ColorUtil.toColorString(PREFIX + "&a성공적으로 아이템이 등록되었습니다!"));
                } catch(NumberFormatException e) {
                    sender.sendMessage(ColorUtil.toColorString(PREFIX + "&4숫자만 입력 해 주세요."));
                }
            } else if(args.length == 1 && args[0].equals("보기")) {
                player.openInventory(new AuctionInventory(player).getInventory());
            } else {
                return false;
            }

        }

        return true;
    }

}
