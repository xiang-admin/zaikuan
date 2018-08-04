package com.pro.warehouse.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static Date stringToDate(String s){
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
       return date;

    }

    public static String dateToString(Date date){
        String dateStr = null;
        dateStr = format.format(date);
        return dateStr;

    }
}
