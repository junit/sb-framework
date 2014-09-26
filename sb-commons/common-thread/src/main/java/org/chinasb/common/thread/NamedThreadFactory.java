package org.chinasb.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 * @author zhujuan
 */
public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final String namePrefix;

    /**
     * 构造器
     * @param group
     * @param name
     */
    public NamedThreadFactory(ThreadGroup group, String name) {
        this.group = group;
        this.namePrefix = group.getName() + ":" + name;
    }

    /**
     * 创建线程
     */
    public Thread newThread(Runnable r) {
        Thread t =
                new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        return t;
    }
}
