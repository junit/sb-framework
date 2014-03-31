package org.chinasb.common.lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;

public class LockUtils {
    private final static ObjectLockHolder holder = new ObjectLockHolder();

    public static ChainLock getLock(Object... objects) {
        List<? extends Lock> locks = loadLocks(objects);
        return new ChainLock(locks);
    }

    public static List<? extends Lock> loadLocks(Object... objects) {
        List<ObjectLock> locks = new ArrayList<ObjectLock>();
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
                start = Integer.valueOf(i);
            } else {
                ObjectLock lock1 = (ObjectLock) locks.get(start.intValue());
                ObjectLock lock2 = (ObjectLock) locks.get(i);
                if (lock1.isTie(lock2)) {
                    idx.add(start);
                } else {
                    start = Integer.valueOf(i);
                }
            }
        }
        if (idx.isEmpty()) {
            return locks;
        }

        List<Lock> newsLocks = new ArrayList<Lock>(locks.size() + idx.size());
        newsLocks.addAll(locks);
        Iterator<Integer> it = idx.descendingIterator();
        while (it.hasNext()) {
            Integer i = (Integer) it.next();
            ObjectLock lock = (ObjectLock) locks.get(i.intValue());
            Lock tieLock = holder.getTieLock(lock.getClz());
            newsLocks.add(i.intValue(), tieLock);
        }
        return newsLocks;
    }
}
