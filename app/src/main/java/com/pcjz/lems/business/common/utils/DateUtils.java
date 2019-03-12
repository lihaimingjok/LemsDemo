package com.pcjz.lems.business.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ${YGP} on 2017/5/3.
 */

public class DateUtils {

    /**
     * 当前时间大于time返回true
     * @param time
     * @return
     */
    public static boolean compareNowdDate(String time) {
        long date = 0;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nowDate = new Date(System.currentTimeMillis()).getTime();

        return nowDate > date;
    }

    /**
     * time1大于time2返回true
     * @param time1
     * @param time2
     * @return
     */
    public static boolean compareDate(String time1, String time2) {
        long startDate = 0;
        long endDate = 0;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time1)
                    .getTime();
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time2)
                    .getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startDate > endDate;
    }

    public static int compareDateRetInt(String time1, String time2) {
        long startDate = 0;
        long endDate = 0;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time1)
                    .getTime();
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time2)
                    .getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startDate == endDate) {
            return 1;
        }
        if (startDate > endDate) {
            return 2;
        } else {
            return 3;
        }
    }

    public static String getDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException{
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
