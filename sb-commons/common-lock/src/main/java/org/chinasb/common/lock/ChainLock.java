package org.chinasb.common.lock;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 锁链
 * @author zhujuan
 */
public class ChainLock {
    private final Lock current;
    private final ChainLock next;

    /**
     * 构造一条锁链
     * @param locks
     */
    public ChainLock(List<? extends Lock> locks) {
        if ((locks == null) || (locks.isEmpty())) {
            throw new IllegalArgumentException("构建锁链的锁数量不能为0");
        }
        this.current = ((Lock) locks.remove(0));
        if (locks.size() > 0) {
            this.next = new ChainLock(locks);
        } else {
            this.next = null;
        }
    }

    /**
     * 加锁
     */
    public void lock() {
        this.current.lock();
        if (this.next != null) {
            this.next.lock();
        }
    }

    /**
     * 解锁
     */
    public void unlock() {
        if (this.next != null) {
            this.next.unlock();
        }
        this.current.unlock();
    }
}
