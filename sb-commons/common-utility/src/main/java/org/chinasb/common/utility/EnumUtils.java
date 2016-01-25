package org.chinasb.common.utility;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(EnumUtils.class);

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
            T[] values = (T[]) method.invoke(enumClass, new Object[0]);
            if ((values != null) && (values.length > value)) {
                return values[value];
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("构建枚举 [Class: {} - Value: {} ] 出现异常",
                    new Object[] {enumClass, Integer.valueOf(value), e});
        }
        return null;
    }
}
