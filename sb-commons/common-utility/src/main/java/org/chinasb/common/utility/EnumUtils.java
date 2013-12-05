package org.chinasb.common.utility;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 枚举工具类
 * 
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class EnumUtils {

    private static Logger logger = LoggerFactory.getLogger(EnumUtils.class);

    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String fieldName) {
        return Enum.valueOf(enumClass, fieldName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, int value) {
        try {
            if (value < 0) {
                return null;
            }
            Method method = enumClass.getMethod("values", new Class[0]);
            Enum<T>[] values = (Enum<T>[]) method.invoke(enumClass, new Object[0]);
            if ((values != null) && (values.length > value)) {
                return (T) values[value];
            }
            return null;
        } catch (Exception e) {
            logger.error("构建枚举 [Class: {} - Value: {} ] 出现异常",
                    new Object[] {enumClass, Integer.valueOf(value), e});
        }
        return null;
    }
}
