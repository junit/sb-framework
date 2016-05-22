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
 * 
 * @author zhujuan
 */
public class NumberUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(NumberUtils.class);
	
	/**
	 * 构建数值类型
	 * 
	 * @param resultType
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(Class<T> resultType, Number value) {
		if (resultType == null) {
			String msg = value.getClass().getSimpleName() + " -> NULL";
			throw new NullPointerException(msg);
		}

		if (resultType == int.class || resultType == Integer.class) {
			return (T) Integer.valueOf(value.intValue());
		} else if (resultType == double.class || resultType == Double.class) {
			return (T) Double.valueOf(value.doubleValue());
		} else if (resultType == boolean.class || resultType == Boolean.class) {
			return (T) Boolean.valueOf(value.doubleValue() > 0D);
		} else if (resultType == byte.class || resultType == Byte.class) {
			return (T) Byte.valueOf(value.byteValue());
		} else if (resultType == long.class || resultType == Long.class) {
			return (T) Long.valueOf(value.longValue());
		} else if (resultType == short.class || resultType == Short.class) {
			return (T) Short.valueOf(value.shortValue());
		} else if (resultType == float.class || resultType == Float.class) {
			return (T) Float.valueOf(value.floatValue());
		} else if (resultType == Number.class) {
			return (T) value;
		} else {
			String msg = value.getClass().getSimpleName() + " -> " + resultType.getSimpleName();
			throw new IllegalArgumentException(new ClassCastException(msg));
		}
	}
	
	/**
	 * 将字符串按指定的分割符分割并返回指定类型的数组
	 * 
	 * @param  element				字符串
	 * @param  separator			分割符
	 * @param  clazz				要转换类型的类,如:Integer.class,Long.class
	 * @return T[] 					返回class类型的数组
	 */
	public static <T> T[] convertArray(String element, String separator, Class<T> clazz) {
		if (element != null && element.trim().length() > 0) {
			String[] vals = element.split(separator);
			try {
				return covertArray(clazz, vals, 0, vals.length);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	static <T> T[] covertArray(Class<T> clazz, String[] vals, int from, int to)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		int start, end;
		if (from > to) {
			start = to;
			end = from;
		} else {
			start = from;
			end = to;
		}

		T[] result = (T[]) Array.newInstance(clazz, to - from);
		Method valueOfMethod = clazz.getMethod("valueOf", String.class);
		boolean accessible;
		accessible = valueOfMethod.isAccessible();
		valueOfMethod.setAccessible(true);
		if (valueOfMethod != null) {
			for (int i = start; i < end; i++) {
				T val = (T) valueOfMethod.invoke(clazz, vals[i]);
				result[i - start] = val;
			}
		}
		valueOfMethod.setAccessible(accessible);
		return result;
	}

	/**
	 * 把以|_分割的字符串分解成数组
	 * 
	 * @param <T>
	 * @param str
	 *            字符串
	 * @param clazz
	 *            T的类
	 * @return
	 */
	public static <T> List<T[]> delimiterString2Array(String str, Class<T> clazz) {
		try{
			if (str != null && str.trim().length() > 0) {
				String[] ss = str.trim().split(Splitable.ELEMENT_SPLIT);
				if (ss != null && ss.length > 0) {
					List<T[]> list = new ArrayList<T[]>();
					for (int i = 0; i < ss.length; i++) {
						list.add(convertArray(ss[i], Splitable.ATTRIBUTE_SPLIT,	clazz));
					}
					return list;
				}
			}
		}catch (Exception e) {
			LOGGER.error("转换字符串时出错：{}",e);
		}
		return null;
	}

	/**
	 * 把以|_分割的字符串分解成HashMap,键位为字符串的0下标数值
	 * 
	 * @param delimiterString
	 * @param clazz1
	 *            返回map的key的类
	 * @param clazz2
	 *            返回map的value的类
	 * @return Map<K,V[]>
	 */
	public static <K, V> Map<K, V[]> delimiterString2Map(String delimiterString, Class<K> clazz1, Class<V> clazz2) {
		Map<K, V[]> map = new HashMap<K, V[]>();
		if (delimiterString == null || delimiterString.trim().length() == 0) {
			return map;
		}

		String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
		if (ss != null && ss.length > 0) {

			for (int i = 0; i < ss.length; i++) {
				String[] vals = ss[i].split(Splitable.ATTRIBUTE_SPLIT);
				if (vals.length < 1) {
					continue;
				}

				try {
					K k = covertArray(clazz1, vals, 0, 1)[0];
					V[] v = covertArray(clazz2, vals, 1, vals.length);
					map.put(k, v);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		return map;
	}
	
	/**
	 * 把以|_分割的字符串分解成HashMap,键位为字符串的0下标数值
	 * 
	 * @param delimiterString
	 * @param clazz1
	 *            返回map的key的类
	 * @param clazz2
	 *            返回map的value的类
	 * @return Map<K,V>
	 */
	public static <K, V> Map<K, V> delimiterString2Map2(String delimiterString, Class<K> clazz1, Class<V> clazz2) {
		Map<K, V> map = new HashMap<K, V>();
		if (delimiterString == null || delimiterString.trim().length() == 0) {
			return map;
		}
		
		String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
		if (ss != null && ss.length > 0) {
			
			for (int i = 0; i < ss.length; i++) {
				if(ss[i] == null){
					continue;
				}
				String[] vals = ss[i].split(Splitable.ATTRIBUTE_SPLIT);
				if (vals.length < 2) {
					continue;
				}
				try {
					K k = valueOf(vals[0], clazz1 );
					V v = valueOf(vals[1], clazz2 );
					map.put(k, v);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		return map;
	}
	/**
	 * 把以|_分割的字符串分解成List,键位为字符串的0下标数值
	 * 
	 * @param delimiterString
	 * @param clazz
	 *            返回value类
	 * @return Map<K,V>
	 */
	public static <T> List<T> delimiterString2List2(String delimiterString, Class<T> clazz) {
		if (delimiterString == null || delimiterString.trim().length() == 0) {
			return Collections.emptyList();
		}
		
		List<T> list = new ArrayList<T>();
		String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
		if (ss != null && ss.length > 0) {
			
			for (int i = 0; i < ss.length; i++) {
				try {
					T t = valueOf(ss[i], clazz );
					list.add(t);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}
		return list;
	}
	
	/**
	 * 构建指定类型的对象
	 * 
	 * @param  str			需要构建的字符串
	 * @param  clazz		需要构建的类型
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(String str, Class<T> clazz){
		try{
			Method valueOfMethod = clazz.getMethod("valueOf", String.class);
			if (valueOfMethod != null) {
				T val = (T) valueOfMethod.invoke(clazz, str);
				return val;
			}
		}catch (Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}
}
