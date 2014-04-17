package org.chinasb.common.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

/**
 * 反射工具类
 * @author zhujuan
 */
public abstract class ReflectionUtility extends ReflectionUtils {

    public static <A extends Annotation> Field findUniqueFieldWithAnnotation(Class<?> clazz,
            final Class<A> type) {
        final List<Field> fields = new ArrayList<Field>();
        FieldFilter fieldFilter = new FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.isAnnotationPresent(type);
            }
        };
        FieldCallback fieldCallback = new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fields.add(field);
            }
        };
        doWithFields(clazz, fieldCallback, fieldFilter);
        if (fields.size() > 1) {
            throw new IllegalStateException("被注释" + type.getSimpleName() + "声明的域不唯一");
        }
        if (fields.size() == 1) {
            return (Field) fields.get(0);
        }
        return null;
    }

    public static void doWithDeclaredFields(Class<?> clazz, ReflectionUtils.FieldCallback fc,
            ReflectionUtils.FieldFilter ff) throws IllegalArgumentException {
        if ((clazz == null) || (clazz == Object.class)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ((ff != null) && (!(ff.matches(field)))) continue;
            try {
                fc.doWith(field);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("非法访问属性 '" + field.getName() + "': " + ex);
            }
        }
    }

    public static Field getFirstDeclaredFieldWith(Class<?> clz,
            Class<? extends Annotation> annotationClass) {
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                return field;
            }
        }
        return null;
    }

    public static Field getRecursiveFirstDeclaredFieldWith(Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        Field currField = null;
        if ((clazz != null) && (clazz != Object.class)) {
            currField = getFirstDeclaredFieldWith(clazz, annotationClass);
            if (currField == null) {
                return getRecursiveFirstDeclaredFieldWith(clazz.getSuperclass(), annotationClass);
            }
        }
        return currField;
    }

    public static Field[] getDeclaredFieldsWith(Class<?> clz,
            Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                fields.add(field);
            }
        }
        return ((Field[]) fields.toArray(new Field[0]));
    }

    public static Method getFirstDeclaredMethodWith(Class<?> clz,
            Class<? extends Annotation> annotationClass) {
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
    }

    public static Method[] getDeclaredMethodsWith(Class<?> clz,
            Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                methods.add(method);
            }
        }
        return ((Method[]) methods.toArray(new Method[0]));
    }

    public static Method[] getDeclaredGetMethodsWith(Class<?> clz,
            Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method : clz.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) == null) {
                continue;
            }
            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            methods.add(method);
        }
        return ((Method[]) methods.toArray(new Method[0]));
    }
}
