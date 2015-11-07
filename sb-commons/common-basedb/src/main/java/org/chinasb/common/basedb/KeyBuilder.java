package org.chinasb.common.basedb;

import org.springframework.util.StringUtils;

/**
 * 键值生成器
 * @author zhujuan
 */
public class KeyBuilder {
    /**
     * 生成索引键值
     * @param clazz 索引数据类对象
     * @param indexName 索引名称
     * @param indexValues 索引值
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