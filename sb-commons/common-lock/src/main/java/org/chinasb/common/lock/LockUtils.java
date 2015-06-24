package org.chinasb.common.lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;

/**
 * 锁工具类
 * @author zhujuan
 */
public class LockUtils {
    /** 对象锁持有者，用于避免重复的锁创建 */
    private final static ObjectLockHolder holder = new ObjectLockHolder();

    /**
     * 获取对象实例的锁链
     * @param objects
     * @return
     */
    public static ChainLock getLock(Object... objects) {
        List<? extends Lock> locks = loadLocks(objects);
        return new ChainLock(locks);
    }

    /**
     * 加载对象实例的对象锁，并对所有对象锁处理排序
     * @param objects
     * @return
     */
    private static List<? extends Lock> loadLocks(Object... objects) {
        List<ObjectLock> locks = new ArrayList<ObjectLock>(objects.length);
        for (Object obj : objects) {
            ObjectLock lock = holder.getLock(obj);
            if ((lock != null) && (!(locks.contains(lock)))) {
                locks.add(lock);
            }
        }
        Collections.sort(locks);
        
        TreeSet<Integer> idx = new TreeSet<Integer>();
        Integer start = null;
        for (int i = 0; i < locks.size(); ++i) {
            if (start == null) {
                start = i;
            } else {
                ObjectLock lock1 = locks.get(start.intValue());
                ObjectLock lock2 = locks.get(i);
                if (lock1.isTie(lock2)) {
                    idx.add(start);
                } else {
                    start = i;
                }
            }
        }
        if (idx.isEmpty()) {
            return locks;
        }
        
        List<Lock> newLocks = new ArrayList<Lock>(locks.size() + idx.size());
        newLocks.addAll(locks);
        Iterator<Integer> it = idx.descendingIterator();
        while (it.hasNext()) {
            Integer i = (Integer) it.next();
            ObjectLock lock = locks.get(i.intValue());
            Lock tieLock = holder.getTieLock(lock.getClz());
            newLocks.add(i.intValue(), tieLock);
        }
        return newLocks;
    }
}
