package org.chinasb.common.utility;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        if (clazz == null) {
            return null;
        }

        Type genericType = clazz.getGenericSuperclass();
        if (!(genericType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
        if ((params != null) && (index >= 0) && (index < params.length)
                && (params[index] instanceof Class)) {
            return (Class<?>) params[index];
        }

        return Object.class;
    }
}
