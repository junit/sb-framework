package org.chinasb.common.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 对象锁缓存
 * @author zhujuan
 */
public class ObjectLockHolder {
    private final LoadingCache<Class, Holder> HOLDERS = CacheBuilder.newBuilder().maximumSize(1000)
            .build(new CacheLoader<Class, Holder>() {
                @Override
                public Holder load(Class clz) {
                    return new Holder(clz);
                }
            });

    public class Holder {
        private final Class clz;
        /**
         * 用于当对象的hash值一样时使用（或实现自Entity接口的getIdentity()一样时使用）
         */
        private final Lock tieLock = new ReentrantLock();

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
        
        public ObjectLock getLock(Object object) {
            return locks.getUnchecked(object);
        }

        public Lock getTieLock() {
            return tieLock;
        }

        public long count() {
            return locks.size();
        }
    }
    
    private Holder getHolder(Class clz) {
        return HOLDERS.getUnchecked(clz);
    }
    
    /**
     * 获得一个对象锁
     * @param object
     * @return
     */
    public ObjectLock getLock(Object object) {
        return getHolder(object.getClass()).getLock(object);
    }
    
    /**
     * 获得该类的加时锁(tie-breaker)
     * @param clz
     * @return
     */
    public Lock getTieLock(Class clz) {
        return getHolder(clz).getTieLock();
    }

    /**
     * 获得该类的对象锁缓存数量
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
