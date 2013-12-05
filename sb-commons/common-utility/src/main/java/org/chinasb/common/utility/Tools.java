package org.chinasb.common.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class Tools {
    
    private static final Logger log = LoggerFactory.getLogger(Tools.class);
    
    public static final String DELIMITER_INNER_ITEM = "_";
    public static final String DELIMITER_INNER_ITEM1 = ":";
    public static final String DELIMITER_INNER_ITEM2 = ",";
    public static final String DELIMITER_BETWEEN_ITEMS = "|";
    public static final String DELIMITER_BETWEEN_ITEMS2 = "#";
    public static final String ARGS_DELIMITER = " ";
    public static final String ARGS_ITEMS_DELIMITER = "\\|";
    
    private static final Random RANDOM = new Random();

    public static byte[] object2ByteArray(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(obj);
            return bos.toByteArray();
        } catch (IOException ex) {
            log.error("failed to serialize obj", ex);
        }
        return null;
    }

    public static Object byteArray2Object(byte[] buffer) {
        if ((buffer == null) || (buffer.length == 0)) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception ex) {
            log.error("failed to deserialize obj", ex);
            return null;
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (Exception localException5) {}
            try {
                bais.close();
            } catch (Exception localException6) {}
        }
    }

    public static int getRandomInteger(int maxValue) {
        int value = 0;
        if (maxValue > 0) {
            value = RANDOM.nextInt(maxValue);
        }
        return value;
    }

    public static List<String[]> delimiterString2Array(String delimiterString) {
        List<String[]> list = new ArrayList<String[]>();
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return list;
        }
        String[] ss = delimiterString.trim().split(ARGS_ITEMS_DELIMITER);
        if ((ss != null) && (ss.length > 0)) {
            for (String s : ss) {
                list.add(s.split(DELIMITER_INNER_ITEM));
            }
        }
        return list;
    }
    
    public static Map<String, String[]> delimiterString2Map(String delimiterString) {
        Map<String, String[]> map = new HashMap<String, String[]>();
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return map;
        }
        String[] ss = delimiterString.trim().split(ARGS_ITEMS_DELIMITER);
        if ((ss != null) && (ss.length > 0)) {
            for (String s : ss) {
                String[] str = s.split(DELIMITER_INNER_ITEM);
                if (str != null && str.length > 0) {
                    map.put(str[0], str);
                }
            }
        }
        return map;
    }

    public static String delimiterCollection2String(Collection<String[]> collection) {
        if ((collection == null) || (collection.isEmpty())) {
            return "";
        }
        StringBuffer subContent = new StringBuffer();
        for (String[] strings : collection) {
            if (strings == null || strings.length == 0) {
                continue;
            }
            for (int i = 0; i < strings.length; ++i) {
                if (i == strings.length - 1) {
                    subContent.append(strings[i]).append(DELIMITER_BETWEEN_ITEMS);
                } else {
                    subContent.append(strings[i]).append(DELIMITER_INNER_ITEM);
                }
            }
        }
        return subContent.toString();
    }

    public static String array2DelimiterString(String[] subArray) {
        if ((subArray == null) || (subArray.length == 0)) {
            return "";
        }
        StringBuffer subContent = new StringBuffer();
        for (int i = 0; i < subArray.length; ++i) {
            subContent.append(subArray[i]).append(DELIMITER_INNER_ITEM);
        }
        String tmp =
                subContent.toString().substring(0, subContent.lastIndexOf(DELIMITER_INNER_ITEM));
        return tmp + DELIMITER_BETWEEN_ITEMS;
    }

    public static String listArray2DelimiterString(List<String[]> subArrayList) {
        if ((subArrayList == null) || (subArrayList.isEmpty())) {
            return "";
        }
        StringBuffer subContent = new StringBuffer();
        for (String[] strings : subArrayList) {
            if (strings == null || strings.length == 0) {
                continue;
            }
            for (int i = 0; i < strings.length; ++i)
                if (i == strings.length - 1) {
                    subContent.append(strings[i]).append(DELIMITER_BETWEEN_ITEMS);
                } else {
                    subContent.append(strings[i]).append(DELIMITER_INNER_ITEM);
                }
        }
        return subContent.toString();
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 4).doubleValue();
    }

    public static double roundDown(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 1).doubleValue();
    }

    public static double roundUp(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 0).doubleValue();
    }

    public static double divideAndRoundUp(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bd1 = new BigDecimal(v1);
        BigDecimal bd2 = new BigDecimal(v2);
        return bd1.divide(bd2, scale, 0).doubleValue();
    }

    public static double divideAndRoundDown(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bd1 = new BigDecimal(v1);
        BigDecimal bd2 = new BigDecimal(v2);
        return bd1.divide(bd2, scale, 1).doubleValue();
    }

    public static long getQuot(String time1, String time2) {
        long quot = 0L;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = ft.parse(time1);
            Date date2 = ft.parse(time2);
            quot = date1.getTime() - date2.getTime();
            quot = quot / 1000L / 60L / 60L / 24L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

    public static <T> List<T> pageResult(List<T> list, int startIndex, int fetchCount) {
        if ((list != null) && (list.size() > 0)) {
            if (startIndex >= list.size()) {
                return null;
            }
            startIndex = (startIndex < 0) ? 0 : startIndex;
            if (fetchCount <= 0) {
                return list.subList(startIndex, list.size());
            }
            int toIndex = Math.min(startIndex + fetchCount, list.size());
            return list.subList(startIndex, toIndex);
        }
        return null;
    }

    public static void add2MapList(Map<Object, List<Object>> map, Object key, Object value) {
        if ((map != null) && (key != null)) {
            List<Object> list = (List<Object>) map.get(key);
            if (list == null) {
                list = new ArrayList<Object>();
                map.put(key, list);
            }
            list.add(value);
        }
    }
}
