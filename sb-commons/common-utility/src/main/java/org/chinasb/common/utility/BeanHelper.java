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
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean工具类
 * 
 * @author zhujuan
 */
public class BeanHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);
	private static final BeanUtilsBean BEANUTILSBEAN = BeanUtilsBean.getInstance();


	/**
	 * bean属性拷贝
	 * 
	 * @param source
	 * @param target
	 * @param ignoreFields 可以不传 如：copyProperties(Object source, Object target)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void copyProperties(Object source, Object target, String... ignoreFields) {
		if (source == null || target == null) {
			return;
		}

		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds =
				org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd == null) {
				continue;
			}

			if (targetPd.getWriteMethod() == null) {
				continue;
			}

			if (ignoreFields != null && Arrays.asList(ignoreFields).contains(targetPd.getName())) {
				continue;
			}

			PropertyDescriptor sourcePd =
					getPropertyDescriptor(source.getClass(), targetPd.getName());
			if (sourcePd == null || sourcePd.getReadMethod() == null) {
				continue;
			}

			try {
				Method readMethod = sourcePd.getReadMethod();
				if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}

				Object value = readMethod.invoke(source);
				Class sourceType = sourcePd.getPropertyType();
				Class targetType = getPropertyDescriptor(target.getClass(), targetPd.getName())
						.getPropertyType();
				if (sourceType.isEnum()
						&& (Integer.class.equals(targetType) || int.class.equals(targetType))) {// 源对象属性是枚举
					value = value == null ? -1 : value;
					value = EnumUtils.getEnum(sourceType, (Integer) value).ordinal();
				} else if (targetType.isEnum()
						&& (Integer.class.equals(sourceType) || int.class.equals(sourceType))) {// 目标对象属性是枚举
					value = value == null ? -1 : value;
					value = EnumUtils.getEnum(targetType, (Integer) value);
				}

				if (String.class.equals(sourceType) && Number.class.isAssignableFrom(targetType)) {
					Constructor constructor = targetType.getConstructor(String.class);
					value = constructor.newInstance(String.valueOf(value));
				} else if (String.class.equals(targetType)
						&& Number.class.isAssignableFrom(sourceType)) {
					value = String.valueOf(value);
				}

				if ((Boolean.class.equals(sourceType) || boolean.class.equals(sourceType))
						&& (Integer.class.equals(targetType) || int.class.equals(targetType))) {
					value = (Boolean) value ? 1 : 0;
				} else if ((Boolean.class.equals(targetType) || boolean.class.equals(targetType))
						&& (Integer.class.equals(sourceType) || int.class.equals(sourceType))) {
					value = (Integer) value > 0 ? true : false;
				}

				Method writeMethod = targetPd.getWriteMethod();
				if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					writeMethod.setAccessible(true);
				}

				writeMethod.invoke(target, value);
			} catch (Throwable e) {
				LOGGER.error("BeanUtil 对象复制出错:", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 获得Bean的解释器
	 * 
	 * @param clazz 类对象
	 * @param targetName 目标名
	 * @return {@link PropertyDescriptor} 解释器
	 */
	private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String targetName) {
		return org.springframework.beans.BeanUtils.getPropertyDescriptor(clazz, targetName);
	}

	/**
	 * 克隆对象
	 * 
	 * @param bean 需要克隆的对象
	 * @return {@link Object} 克隆后的对象
	 */
	public static Object cloneBean(Object bean) {
		try {
			return BEANUTILSBEAN.cloneBean(bean);
		} catch (Throwable e) {
			LOGGER.error("BeanUtil 对象克隆出错:", e);
			throw new RuntimeException(e);
		}
	}


	/**
	 * 拷贝属性给对象(类型宽松)
	 * 
	 * @param bean
	 * @param name 属性名
	 * @param value 属性值
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void copyProperty(Object bean, String name, Object value) {
		try {
			Class propertyClazz = BEANUTILSBEAN.getPropertyUtils().getPropertyType(bean, name);
			if (propertyClazz.isEnum() && value instanceof Integer) {// 属性枚举型 目标值是整型
				value = EnumUtils.getEnum(propertyClazz, (Integer) value);
			}
			BEANUTILSBEAN.copyProperty(bean, name, value);
		} catch (Throwable e) {
			LOGGER.error("BeanUtil 对象属性赋值出错:", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 将bean装换为一个map(不能将枚举转换为int)
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings({"rawtypes"})
	public static Map describe(Object bean) {
		try {
			return BEANUTILSBEAN.describe(bean);
		} catch (Throwable e) {
			LOGGER.error("BeanUtil 对象克隆出错:", e);
			throw new RuntimeException(e);
		}
	}


	/**
	 * 将bean装换为一个map(能将枚举转换为int)
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Map buildMap(Object bean) {
		if (bean == null) {
			return null;
		}
		try {
			Map map = describe(bean);
			PropertyDescriptor[] pds =
					BEANUTILSBEAN.getPropertyUtils().getPropertyDescriptors(bean);
			for (PropertyDescriptor pd : pds) {
				Class type = pd.getPropertyType();
				if (type.isEnum()) {
					Object value =
							BEANUTILSBEAN.getPropertyUtils().getSimpleProperty(bean, pd.getName());
					Enum enums = EnumUtils.valueOf(type, String.valueOf(value));
					map.put(pd.getName(), enums == null ? -1 : enums.ordinal());
				} else if (type == java.util.Date.class) {// 防止是Timestamp
					Object value =
							BEANUTILSBEAN.getPropertyUtils().getSimpleProperty(bean, pd.getName());
					if (value != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime((java.util.Date) value);
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

	/**
	 * 将bean列表转换成map的列表
	 * 
	 * @param beanList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> buildMapList(List beanList) {
		if (beanList != null && !beanList.isEmpty()) {
			List<Map> mapList = new ArrayList<Map>();
			for (Object bean : beanList) {
				mapList.add(buildMap(bean));
			}
			return mapList;
		}
		return null;
	}


	/**
	 * 将map转Bean
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Object buildBean(Map map, Class clazz) {
		if (map == null) {
			return null;
		}
		Object bean = null;
		try {
			bean = clazz.newInstance();
			PropertyDescriptor[] pds =
					BEANUTILSBEAN.getPropertyUtils().getPropertyDescriptors(clazz);
			for (PropertyDescriptor pd : pds) {
				String fieldName = pd.getName();
				if (map.containsKey(fieldName)) {
					Object mapValue = map.get(fieldName);
					Class beanType = pd.getPropertyType();
					Object beanValue = mapValue;


					if (beanType.isEnum()) {
						if (mapValue != null) {
							if (mapValue instanceof String) {
								if (String.valueOf(mapValue).matches("\\d+")) {// 数字型
									mapValue = Integer.parseInt(String.valueOf(mapValue));
									int intValue = (Integer) mapValue;

									Method method = beanType.getMethod("values");
									Object[] enumValues = (Object[]) method.invoke(beanType);
									if (intValue >= 0 && intValue < enumValues.length) {
										beanValue = enumValues[intValue];
									} else {
										continue;
									}
								} else {// 字符串标识的枚举值
									try {
										beanValue =
												Enum.valueOf(beanType, String.valueOf(mapValue));
									} catch (IllegalArgumentException e) {// 是一个错误的值
										continue;
									}
								}

							} else if (mapValue instanceof Integer) {// 整型
								int intValue = (Integer) mapValue;
								Method method = beanType.getMethod("values");
								Object[] enumValues = (Object[]) method.invoke(beanType);
								if (intValue >= 0 && intValue < enumValues.length) {
									beanValue = enumValues[intValue];
								} else {// 超过了枚举的int值范围
									continue;
								}
							}
						}
					} else if (beanType.equals(java.util.Date.class)) {
						if (mapValue != null) {
							if (mapValue instanceof String) {
								try {
									DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									beanValue = format.parse(String.valueOf(mapValue));
								} catch (ParseException e) {
									LOGGER.error("BeanHelper buildBean string 转 Date 出错!");
									continue;
								}

							}
						}
					}

					BEANUTILSBEAN.copyProperty(bean, fieldName, beanValue);

				}

			}
			return bean;
		} catch (Throwable e) {
			LOGGER.error("BeanHelper 根据map创建bean出错:", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 给对象属性赋值
	 * 
	 * @param bean
	 * @param name 属性名
	 * @param value 属性值
	 */
	public static void setProperty(Object bean, String name, Object value) {
		try {
			BEANUTILSBEAN.setProperty(bean, name, value);
		} catch (Throwable e) {
			LOGGER.error("BeanHelper 给对象属性赋值出错:", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 获取对象属性值
	 * 
	 * @param bean
	 * @param name
	 * @return
	 */
	public static Object getProperty(Object bean, String name) {
		try {
			return BEANUTILSBEAN.getPropertyUtils().getSimpleProperty(bean, name);
		} catch (Throwable e) {
			LOGGER.error("BeanHelper 获取对象属性值出错:", e);
			throw new RuntimeException(e);
		}
	}
}
