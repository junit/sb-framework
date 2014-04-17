package org.chinasb.common.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 对象锁
 * @author zhujuan
 */
public class ObjectLock extends ReentrantLock implements Comparable<ObjectLock> {
    private static final long serialVersionUID = 8895584738437574058L;
    private static final Class<Entity> IENTITY_CLASS = Entity.class;
    private final Class clz;
    private final Comparable value;
    private final boolean entity;

    /**
     * 构造一个对象锁
     * @param object
     */
    public ObjectLock(Object object) {
        this(object, false);
    }

    /**
     * 构造一个对象锁
     * @param object
     * @param fair
     */
    public ObjectLock(Object object, boolean fair) {
        super(fair);
        this.clz = object.getClass();
        if (object instanceof Entity)
            this.value = ((Entity) object).getIdentity();
        else {
            this.value = new Integer(System.identityHashCode(object));
        }
        if (IENTITY_CLASS.isAssignableFrom(this.clz)) {
            this.entity = true;
        } else {
            this.entity = false;
        }
    }

    /**
     * tie-breaking
     * @param other
     * @return
     */
    public boolean isTie(ObjectLock other) {
        if (this.clz != other.clz) {
            return false;
        }
        return (this.value.compareTo(other.value) == 0);
    }

    public Class getClz() {
        return this.clz;
    }

    public Comparable getValue() {
        return this.value;
    }

    public boolean isEntity() {
        return this.entity;
    }

    @Override
    public int compareTo(ObjectLock o) {
        if ((isEntity()) && (!(o.isEntity()))) return 1;
        if ((!(isEntity())) && (o.isEntity())) {
            return -1;
        }

        if (this.clz != o.clz) {
            if (this.clz.hashCode() < o.clz.hashCode()) return -1;
            if (this.clz.hashCode() > o.clz.hashCode()) {
                return 1;
            }
            return this.clz.getName().compareTo(o.clz.getName());
        }

        return this.value.compareTo(o.value);
    }
}
