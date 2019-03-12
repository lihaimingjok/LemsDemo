package com.pcjz.lems.business.common.utils;

/**
 * Created by ${YGP} on 2017/7/3.
 */

public class DateFormat {
    public static String getTimeFormat(String time) {
        return time.substring(0, 4) + "/" + time.substring(5, 7) + "/" + time.substring(8, 10);
    }
}
