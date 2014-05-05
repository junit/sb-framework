package org.chinasb.common.utility;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Bean工具类
 * @author zhujuan
 */
public class BeanUtility {
    private static Logger LOGGER = LoggerFactory.getLogger(BeanUtility.class);
    private static BeanUtilsBean beanUtilsBean;

    static {
        if (beanUtilsBean == null) {
            beanUtilsBean = BeanUtilsBean.getInstance();
        }
    }

    public static void copyProperties(Object source, Object target, String... ignoreFields) {
        if ((source == null) || (target == null)) {
            return;
        }
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = ignoreFields != null ? Arrays.asList(ignoreFields) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            if ((targetPd.getWriteMethod() != null)
                    && ((ignoreFields == null) || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd =
                        BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if ((sourcePd != null) && (sourcePd.getReadMethod() != null)) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source, new Object[0]);

                        Class sourceType = sourcePd.getPropertyType();
                        PropertyDescriptor pd =
                                BeanUtils.getPropertyDescriptor(target.getClass(),
                                        targetPd.getName());
                        Class targetType = pd.getPropertyType();
                        if ((sourceType.isEnum())
                                && ((Integer.class.equals(targetType)) || (Integer.TYPE
                                        .equals(targetType)))) {
                            if (value == null) {
                                value = Integer.valueOf(0);
                            } else {
                                value =
                                        Integer.valueOf(Enum.valueOf(sourceType,
                                                String.valueOf(value)).ordinal());
                            }
                        } else if ((targetType.isEnum())
                                && ((Integer.class.equals(sourceType)) || (Integer.TYPE
                                        .equals(sourceType)))) {
                            if (value == null) {
                                value = Integer.valueOf(0);
                            }
                            int intValue = ((Integer) value).intValue();
                            Method method = targetType.getMethod("values", new Class[0]);
                            Object[] enumValues =
                                    (Object[]) method.invoke(targetType, new Object[0]);
                            if ((intValue < 0) || (intValue >= enumValues.length)) {
                                continue;
                            }
                            value = enumValues[intValue];
                        }
                        if ((String.class.equals(sourceType))
                                && (Number.class.isAssignableFrom(targetType))) {
                            Constructor constructor =
                                    targetType.getConstructor(new Class[] {String.class});
                            value = constructor.newInstance(new Object[] {String.valueOf(value)});
                        } else if ((String.class.equals(targetType))
                                && (Number.class.isAssignableFrom(sourceType))) {
                            value = String.valueOf(value);
                        }
                        if (((Boolean.class.equals(sourceType)) || (Boolean.TYPE.equals(sourceType)))
                                && ((Integer.class.equals(targetType)) || (Integer.TYPE
                                        .equals(targetType)))) {
                            value = Integer.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
                        } else if (((Boolean.class.equals(targetType)) || (Boolean.TYPE
                                .equals(targetType)))
                                && ((Integer.class.equals(sourceType)) || (Integer.TYPE
                                        .equals(sourceType)))) {
                            value = Boolean.valueOf(((Integer) value).intValue() > 0);
                        }
                        Method writeMethod = targetPd.getWriteMethod();
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        writeMethod.invoke(target, new Object[] {value});
                    } catch (Throwable e) {
                        LOGGER.error("BeanUtil 对象复制出错:", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static Object cloneBean(Object bean) {
        try {
            return beanUtilsBean.cloneBean(bean);
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 对象克隆出错:", e);
            throw new RuntimeException(e);
        }
    }

    public static void copyProperty(Object bean, String name, Object value) {
        try {
            Class propertyClazz = beanUtilsBean.getPropertyUtils().getPropertyType(bean, name);
            if ((propertyClazz.isEnum()) && ((value instanceof Integer))) {
                int intValue = ((Integer) value).intValue();
                Method method = propertyClazz.getMethod("values", new Class[0]);
                Object[] enumValues = (Object[]) method.invoke(propertyClazz, new Object[0]);
                if ((intValue >= 0) && (intValue < enumValues.length)) {
                    value = enumValues[intValue];
                } else {
                    return;
                }
            }
            beanUtilsBean.copyProperty(bean, name, value);
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 对象属性赋值出错:", e);
            throw new RuntimeException(e);
        }
    }

    public static Map describe(Object bean) {
        try {
            return beanUtilsBean.describe(bean);
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 对象克隆出错:", e);
            throw new RuntimeException(e);
        }
    }

    public static Map buildMap(Object bean) {
        if (bean == null) {
            return null;
        }
        try {
            Map map = describe(bean);
            PropertyDescriptor[] pds =
                    beanUtilsBean.getPropertyUtils().getPropertyDescriptors(bean);
            for (PropertyDescriptor pd : pds) {
                Class type = pd.getPropertyType();
                if (type.isEnum()) {
                    Object value =
                            beanUtilsBean.getPropertyUtils().getSimpleProperty(bean, pd.getName());
                    int intValue = 0;
                    if (value != null) {
                        intValue = Enum.valueOf(type, String.valueOf(value)).ordinal();
                    }
                    map.put(pd.getName(), Integer.valueOf(intValue));
                } else if (type == Date.class) {
                    Object value =
                            beanUtilsBean.getPropertyUtils().getSimpleProperty(bean, pd.getName());
                    if (value != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime((Date) value);
                        map.put(pd.getName(), cal.getTime());
                    }
                }
            }
            return map;
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 创建Map失败:", e);
            throw new RuntimeException(e);
        }
    }

    public static List<Map> buildMapList(List beanList) {
        if ((beanList != null) && (!beanList.isEmpty())) {
            List<Map> mapList = new ArrayList<Map>();
            for (Object bean : beanList) {
                mapList.add(buildMap(bean));
            }
            return mapList;
        }
        return null;
    }

    public static Object buildBean(Map map, Class clazz) {
        if (map == null) {
            return null;
        }
        Object bean = null;
        try {
            bean = clazz.newInstance();
            PropertyDescriptor[] pds =
                    beanUtilsBean.getPropertyUtils().getPropertyDescriptors(clazz);
            for (PropertyDescriptor pd : pds) {
                String fieldName = pd.getName();
                if (map.containsKey(fieldName)) {
                    Object mapValue = map.get(fieldName);
                    Class beanType = pd.getPropertyType();
                    Object beanValue = mapValue;
                    if (beanType.isEnum()) {
                        if (mapValue != null) {
                            if ((mapValue instanceof String)) {
                                if (String.valueOf(mapValue).matches("\\d+")) {
                                    mapValue =
                                            Integer.valueOf(Integer.parseInt(String
                                                    .valueOf(mapValue)));
                                    int intValue = ((Integer) mapValue).intValue();

                                    Method method = beanType.getMethod("values", new Class[0]);
                                    Object[] enumValues =
                                            (Object[]) method.invoke(beanType, new Object[0]);
                                    if ((intValue < 0) || (intValue >= enumValues.length)) {
                                        continue;
                                    }
                                    beanValue = enumValues[intValue];
                                } else {
                                    try {
                                        beanValue =
                                                Enum.valueOf(beanType, String.valueOf(mapValue));
                                    } catch (IllegalArgumentException e) {
                                        continue;
                                    }
                                }
                            } else if ((mapValue instanceof Integer)) {
                                int intValue = ((Integer) mapValue).intValue();
                                Method method = beanType.getMethod("values", new Class[0]);
                                Object[] enumValues =
                                        (Object[]) method.invoke(beanType, new Object[0]);
                                if ((intValue < 0) || (intValue >= enumValues.length)) {
                                    continue;
                                }
                                beanValue = enumValues[intValue];
                            }
                        }
                    } else if ((beanType.equals(Date.class)) && (mapValue != null)
                            && ((mapValue instanceof String))) {
                        try {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            beanValue = format.parse(String.valueOf(mapValue));
                        } catch (ParseException e) {
                            LOGGER.error("BeanUtil buildBean string 转 Date 出错!");
                            continue;
                        }
                    }
                    beanUtilsBean.copyProperty(bean, fieldName, beanValue);
                }
            }
            return bean;
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 根据map创建bean出错:", e);
            throw new RuntimeException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            beanUtilsBean.setProperty(bean, name, value);
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 给对象属性赋值出错:", e);
            throw new RuntimeException(e);
        }
    }

    public static Object getProperty(Object bean, String name) {
        try {
            return beanUtilsBean.getPropertyUtils().getSimpleProperty(bean, name);
        } catch (Throwable e) {
            LOGGER.error("BeanUtil 获取对象属性值出错:", e);
            throw new RuntimeException(e);
        }
    }
}
