package xyz.cheesetown.auction.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDateStr(long timeStamp, String format) {
        if (format==null || format.equals("")) return null;
        Date date = new Date(timeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

}
