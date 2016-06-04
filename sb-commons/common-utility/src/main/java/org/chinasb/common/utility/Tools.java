package org.chinasb.common.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 通用工具类
 */
public class Tools {

	/** 随机数对象 */
	private static ThreadLocal<Random> RANDOM_THREAD_LOCAL = new ThreadLocal<Random>();

	private static Random getRandom() {
		Random random = RANDOM_THREAD_LOCAL.get();
		if (random == null) {
			RANDOM_THREAD_LOCAL.set(new Random());
			random = RANDOM_THREAD_LOCAL.get();
		}
		return random;
	}

	/**
	 * 取得随机数
	 * 
	 * @param maxValue 随机数最大值
	 * @return {@link Integer} 随机的值
	 */
	public static int getRandomInt(int maxValue) {
		int value = 0;
		if (maxValue > 0) {
			value = getRandom().nextInt(maxValue);
		}
		return value;
	}

	/**
	 * 取得区间随机数
	 * 
	 * @param minValue 最小保底值
	 * @param maxValue 随机数最大值
	 * @return {@link Integer} 随机的值
	 */
	public static int getRandomInt(int minValue, int maxValue) {
		int value = 0;
		int min = Math.min(minValue, maxValue);
		int max = Math.max(minValue, maxValue);
		int between = Math.max(0, max - min);
		if (between > 0) {
			value = getRandomInt(between) + 1;
		}
		return min + value;
	}

	/**
	 * 需要拆分的字符串
	 * @param delimiterString
	 * @return
	 */
	public static List<String[]> delimiterString2Array(String delimiterString) {
		if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
			return null;
		}
		String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
		if ((ss != null) && (ss.length > 0)) {
			List<String[]> list = new ArrayList<String[]>();
			for (int i = 0; i < ss.length; i++) {
				list.add(ss[i].split(Splitable.ATTRIBUTE_SPLIT));
			}
			return list;
		}
		return null;
	}

	/**
	 * 需要拆分的字符串
	 * @param delimiterString
	 * @return
	 */
	public static Map<String, String[]> delimiterString2Map(String delimiterString) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
			return map;
		}
		String[] ss = delimiterString.trim().split(Splitable.ELEMENT_SPLIT);
		if ((ss != null) && (ss.length > 0)) {
			for (int i = 0; i < ss.length; i++) {
				String[] str = ss[i].split(Splitable.ATTRIBUTE_SPLIT);
				if (str.length > 0) {
					map.put(str[0], str);
				}
			}
			return map;
		}
		return map;
	}

	/**
	 * 把子项数组转换成以|_分割的字符串
	 * @param collection
	 * @return
	 */
	public static String delimiterCollection2String(Collection<String[]> collection) {
		if ((collection == null) || (collection.isEmpty())) {
			return "";
		}
		StringBuffer subContent = new StringBuffer();
		for (String[] strings : collection) {
			if ((strings != null) && (strings.length != 0)) {
				for (int i = 0; i < strings.length; i++) {
					if (i == strings.length - 1) {
						subContent.append(strings[i]).append(Splitable.ELEMENT_DELIMITER);
					} else {
						subContent.append(strings[i]).append(Splitable.ATTRIBUTE_SPLIT);
					}
				}
			}
		}
		return subContent.toString();
	}

	/**
	 * 把子项数组转换成以|_分割的字符串
	 * @param subArray
	 * @return
	 */
	public static String array2DelimiterString(String[] subArray) {
		if ((subArray == null) || (subArray.length == 0)) {
			return "";
		}
		StringBuffer subContent = new StringBuffer();
		for (int i = 0; i < subArray.length; i++) {
			subContent.append(subArray[i]).append(Splitable.ATTRIBUTE_SPLIT);
		}
		String tmp = subContent.toString().substring(0, subContent.lastIndexOf(Splitable.ATTRIBUTE_SPLIT));

		return tmp + Splitable.ELEMENT_DELIMITER;
	}

	/**
	 * 把子项数组转换成以|_分割的字符串
	 * @param subArrayList
	 * @return
	 */
	public static String listArray2DelimiterString(List<String[]> subArrayList) {
		if ((subArrayList == null) || (subArrayList.isEmpty())) {
			return "";
		}
		StringBuffer subContent = new StringBuffer();
		for (String[] strings : subArrayList) {
			if ((strings != null) && (strings.length != 0)) {
				for (int i = 0; i < strings.length; i++) {
					if (i == strings.length - 1) {
						subContent.append(strings[i]).append(Splitable.ELEMENT_DELIMITER);
					} else {
						subContent.append(strings[i]).append(Splitable.ATTRIBUTE_SPLIT);
					}
				}
			}
		}
		return subContent.toString();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param value 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return {@link Double} 四舍五入后的结果
	 */
	public static double round(double value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal devideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(devideBigDecimal, scale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	/**
	 * 四舍五入向下取整
	 * 
	 * @param value 需要向下取整的值
	 * @param scale 小数点后保留的小数位
	 * @return {@link Double} 向下取整后的值
	 */
	public static double roundDown(double value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal devideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(devideBigDecimal, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**
	 * 四舍五入向上取整
	 * 
	 * @param value 需要向上取整的值
	 * @param scale 小数点后保留的小数位
	 * @return {@link Double} 向上取整后的值
	 */
	public static double roundUp(double value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal divideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(divideBigDecimal, scale, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * 相除向上取整
	 * 
	 * @param value1 被除数
	 * @param value2 除数
	 * @param scale 保留的位
	 * @return {@link Double} 相除后的值
	 */
	public static double divideRoundUp(double value1, double value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal bigDecimal1 = new BigDecimal(value1);
		BigDecimal bigDecimal2 = new BigDecimal(value2);
		return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * 相除向上取整
	 * 
	 * @param value1 被除数
	 * @param value2 除数
	 * @param scale 保留的位
	 * @return {@link Double} 相除后的值
	 */
	public static double divideRoundDown(double value1, double value2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal bigDecimal1 = new BigDecimal(value1);
		BigDecimal bigDecimal2 = new BigDecimal(value2);
		return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}

}
