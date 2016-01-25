package org.chinasb.common.threadpool.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Dispatcher;
import reactor.core.dispatch.RingBufferDispatcher;
import reactor.core.dispatch.WorkQueueDispatcher;
import reactor.core.dispatch.wait.AgileWaitingStrategy;
import reactor.jarjar.com.lmax.disruptor.dsl.ProducerType;

/**
 * 
 * @author zhujuan
 *
 */
public final class DispatcherFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherFactory.class);

    private DispatcherFactory() {}

    /**
     * 创建一个多线程调度器
     * 
     * @param loggerClass
     * @param name
     * @return
     */
    public static final Dispatcher workQueueDispatcher(Class<?> loggerClass, String name) {
        return workQueueDispatcher(loggerClass, name, Runtime.getRuntime().availableProcessors(),
                2048);
    }

    /**
     * 创建一个多线程调度器
     * 
     * @param loggerClass
     * @param name
     * @param poolSize
     * @param backlog
     * @return
     */
    public static final Dispatcher workQueueDispatcher(Class<?> loggerClass, String name,
            int poolSize, int backlog) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "Constructing a new WorkQueueDispatcher requested by {}, name[{}], poolsize[{}], backlog[{}]",
                    loggerClass.getSimpleName(), name, poolSize, backlog);
        }
        return new WorkQueueDispatcher(name == null ? "WorkQueueDispatcher-Factory" : name,
                poolSize, backlog, new ThrowableConsumer(loggerClass), ProducerType.MULTI,
                new AgileWaitingStrategy());
    }

    /**
     * 创建一个单线程调度器
     * 
     * @param loggerClass
     * @param name
     * @return
     */
    public static final Dispatcher ringBufferDispatcher(Class<?> loggerClass, String name) {
        return ringBufferDispatcher(loggerClass, name, 2048);
    }

    /**
     * 创建一个单线程调度器
     * 
     * @param loggerClass
     * @param name
     * @param backlog
     * @return
     */
    public static final Dispatcher ringBufferDispatcher(Class<?> loggerClass, String name,
            int backlog) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "Constructing a new RingBufferDispatcher requested by {}, name[{}], backlog[{}]",
                    loggerClass.getSimpleName(), name, backlog);
        }
        return new RingBufferDispatcher(name == null ? "RingBufferDispatcher-Factory" : name,
                backlog, new ThrowableConsumer(loggerClass), ProducerType.SINGLE,
                new AgileWaitingStrategy());
    }
}
