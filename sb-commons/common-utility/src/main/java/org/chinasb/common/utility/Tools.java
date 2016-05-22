package org.chinasb.common.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.base.Strings;

/**
 * 通用工具类
 */
public class Tools {

	/** 随机数对象 */
	private static ThreadLocal<Random> RANDOM_THREAD_LOCAL = new ThreadLocal<Random>();
	
	private static Random getRandom() {
		Random random = RANDOM_THREAD_LOCAL.get();
		if(random == null) {
			RANDOM_THREAD_LOCAL.set(new Random());
			random = RANDOM_THREAD_LOCAL.get();
		}
		return random;
	}
	/**
	 * 取得随机数
	 * 
	 * @param  maxValue 			随机数最大值
	 * @return {@link Integer}		随机的值
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
	 * @param  minValue				最小保底值
	 * @param  maxValue 			随机数最大值
	 * @return {@link Integer}		随机的值
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
	 * 
	 * @param  delimiter		需要拆分的字符串
	 * @return {@link List}		拆分出来的字符串
	 */
	public static List<String[]> delimiterString(String delimiter) {
		List<String[]> elementLists = new ArrayList<String[]>(0);
		delimiter = Strings.nullToEmpty(delimiter).trim(); 
		for (String element : delimiter.split(Splitable.ELEMENT_SPLIT)) {
			if(!Strings.isNullOrEmpty(element)) {
				elementLists.add(element.split(Splitable.ATTRIBUTE_SPLIT));
			}
		}
		return elementLists;
	}

	/**
	 * 需要拆分的字符串
	 * 
	 * @param  delimiter		需要拆分的字符串
	 * @return {@link List}		拆分出来的字符串
	 */
	public static String delimiterToString(Collection<String[]> collection) {
		if(collection == null || collection.isEmpty()) {
			return "";
		}
		
		StringBuffer buffer = new StringBuffer();
		for (String[] elements : collection) {
			buffer.append(delimiterToString(elements));
		}
		return buffer.toString();
	}
	
	/**
	 * 把子项数组转换成以|_分割的字符串
	 * 
	 * @param  elements			需要切换的数组
	 * @return {@link String}
	 */
	public static String delimiterToString(String[] elements) {
		if (elements == null || elements.length == 0) {
			return "";
		}

		int length = elements.length;
		StringBuffer buffer = new StringBuffer();
		for (int index = 0; index < length; index++) {
			if (index == length - 1) {
				buffer.append(elements[index]).append(Splitable.ELEMENT_DELIMITER);
			} else {
				buffer.append(elements[index]).append(Splitable.ATTRIBUTE_SPLIT);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param  value 			需要四舍五入的数字
	 * @param  scale 			小数点后保留几位
	 * @return {@link Double}	四舍五入后的结果
	 */
	public static double round(double value, int scale) {
		BigDecimal devideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(devideBigDecimal, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 四舍五入向下取整
	 * 
	 * @param  value			需要向下取整的值
	 * @param  scale			小数点后保留的小数位
	 * @return {@link Double}	向下取整后的值
	 */
	public static double roundDown(double value, int scale) {
		BigDecimal devideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(devideBigDecimal, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**
	 * 四舍五入向上取整
	 * 
	 * @param  value			需要向上取整的值
	 * @param  scale			小数点后保留的小数位
	 * @return {@link Double}	向上取整后的值
	 */
	public static double roundUp(double value, int scale) {
		BigDecimal divideBigDecimal = new BigDecimal("1");
		BigDecimal valueBigDecimal = new BigDecimal(Double.toString(value));
		return valueBigDecimal.divide(divideBigDecimal, scale, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * 相除向上取整
	 * 
	 * @param  value1			被除数
	 * @param  value2			除数
	 * @param  scale			保留的位
	 * @return {@link Double}	相除后的值
	 */
	public static double divideRoundUp(double value1, double value2, int scale) {
		BigDecimal bigDecimal1 = new BigDecimal(value1);
		BigDecimal bigDecimal2 = new BigDecimal(value2);
		return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * 相除向上取整
	 * 
	 * @param  value1			被除数
	 * @param  value2			除数
	 * @param  scale			保留的位
	 * @return {@link Double}	相除后的值
	 */
	public static double divideRoundDown(double value1, double value2, int scale) {
		BigDecimal bigDecimal1 = new BigDecimal(value1);
		BigDecimal bigDecimal2 = new BigDecimal(value2);
		return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}
	
}
