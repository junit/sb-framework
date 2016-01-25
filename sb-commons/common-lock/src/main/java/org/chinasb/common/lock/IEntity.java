package org.chinasb.common.lock;

/**
 * 实体标识接口
 * 
 * @author zhujuan
 */
@SuppressWarnings("rawtypes")
public interface IEntity<T extends Comparable> {
    /**
     * 获取实体标识
     * 
     * @return
     */
    public T getIdentity();
}
