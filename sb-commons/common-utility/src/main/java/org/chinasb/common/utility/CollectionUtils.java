package org.chinasb.common.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 * @author zhujuan
 */
public abstract class CollectionUtils {
    
    /**
     * 拷贝数据集合
     * @param source 源数据
     * @param start 开始索引
     * @param count 拷贝数量
     * @return
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
     * 拷贝数据集合
     * @param source 源数据
     * @param startIndex 开始索引
     * @param stopIndex 结束索引
     * @return
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
     * 添加数据到集合指定位置
     * @param list
     * @param idx
     * @param element
     */
    public static <T> void addNotExist(List<T> list, int idx, T element) {
        if (!list.contains(element)) {
            list.add(idx, element);
        }
    }
}
