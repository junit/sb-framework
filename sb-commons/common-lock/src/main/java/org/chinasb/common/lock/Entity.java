package org.chinasb.common.lock;

/**
 * 实体对象
 * @author zhujuan
 * @param <T>
 */
public interface Entity<T extends Comparable<T>> {
    /**
     * 获得唯一标识
     * @return
     */
    public T getIdentity();
}
