package org.chinasb.common.utility.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 可配置文件处理器
 * @author zhujuan
 *
 */
public class ConfigurableProcessor {
    /**
     * 处理配置对象
     * @param object
     */
    public static void process(Object object) {
        Class<?> clazz = null;
        if ((object instanceof Class)) {
            clazz = (Class<?>) object;
            object = null;
        } else {
            clazz = object.getClass();
        }
        Map<String, ResourceBundle> bundleMaps = new HashMap<String, ResourceBundle>();
        for (Field field : clazz.getDeclaredFields()) {
            Property annotation = (Property) field.getAnnotation(Property.class);
            if (annotation != null) {
                if (Modifier.isFinal(field.getModifiers())) {
                    String fileName = field.getName();
                    String clazzName = clazz.getName();
                    throw new RuntimeException("Attempt to proceed final field " + fileName
                            + " of class " + clazzName);
                }
                String keyName = annotation.key();
                String fileName = annotation.fileName();
                ResourceBundle resourceBundle = (ResourceBundle) bundleMaps.get(fileName);
                if (resourceBundle == null) {
                    bundleMaps.put(fileName, ResourceBundle.getBundle(fileName));
                    resourceBundle = (ResourceBundle) bundleMaps.get(fileName);
                }
                String value = null;
                try {
                    value = resourceBundle.getString(keyName);
                } catch (Exception e) {
                    value = annotation.defaultValue();
                }
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                Object transformValue = transformerFieldValue(value, field, annotation);
                try {
                    field.set(object, transformValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (annotation.required()) {
                        throw new IllegalArgumentException(String.format(
                                "Parse %s ERROR Exception", new Object[] {annotation.key()}));
                    }
                }
            }
        }
    }

    /**
     * 转换字段内容
     * @param value
     * @param field
     * @param annotation
     * @return
     */
    private static Object transformerFieldValue(String value, Field field, Property annotation) {
        try {
            Class<?> fieldType = field.getType();
            Class<? extends PropertyTransformer> formers = annotation.propertyTransformer();
            PropertyTransformer<?> propertyTransformer =
                    PropertyTransformerFactory.newTransformer(fieldType, formers);
            return propertyTransformer.transform(value, field);
        } catch (Exception e) {
        }
        return annotation.defaultValue();
    }
}
