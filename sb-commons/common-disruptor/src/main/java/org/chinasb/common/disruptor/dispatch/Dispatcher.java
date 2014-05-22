package org.chinasb.common.disruptor.dispatch;

import java.util.concurrent.TimeUnit;

import org.chinasb.common.disruptor.Event;


/**
 * 任务调度
 * @author zhujuan
 */
public interface Dispatcher {
    /**
     * 任务调度是否可用
     * @return
     */
    boolean alive();

    /**
     * 停止任务调度（阻塞直到所有提交任务完成）
     * @return
     */
    boolean awaitAndShutdown();

    /**
     * 停止任务调度（阻塞直到所有提交任务完成）
     * @param timeout
     * @param timeUnit
     * @return
     */
    boolean awaitAndShutdown(long timeout, TimeUnit timeUnit);

    /**
     * 停止任务调度
     */
    void shutdown();

    /**
     * 强制停止任务调度（正在执行的任务将被停止，未执行的任务将被丢弃）
     */
    void halt();

    <E extends Event<?>> void dispatch(E event);
}
