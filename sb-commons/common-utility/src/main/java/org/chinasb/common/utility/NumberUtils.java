package org.chinasb.common.utility;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数字工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class NumberUtils {

    private static Logger log = LoggerFactory.getLogger(NumberUtils.class);

    @SuppressWarnings("unchecked")
    public static <T> T valueOf(Class<T> resultType, Number value) {
        if (resultType == null) {
            String msg = value.getClass().getSimpleName() + " -> NULL";
            throw new NullPointerException(msg);
        }

        if ((resultType == Integer.TYPE) || (resultType == Integer.class))
            return (T) Integer.valueOf(value.intValue());
        if ((resultType == Double.TYPE) || (resultType == Double.class))
            return (T) Double.valueOf(value.doubleValue());
        if ((resultType == Boolean.TYPE) || (resultType == Boolean.class))
            return (T) Boolean.valueOf(value.doubleValue() > 0.0D);
        if ((resultType == Byte.TYPE) || (resultType == Byte.class))
            return (T) Byte.valueOf(value.byteValue());
        if ((resultType == Long.TYPE) || (resultType == Long.class))
            return (T) Long.valueOf(value.longValue());
        if ((resultType == Short.TYPE) || (resultType == Short.class))
            return (T) Short.valueOf(value.shortValue());
        if ((resultType == Float.TYPE) || (resultType == Float.class))
            return (T) Float.valueOf(value.floatValue());
        if (resultType == Number.class) {
            return (T) value;
        }
        String msg = value.getClass().getSimpleName() + " -> " + resultType.getSimpleName();
        throw new IllegalArgumentException(new ClassCastException(msg));
    }

    public static <T> T[] convertArray(String str, String separator, Class<T> clazz) {
        if ((str != null) && (str.trim().length() > 0)) {
            String[] vals = str.split(separator);
            try {
                return covertArray(clazz, vals, 0, vals.length);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] covertArray(Class<T> clazz, String[] vals, int from, int to)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int end;
        int start;
        if (from > to) {
            start = to;
            end = from;
        } else {
            start = from;
            end = to;
        }

        T[] result = (T[]) Array.newInstance(clazz, to - from);
        Method valueOfMethod = clazz.getMethod("valueOf", new Class[] {String.class});

        boolean accessible = valueOfMethod.isAccessible();
        valueOfMethod.setAccessible(true);
        if (valueOfMethod != null) {
            for (int i = start; i < end; ++i) {
                Object val = valueOfMethod.invoke(clazz, new Object[] {vals[i]});
                result[(i - start)] = (T) val;
            }
        }
        valueOfMethod.setAccessible(accessible);
        return result;
    }

    public static <T> List<T[]> delimiterString2Array(String str, Class<T> clazz) {
        try {
            if ((str == null) || (str.trim().length() <= 0)) {
                return null;
            }
            String[] ss = str.trim().split(Splitable.ELEMENT_SPLIT);
            if ((ss == null) || (ss.length <= 0)) {
                return null;
            }
            List<T[]> list = new ArrayList<T[]>();
            for (int i = 0; i < ss.length; ++i) {
                list.add(convertArray(ss[i], Splitable.ATTRIBUTE_SPLIT, clazz));
            }
            return list;
        } catch (Exception e) {
            log.error("转换字符串时出错：{}", e);
        }
        return null;
    }

    public static <K, V> Map<K, V[]> delimiterString2Map(String delimiterString, Class<K> clazzKey,
            Class<V> clazzVal) {
        Map<K, V[]> map = new HashMap<K, V[]>();
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return map;
        }

        String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
        if ((ss != null) && (ss.length > 0)) {
            for (int i = 0; i < ss.length; ++i) {
                String[] vals = ss[i].split(Splitable.ATTRIBUTE_SPLIT);
                if (vals.length < 1) {
                    continue;
                }
                try {
                    K k = covertArray(clazzKey, vals, 0, 1)[0];
                    V[] v = covertArray(clazzVal, vals, 1, vals.length);
                    map.put(k, v);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> delimiterString2Map2(String delimiterString, Class<K> clazzKey,
            Class<V> clazzVal) {
        Map<K, V> map = new HashMap<K, V>();
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return map;
        }

        String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
        if ((ss != null) && (ss.length > 0)) {
            for (int i = 0; i < ss.length; ++i) {
                String[] vals = ss[i].split(Splitable.ATTRIBUTE_SPLIT);
                if (vals.length < 1) continue;
                try {
                    K k = valueOf(vals[0], clazzKey);
                    V v = valueOf(vals[1], clazzVal);
                    map.put(k, v);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        return map;
    }

    public static <T> List<T> delimiterString2List2(String delimiterString, Class<T> clazz) {
        if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<T>();
        String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
        if ((ss != null) && (ss.length > 0)) {
            for (int i = 0; i < ss.length; ++i) {
                try {
                    T t = valueOf(ss[i], clazz);
                    list.add(t);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> T valueOf(String str, Class<T> clazz) {
        T val = null;
        try {
            Method valueOfMethod = clazz.getMethod("valueOf", new Class[] {String.class});
            if (valueOfMethod != null) {
                val = (T) valueOfMethod.invoke(clazz, new Object[] {str});
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return val;
    }
}
