package org.chinasb.common.basedb;

import org.springframework.util.StringUtils;

/**
 * 键名生成器
 * @author zhujuan
 */
public class KeyBuilder {
    /**
     * 生成索引键名
     * @param clazz
     * @param indexName
     * @param indexValues
     * @return
     */
    public static String buildIndexKey(Class<?> clazz, String indexName, Object... indexValues) {
        StringBuilder builder = new StringBuilder();
        if (clazz != null) {
            builder.append(clazz.getName()).append("&");
        }
        if (indexName != null) {
            builder.append(indexName).append("#");
        }
        if ((indexValues != null) && (indexValues.length > 0)) {
            return StringUtils.arrayToDelimitedString(indexValues, "^");
        }
        return builder.toString();
    }
}