package org.chinasb.common.utility;

/**
 * 数值转换工具类
 * @author zhujuan
 */
public class NumberUtility {
    
    /**
     * Number类型转换
     * @param resultType
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T valueOf(Class<T> resultType, Number value) {
        if (resultType == null) {
            throw new NullPointerException(value.getClass().getSimpleName() + " -> NULL");
        }
        if ((resultType == Integer.TYPE) || (resultType == Integer.class)) {
            return (T) Integer.valueOf(value.intValue());
        }
        if ((resultType == Double.TYPE) || (resultType == Double.class)) {
            return (T) Double.valueOf(value.doubleValue());
        }
        if ((resultType == Boolean.TYPE) || (resultType == Boolean.class)) {
            return (T) Boolean.valueOf(value.doubleValue() > 0.0D);
        }
        if ((resultType == Byte.TYPE) || (resultType == Byte.class)) {
            return (T) Byte.valueOf(value.byteValue());
        }
        if ((resultType == Long.TYPE) || (resultType == Long.class)) {
            return (T) Long.valueOf(value.longValue());
        }
        if ((resultType == Short.TYPE) || (resultType == Short.class)) {
            return (T) Short.valueOf(value.shortValue());
        }
        if ((resultType == Float.TYPE) || (resultType == Float.class)) {
            return (T) Float.valueOf(value.floatValue());
        }
        if (resultType == Number.class) {
            return (T) value;
        }
        throw new IllegalArgumentException(new ClassCastException(value.getClass().getSimpleName()
                + " -> " + resultType.getSimpleName()));
    }
}
