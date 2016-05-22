package org.chinasb.common.basedb;

/**
 * 取值器接口
 * 
 * @author zhujuan
 */
public interface Getter {
    /**
     * 获取对象值
     * 
     * @param value 静态资源实例
     * @return {@link Object} 对象相关值
     */
    Object getValue(Object value);
}
