package org.chinasb.common.threadpool.ringbuffer;

/**
 * 事件发布者
 * 
 * @author zhujuan
 */
public interface EventPublisher {
    /**
     * 判断事件发布者存活状态
     * @return
     */
    boolean alive();
    
    /**
     * 包装一个{@link Runnable}事件进行分发消费
     * @param event
     * @param consumer
     */
    <E extends Event<?>> void dispatch(E event);
    
    /**
     * 分发事件给指定的事件消费者
     * @param event
     * @param consumer
     */
    <E extends Event<?>> void dispatch(E event, EventConsumer<E> consumer);
    
    /**
     * 分发事件给指定的事件消费者，处理消费异常
     * @param event
     * @param consumer
     * @param errorConsumer
     */
    <E extends Event<?>> void dispatch(E event, EventConsumer<E> consumer, EventConsumer<Throwable> errorConsumer);
    
    /**
     * 包装一个{@link Runnable}事件，尝试进行分发消费
     * @param event
     * @param consumer
     */
    <E extends Event<?>> boolean tryDispatch(E event);
    
    /**
     * 尝试分发事件给指定的事件消费者
     * @param event
     * @param consumer
     */
    <E extends Event<?>> boolean tryDispatch(E event, EventConsumer<E> consumer);
    
    /**
     * 尝试分发事件给指定的事件消费者，处理消费异常
     * @param event
     * @param consumer
     * @param errorConsumer
     */
    <E extends Event<?>> boolean tryDispatch(E event, EventConsumer<E> consumer, EventConsumer<Throwable> errorConsumer);
}
