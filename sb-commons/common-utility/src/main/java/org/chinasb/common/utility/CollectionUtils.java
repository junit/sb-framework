package org.chinasb.common.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class CollectionUtils {
    
    public static <T> List<T> subListCopy(List<T> source, int start, int count) {
        if ((source == null) || (source.size() == 0)) {
            return new ArrayList<T>(0);
        }

        int fromIndex = (start <= 0) ? 0 : start;
        if (start > source.size()) {
            fromIndex = source.size();
        }

        count = (count <= 0) ? 0 : count;
        int endIndex = fromIndex + count;
        if (endIndex > source.size()) {
            endIndex = source.size();
        }
        return new ArrayList<T>(source.subList(fromIndex, endIndex));
    }

    public static <T> List<T> subList(List<T> source, int startIndex, int stopIndex) {
        if ((source == null) || (source.size() == 0)) {
            return new ArrayList<T>(0);
        }

        int fromIndex = (startIndex <= 0) ? 0 : startIndex;
        if (startIndex > source.size()) {
            fromIndex = source.size();
        }

        stopIndex = (stopIndex <= 0) ? 0 : stopIndex;
        stopIndex = (stopIndex <= startIndex) ? startIndex : stopIndex;
        if (stopIndex > source.size()) {
            stopIndex = source.size();
        }
        return new ArrayList<T>(source.subList(fromIndex, stopIndex));
    }

    public static <T> void addNotExist(List<T> list, int idx, T element) {
        if (!(list.contains(element))) list.add(idx, element);
    }
}
