package com.fireflyest.btcontrol.util;

/**
 * @author Fireflyest
 * ❤✦✧@❀❁❂☻☠☽⚒⚓⚔⚜⚝✈✂✄✘♯¶♩♪♫♬
 */
public class ConvertUtil {

    private final static String BAR = "▎";

    private ConvertUtil(){

    }

    /**
     * 获取一定数量的符号条
     * @param amount 数量
     * @return String
     */
    public static String convertBar(int amount) {
        String bars = "";
        for(int i = amount ; i > 0 ; i--) { bars = bars.concat(BAR); }
        return bars;
    }

    /**
     * 将long类型转化为时间
     * @param time 时间数据
     * @return String
     */
    public static String convertTime(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;
        long day = time / dd;
        long hour = (time - day * dd)/ hh;
        long minute = (time - day * dd - hour * hh )/ mi;
        long second = (time - day * dd - hour * hh - minute * mi )/ ss;
        String h, m, s;
        h = hour >= 10 ? String.valueOf(hour) : "0"+hour;
        m = minute >= 10 ? String.valueOf(minute) : "0"+minute;
        s = second >= 10 ? String.valueOf(second) : "0"+second;
        return h+":"+m+":"+s;
    }

    /**
     * 将String类型时间转化为long
     * @param time 时间
     * @return date
     */
    public static long convertTime(String time) {
        long date = 1;
        if(time.contains("M")) { date = 1000*60; date *= Integer.parseInt(time.replace("M", "")); 	}
        if(time.contains("H")) { date = 1000*60*60; date *= Integer.parseInt(time.replace("H", "")); }
        if(time.contains("D")) { date = 1000*60*60*24; date *= Integer.parseInt(time.replace("D", "")); }
        return date;
    }

}
