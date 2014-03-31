package org.chinasb.common.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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
    
    public ObjectLock getLock(Object object) {
        return getHolder(object.getClass()).getLock(object);
    }
    
    public Lock getTieLock(Class clz) {
        return getHolder(clz).getTieLock();
    }

    public long count(Class<?> clz) {
        Holder holder = HOLDERS.getIfPresent(clz);
        if (holder != null) {
            return holder.count();
        }
        return 0;
    }
}
