package org.chinasb.common.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类
 * 
 * @author zhujuan
 */
public abstract class CollectionUtils {

	/**
	 * 获取列表的子集的拷贝
	 * 
	 * @param <T>
	 * @param source 			源列表
	 * @param start 			开始位置
	 * @param count 			获取记录数量
	 * @return {@link List}		返回结果是另一个 ArrayList 实例
	 */
    public static <T> List<T> subListCopy(List<T> source, int start, int count) {
        if ((source == null) || (source.size() == 0)) {
            return new ArrayList<T>(0);
        }
        int fromIndex = start <= 0 ? 0 : start;
        if (start > source.size()) {
            fromIndex = source.size();
        }
        count = count <= 0 ? 0 : count;
        int endIndex = fromIndex + count;
        if (endIndex > source.size()) {
            endIndex = source.size();
        }
        return new ArrayList<T>(source.subList(fromIndex, endIndex));
    }

	/**
	 * 获取列表的子集的拷贝
	 * 
	 * @param <T>
	 * @param source 			源列表
	 * @param start 			开始位置
	 * @param stopIndex 		结束位置
	 * @return {@link List}		返回结果是另一个 ArrayList 实例
	 */
    public static <T> List<T> subList(List<T> source, int startIndex, int stopIndex) {
        if ((source == null) || (source.size() == 0)) {
            return new ArrayList<T>(0);
        }
        int fromIndex = startIndex <= 0 ? 0 : startIndex;
        if (startIndex > source.size()) {
            fromIndex = source.size();
        }
        stopIndex = stopIndex <= 0 ? 0 : stopIndex;
        stopIndex = stopIndex <= startIndex ? startIndex : stopIndex;
        if (stopIndex > source.size()) {
            stopIndex = source.size();
        }
        return new ArrayList<T>(source.subList(fromIndex, stopIndex));
    }

	/**
	 * 内存列表分页
	 * @param <T>
	 * @param list
	 * @param startIndex
	 * @param fetchCount
	 * @return
	 */
	public static <T> List<T> pageResult(List<T> list, int startIndex, int fetchCount){
		if(list != null && list.size() > 0){
			if(startIndex >= list.size()){
				return null;
			}
			startIndex = startIndex < 0?0:startIndex;
			if(fetchCount <= 0){
				return list.subList(startIndex, list.size());
			}
			int toIndex = Math.min(startIndex + fetchCount, list.size());
			return list.subList(startIndex, toIndex);
		}
		
		return null;
	}
	
	/**
	 * 当列表元素不存在时，进行添加
	 * 
	 * @param list 		列表
	 * @param idx 		插入位置
	 * @param element 	插入的元素
	 */
	public static <T> void addNotExist(List<T> list, int idx, T element) {
		if (list != null && !list.contains(element)) {
			list.add(idx, element);
		}
	}

	/**
	 * 当列表元素不存在时，进行添加
	 * 
	 * @param list 		列表
	 * @param element 	插入的元素
	 */
	public static <T> void addNotExist(List<T> list, T element) {
		if (list != null && !list.contains(element)) {
			list.add(element);
		}
	}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> sort(List<T> list) {
        if (list != null && list.size() > 0) {
            Collections.sort((List) list);
        }
        return list;
    }

    private static final Comparator<String> SIMPLE_NAME_COMPARATOR = new Comparator<String>() {
        public int compare(String s1, String s2) {
            if (s1 == null && s2 == null) {
                return 0;
            }
            if (s1 == null) {
                return -1;
            }
            if (s2 == null) {
                return 1;
            }
            int i1 = s1.lastIndexOf('.');
            if (i1 >= 0) {
                s1 = s1.substring(i1 + 1);
            }
            int i2 = s2.lastIndexOf('.');
            if (i2 >= 0) {
                s2 = s2.substring(i2 + 1);
            }
            return s1.compareToIgnoreCase(s2);
        }
    };

    public static List<String> sortSimpleName(List<String> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, SIMPLE_NAME_COMPARATOR);
        }
        return list;
    }

    public static Map<String, Map<String, String>> splitAll(Map<String, List<String>> list,
            String separator) {
        if (list == null) {
            return null;
        }
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        for (Map.Entry<String, List<String>> entry : list.entrySet()) {
            result.put(entry.getKey(), split(entry.getValue(), separator));
        }
        return result;
    }

    public static Map<String, List<String>> joinAll(Map<String, Map<String, String>> map,
            String separator) {
        if (map == null) {
            return null;
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            result.put(entry.getKey(), join(entry.getValue(), separator));
        }
        return result;
    }

    public static Map<String, String> split(List<String> list, String separator) {
        if (list == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        if (list == null || list.size() == 0) {
            return map;
        }
        for (String item : list) {
            int index = item.indexOf(separator);
            if (index == -1) {
                map.put(item, "");
            } else {
                map.put(item.substring(0, index), item.substring(index + 1));
            }
        }
        return map;
    }

    public static List<String> join(Map<String, String> map, String separator) {
        if (map == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        if (map == null || map.size() == 0) {
            return list;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.length() == 0) {
                list.add(key);
            } else {
                list.add(key + separator + value);
            }
        }
        return list;
    }

    public static String join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String ele : list) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(ele);
        }
        return sb.toString();
    }

    public static boolean mapEquals(Map<?, ?> map1, Map<?, ?> map2) {
        if (map1 == null && map2 == null) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<?, ?> entry : map1.entrySet()) {
            Object key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            if (!objectEquals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static Map<String, String> toStringMap(String... pairs) {
        Map<String, String> parameters = new HashMap<String, String>();
        if (pairs.length > 0) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("pairs must be even.");
            }
            for (int i = 0; i < pairs.length; i = i + 2) {
                parameters.put(pairs[i], pairs[i + 1]);
            }
        }
        return parameters;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object... pairs) {
        Map<K, V> ret = new HashMap<K, V>();
        if (pairs == null || pairs.length == 0)
            return ret;

        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Map pairs can not be odd number.");
        }
        int len = pairs.length / 2;
        for (int i = 0; i < len; i++) {
            ret.put((K) pairs[2 * i], (V) pairs[2 * i + 1]);
        }
        return ret;
    }

	/**
	 * 判断集合是否为空
	 * 
	 * @param  collections		集合信息
	 * @return {@link Boolean}	true-集合为空, false-集合不为空
	 */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

	/**
	 * 判断集合是否不为空
	 * 
	 * @param  collections		集合信息
	 * @return {@link Boolean}	true-集合为空, false-集合不为空
	 */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    private CollectionUtils() {}

}
