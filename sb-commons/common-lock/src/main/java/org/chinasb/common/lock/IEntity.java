package org.chinasb.common.lock;

public interface IEntity<T extends Comparable> {
    public T getIdentity();
}
