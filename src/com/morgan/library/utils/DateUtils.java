package com.morgan.library.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";
    public static final String COMPLETE_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    /**
     * 按照format输出date的字符串，format格式如：yyyy-MM-dd hh:mm:ss。
     * 
     * @param date
     * @return
     */
    public static String dateToString(Date date, String format) {
        String timeString = null;
        if (date == null) {
            return "";
        }
        try {
            DateFormat formatDate = new SimpleDateFormat(format, Locale.CHINESE);
            timeString = formatDate.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeString;
    }
}
