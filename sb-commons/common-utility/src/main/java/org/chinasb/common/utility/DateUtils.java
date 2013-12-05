package org.chinasb.common.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class DateUtils {
    
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    public static final String[] WEEKS = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    
    public static boolean isSameWeek(int year, int week, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        return ((year == cal.get(Calendar.YEAR)) && (week == cal.get(Calendar.WEEK_OF_YEAR)));
    }

    public static boolean isSameWeek(Date time, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.setFirstDayOfWeek(firstDayOfWeek);
        return isSameWeek(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR), firstDayOfWeek);
    }

    public static Date firstTimeOfWeek(int firstDayOfWeek, Date time) {
        Calendar cal = Calendar.getInstance();
        if (time != null) {
            cal.setTime(time);
        }
        cal.setFirstDayOfWeek(firstDayOfWeek);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        
        if (day == firstDayOfWeek)
            day = 0;
        else if (day < firstDayOfWeek)
            day += 7 - firstDayOfWeek;
        else if (day > firstDayOfWeek) {
            day -= firstDayOfWeek;
        }
        
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        cal.add(Calendar.DAY_OF_MONTH, -day);
        return cal.getTime();
    }

    public static boolean isToday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date end = cal.getTime();
        cal.add(Calendar.MILLISECOND, -1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date start = cal.getTime();
        return ((date.after(start)) && (date.before(end)));
    }
    
    public static String date2String(Date theDate, String datePattern) {
        if (theDate == null) {
            return "";
        }
        
        DateFormat format = new SimpleDateFormat(datePattern);
        try {
            return format.format(theDate);
        } catch (Exception e) {}
        return "";
    }

    public static Date string2Date(String dateString, String datePattern) {
        if ((dateString == null) || (dateString.trim().isEmpty())) {
            return null;
        }
        
        DateFormat format = new SimpleDateFormat(datePattern);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            log.error("ParseException in Converting String to date: " + e.getMessage());
        }
        
        return null;
    }

    public static long toMillisSecond(long[] seconds) {
        long millis = 0L;
        if ((seconds != null) && (seconds.length > 0)) {
            for (long time : seconds) {
                millis += time * 1000L;
            }
        }
        return millis;
    }

    public static long toSecond(long[] millis) {
        long second = 0L;
        if ((millis != null) && (millis.length > 0)) {
            for (long time : millis) {
                second += time / 1000L;
            }
        }
        return second;
    }

    public static Date changeDateTime(Date theDate, int addDays, int hour, int minute, int second) {
        if (theDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);

        cal.add(Calendar.DAY_OF_MONTH, addDays);

        if ((hour >= 0) && (hour <= 24)) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
        }
        if ((minute >= 0) && (minute <= 60)) {
            cal.set(Calendar.MINUTE, minute);
        }
        if ((second >= 0) && (second <= 60)) {
            cal.set(Calendar.SECOND, second);
        }

        return cal.getTime();
    }

    public static Date add(Date theDate, int addHours, int addMinutes, int addSecond) {
        if (theDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);

        cal.add(Calendar.HOUR_OF_DAY, addHours);
        cal.add(Calendar.MINUTE, addMinutes);
        cal.add(Calendar.SECOND, addSecond);

        return cal.getTime();
    }

    public static int dayOfWeek(Date theDate) {
        if (theDate == null) {
            return -1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String getWeekString(Date date) {
        return WEEKS[dayOfWeek(date) - 1];
    }
    
    public static Date getDate0AM(Date theDate) {
        if (theDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);
        return new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
    }

    public static Date getNextDay0AM(Date theDate) {
        if (theDate == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(theDate.getTime() + 86400000L);
        return new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
    }

    public static Date getThisDay2359PM(Date theDate) {
        if (theDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        long millis = theDate.getTime() + 86400000L - 1000L;
        cal.setTimeInMillis(millis);
        Date date = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
        return new Date(date.getTime() - 1000L);
    }
    
    public static int calc2DateTDOADays(Date startDate, Date endDate) {
        if ((startDate == null) || (endDate == null)) {
            return 0;
        }
        Date startDate0AM = getDate0AM(startDate);
        Date endDate0AM = getDate0AM(endDate);
        long v1 = startDate0AM.getTime() - endDate0AM.getTime();
        return Math.abs((int) Tools.divideAndRoundUp(v1, 86400000.0D, 0));
    }

    public static int calc2DateTDOADays(long startTime, long endTime) {
        return calc2DateTDOADays(new Date(startTime), new Date(endTime));
    }
    
    public static int calc2DateTDOADays(int startTime, int endTime) {
        return calc2DateTDOADays(new Date(startTime * 1000L), new Date(endTime * 1000L));
    }
    
    public static Date getNextMonday(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate0AM(date));
        cal.set(Calendar.DAY_OF_WEEK, 2);

        Calendar nextMondayCal = Calendar.getInstance();
        nextMondayCal.setTimeInMillis(cal.getTimeInMillis() + 604800000L);
        return nextMondayCal.getTime();
    }

    public static Date add(int addDay, boolean to0AM) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addDay);
        Date time = calendar.getTime();
        return ((to0AM) ? getDate0AM(time) : time);
    }

    public static long calculateDelayPeriodStart(int hour, int minute, int second, long delay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        long targetLongTime = calendar.getTimeInMillis();
        long currentLongTime = System.currentTimeMillis();
        if (targetLongTime < currentLongTime) {
            targetLongTime += delay;
        }
        return targetLongTime - currentLongTime;
    }
    
    public static long getCurrentMillis() {
        return System.currentTimeMillis();
    }
    
    public static long getCurrentSecond() {
        return toSecond(new long[] {System.currentTimeMillis()});
    }
}
