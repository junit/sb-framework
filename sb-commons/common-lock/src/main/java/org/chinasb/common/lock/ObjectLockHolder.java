package org.chinasb.common.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 对象锁持有者
 * @author zhujuan
 */
@SuppressWarnings("rawtypes")
public class ObjectLockHolder {
    /**
     * 所有类的对象锁持有者缓存
     */
    private final LoadingCache<Class, Holder> HOLDERS = CacheBuilder.newBuilder().maximumSize(1000)
            .build(new CacheLoader<Class, Holder>() {
                @Override
                public Holder load(Class clz) {
                    return new Holder(clz);
                }
            });

    /**
     * 类的对象锁持有者
     * @author zhujuan
     *
     */
    public class Holder {
        /**
         * 对象锁持有者的类型
         */
        @SuppressWarnings("unused")
        private final Class clz;
        /**
         * 加时锁，用于当对象的hash值(或实现自Entity接口的getIdentity())一样时保证锁的获取顺序
         */
        private final Lock tieLock = new ReentrantLock();
        /**
         * 实例的对象锁缓存
         */
        private final LoadingCache<Object, ObjectLock> locks = CacheBuilder.newBuilder().weakKeys()
                .build(new CacheLoader<Object, ObjectLock>() {
                    @Override
                    public ObjectLock load(Object object) {
                        return new ObjectLock(object);
                    }
                });
        
        public Holder(Class clz) {
            this.clz = clz;
        }
        
        /**
         * 获取对象锁
         * @param object
         * @return
         */
        public ObjectLock getLock(Object object) {
            return locks.getUnchecked(object);
        }

        /**
         * 获取加时锁
         * @return
         */
        public Lock getTieLock() {
            return tieLock;
        }

        /**
         * 获取锁的数量
         * @return
         */
        public long count() {
            return locks.size();
        }
    }
    
    /**
     * 获取类的对象锁持有者
     * @param clz
     * @return
     */
    private Holder getHolder(Class clz) {
        return HOLDERS.getUnchecked(clz);
    }
    
    /**
     * 获取对象实例的对象锁
     * @param object
     * @return
     */
    public ObjectLock getLock(Object object) {
        if (object == null) return null;
        return getHolder(object.getClass()).getLock(object);
    }
    
    /**
     * 获取类的对象锁持有者的加时锁(tie-breaker)
     * @param clz
     * @return
     */
    public Lock getTieLock(Class clz) {
        return getHolder(clz).getTieLock();
    }

    /**
     * 获取类的对象锁持有者的锁的数量
     * @param clz
     * @return
     */
    public long count(Class clz) {
        Holder holder = HOLDERS.getIfPresent(clz);
        if (holder != null) {
            return holder.count();
        }
        return 0;
    }
}
