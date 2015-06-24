package org.chinasb.common.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 锁链
 * @author zhujuan
 */
public class ChainLock {
    private final List<? extends Lock> locks;
    private static final int TIME_OUT = 5;
    private static final int TIMES = 3;

    /**
     * 初始化锁链
     * @param locks
     */
    public ChainLock(List<? extends Lock> locks) {
        if ((locks == null) || (locks.isEmpty())) {
            throw new IllegalArgumentException("构建锁链的锁数量不能为0");
        }
        this.locks = locks;
    }

    /**
     * 加锁
     */
    public void lock() {
        boolean relock = false;
        do {
            relock = false;
            for (int i = 0; i < locks.size(); i++) {
                int count = 0;
                Lock current = locks.get(i);
                try {
                    while ((!current.tryLock())
                            && (!current.tryLock(TIME_OUT, TimeUnit.MILLISECONDS))) {
                        if (count++ >= TIMES) {
                            relock = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    relock = true;
                }
                if (relock) {
                    unlock(i);
                    break;
                }
            }
        } while (relock);
    }

    /**
     * 解锁
     */
    public void unlock() {
        unlock(locks.size());
    }

    /**
     * 解锁
     * @param end 位置
     */
    private void unlock(int end) {
        end = Math.min(end, locks.size());
        for (int i = 0; i < end; i++) {
            Lock objectLock = locks.get(i);
            try {
                if (objectLock != null) {
                    objectLock.unlock();
                }
            } catch (Exception e) {}
        }
    }
}
