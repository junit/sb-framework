package org.chinasb.common.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.chinasb.common.executor.event.MessageEvent;
import org.chinasb.common.executor.handler.MessageEventHandler;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 任务调度器
 * @author zhujuan
 */
public class DefaultDispatcher implements Dispatcher {
    private final Disruptor<MessageEvent> disruptor;
    private final RingBuffer<MessageEvent> ringBuffer;

    public DefaultDispatcher() {
        this(1, null, BUFFER_SIZE);
    }

    public DefaultDispatcher(final String executorName) {
        this(1, executorName, BUFFER_SIZE);
    }

    public DefaultDispatcher(final int maxThreads) {
        this(maxThreads, null, BUFFER_SIZE);
    }

    public DefaultDispatcher(final int maxThreads, final String executorName) {
        this(maxThreads, executorName, BUFFER_SIZE);
    }

    public DefaultDispatcher(final int maxThreads, final String executorName, int bufferSize) {
        if ((bufferSize < 0) || (bufferSize & (bufferSize - 1)) != 0) {
            throw new IllegalArgumentException("bufferSize must be power of 2.");
        }
        int threadPoolSize = Math.min(Math.abs(maxThreads), DEFAULT_IO_THREADS);
        ThreadFactory threadFactory = new ThreadFactory() {
            private AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t =
                        new Thread(r, (executorName == null ? "Disruptor" : executorName) + "-"
                                + counter.getAndIncrement());
                t.setDaemon(true);
                t.setPriority(Thread.MAX_PRIORITY);
                return t;
            }
        };
        ExecutorService executor;
        if (threadPoolSize == 1) {
            executor = Executors.newSingleThreadExecutor(threadFactory);
        } else {
            executor = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
        }
        disruptor = new Disruptor<MessageEvent>(MessageEvent.EVENT_FACTORY, bufferSize, executor);
        if (threadPoolSize == 1) {
            EventHandler<MessageEvent>[] handlers = new MessageEventHandler[threadPoolSize];
            for (int i = 0; i < threadPoolSize; i++) {
                handlers[i] = new MessageEventHandler();
            }
            disruptor.handleEventsWith(handlers);
        } else {
            WorkHandler<MessageEvent>[] handlers = new MessageEventHandler[threadPoolSize];
            for (int i = 0; i < threadPoolSize; i++) {
                handlers[i] = new MessageEventHandler();
            }
            disruptor.handleEventsWithWorkerPool(handlers);
        }
        ringBuffer = disruptor.start();
    }

    @Override
    public boolean dispatch(Runnable task) {
        try {
            final long sequence = ringBuffer.tryNext();
            try {
                MessageEvent event = ringBuffer.get(sequence);
                event.task = task;
            } finally {
                ringBuffer.publish(sequence);
            }
            return true;
        } catch (InsufficientCapacityException e) {
            return false;
        }
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
    }
}
