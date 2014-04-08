package org.chinasb.common.executor.disruptor;

/**
 * 任务分发接口
 * @author zhujuan
 */
public interface Dispatcher {
    static final int BUFFER_SIZE = 1024;
    static final int DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    
    /**
     * 任务分发
     * @param task
     * @return
     */
    public boolean dispatch(Runnable task);

    /**
     * 停止
     */
    void shutdown();
}
