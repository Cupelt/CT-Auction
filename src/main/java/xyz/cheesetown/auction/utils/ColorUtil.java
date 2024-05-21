package xyz.cheesetown.auction.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorUtil {
    public static String toColorString(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> toColorList(List<String> list) {
        List<String> stringList = new ArrayList<>();

        for (String string : list) {
            stringList.add(toColorString(string));
        }

        return stringList;
    }
}
