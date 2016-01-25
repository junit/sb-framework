package org.chinasb.common.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 * 
 * @author zhujuan
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final boolean daemon;

    public NamedThreadFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemo) {
        namePrefix = prefix + "-thread-";
        daemon = daemo;
        SecurityManager s = System.getSecurityManager();
        group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    /**
     * 创建线程
     */
    public Thread newThread(Runnable r) {
        Thread t =
                new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        t.setDaemon(daemon);
        return t;
    }

    public ThreadGroup getThreadGroup() {
        return group;
    }
}
