package org.chinasb.common.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * 时间工具类
 * 
 * @author zhujuan
 *
 */
public class TimeUtils {

    private TimeUtils() {}

    /**
     * Prints the duration in a human readable format as X days Y hours Z minutes etc.
     *
     * @param uptime the up-time in milliseconds
     *
     * @return the time used for displaying on screen or in logs
     */
    public static String printDuration(double uptime) {
        // Code taken from Karaf
        // https://svn.apache.org/repos/asf/karaf/trunk/shell/commands/src/main/java/org/apache/karaf/shell/commands/impl/InfoAction.java

        NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
        NumberFormat fmtD =
                new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

        uptime /= 1000;
        if (uptime < 60) {
            return fmtD.format(uptime) + " seconds";
        }
        uptime /= 60;
        if (uptime < 60) {
            long minutes = (long) uptime;
            String s = fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            return s;
        }
        uptime /= 60;
        if (uptime < 24) {
            long hours = (long) uptime;
            long minutes = (long) ((uptime - hours) * 60);
            String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
            if (minutes != 0) {
                s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            }
            return s;
        }
        uptime /= 24;
        long days = (long) uptime;
        long hours = (long) ((uptime - days) * 24);
        String s = fmtI.format(days) + (days > 1 ? " days" : " day");
        if (hours != 0) {
            s += " " + fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
        }

        return s;
    }

    /**
     * 判断日期是否今天
     * 
     * @param date
     * @return
     */
    public static boolean isToday(LocalDate date) {
        return date.compareTo(LocalDate.now()) == 0;
    }

    /**
     * 计算两个相隔时间
     * 
     * @param start 开始时间
     * @param end 结束时间
     * @param unit 时间类型
     * @return
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * 计算开始时间到今天相隔时间
     * 
     * @param start 开始时间
     * @param unit 时间类型
     * @return
     */
    public static long betweenAtToday(LocalDateTime start, ChronoUnit unit) {
        return between(start, LocalDateTime.now(), unit);
    }

    /**
     * 计算今天零点开始到现在的相隔时间
     * 
     * @param unit 时间类型
     * @return
     */
    public static long betweenAtStartOfDay(ChronoUnit unit) {
        return between(LocalDate.now().atStartOfDay(), LocalDateTime.now(), unit);
    }

    /**
     * 将时间毫秒单位转换成秒单位
     * 
     * @param millis
     * @return
     */
    public static long toSecond(long... millis) {
        long second = 0L;
        if ((millis != null) && (millis.length > 0)) {
            for (long time : millis) {
                second += time / 1000L;
            }
        }
        return second;
    }

    /**
     * 将时间毫秒单位转换成LocalDateTime
     * 
     * @param time
     * @return
     */
    public static LocalDateTime of(long timeInMilli) {
        Instant instant = Instant.ofEpochMilli(timeInMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long of(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String date2String(LocalDateTime time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return time.format(formatter);
    }

    public static LocalDateTime string2Date(String time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, formatter);
    }
}
