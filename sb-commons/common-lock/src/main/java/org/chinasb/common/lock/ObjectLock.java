package org.chinasb.common.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 对象锁<br/>
 * <pre>
 * 排序顺序说明:
 * 1.非实体在前，实体{@link IEntity}在后
 * 2.不同类型的锁对象，按类型{@link Class}的hashCode值的大小进行排序
 * 3.不同类型的锁对象，当不幸遇到hashCode值相同的情况，用完整类名做字符串排序
 * 4.类型相同时，使用<code>{@link ObjectLock#value}</code>进行排序
 * 5.<code>{@link ObjectLock#value}</code>对于非实体而言，为<code>System.identityHashCode(instance)</code>
 * 6.<code>{@link ObjectLock#value}</code>对于实体而言，为{@link IEntity#getIdentity()}
 * </pre>
 * @author zhujuan
 */
@SuppressWarnings("rawtypes")
public class ObjectLock extends ReentrantLock implements Comparable<ObjectLock> {
    private static final long serialVersionUID = 265250785740061308L;
    private static final Class<IEntity> IENTITY_CLASS = IEntity.class;
    /**
     * 对象锁的类型
     */
    private final Class clz;
    /**
     * 对象锁用于排序的元素
     */
    private final Comparable value;

    /**
     * 构造一个对象实例的对象锁
     * @param object
     */
    public ObjectLock(Object object) {
        this(object, true);
    }

    /**
     * 构造一个对象实例的对象锁
     * @param object
     * @param fair {@link ReentrantLock#isFair()}
     */
    public ObjectLock(Object object, boolean fair) {
        super(fair);
        this.clz = object.getClass();
        if (object instanceof IEntity)
            this.value = ((IEntity) object).getIdentity();
        else {
            this.value = new Integer(System.identityHashCode(object));
        }
    }

    /**
     * 加时锁检查（检查当前对象锁与指定对象锁是否可以保证获取顺序）
     * @param other
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isTie(ObjectLock other) {
        if (this.clz != other.clz) {
            return false;
        }
        if(this.value.compareTo(other.value) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取对象锁的类型
     * @return
     */
    public Class getClz() {
        return clz;
    }

    /**
     * 获取对象锁的排序元素
     * @return
     */
    public Comparable getValue() {
        return value;
    }

    /**
     * 检查当前对象锁是否是实体的对象锁
     * @return
     */
    public boolean isEntity() {
        return IENTITY_CLASS.isAssignableFrom(clz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(ObjectLock o) {
        if (this.isEntity() && !o.isEntity()) {
            return 1;
        }
        if (!this.isEntity() && o.isEntity()) {
            return -1;
        }
        if (this.clz != o.clz) {
            if (this.clz.hashCode() < o.clz.hashCode()) {
                return -1;
            }
            if (this.clz.hashCode() > o.clz.hashCode()) {
                return 1;
            }
            return this.clz.getName().compareTo(o.clz.getName());
        }
        return this.value.compareTo(o.value);
    }
}
