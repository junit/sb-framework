package org.chinasb.common.basedb;

/**
 * 取值器
 * @author zhujuan
 */
public interface Getter {
    /**
     * 取值
     * @param paramObject
     * @return
     */
    public Object getValue(Object paramObject);
}
