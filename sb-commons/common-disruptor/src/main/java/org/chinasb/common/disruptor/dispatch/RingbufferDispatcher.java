package org.chinasb.common.disruptor.dispatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.chinasb.common.disruptor.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 基于ringbuffer队列的任务调度
 * @author zhujuan
 */
public class RingbufferDispatcher extends BaseLifecycleDispatcher {
    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private final ExecutorService executor;
    private final Disruptor<RingBufferTask<?>> disruptor;
    private final RingBuffer<RingBufferTask<?>> ringBuffer;

    public RingbufferDispatcher(String executorName) {
        this(false, executorName, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
    }

    public RingbufferDispatcher(String executorName, int bufferSize) {
        this(false, executorName, bufferSize, ProducerType.MULTI, new BlockingWaitStrategy());
    }

    public RingbufferDispatcher(boolean multiThread, String executorName) {
        this(multiThread, executorName, BUFFER_SIZE, ProducerType.MULTI, new BlockingWaitStrategy());
    }
    
    public RingbufferDispatcher(boolean multiThread, String executorName, int bufferSize) {
        this(multiThread, executorName, bufferSize, ProducerType.MULTI, new BlockingWaitStrategy());
    }
    
    @SuppressWarnings("unchecked")
    public RingbufferDispatcher(final boolean multiThread, final String executorName,
            int bufferSize, ProducerType producerType, WaitStrategy waitStrategy) {
        if ((bufferSize < 0) || (bufferSize & (bufferSize - 1)) != 0) {
            throw new IllegalArgumentException("bufferSize must be power of 2.");
        }
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t =
                        new Thread(r, (executorName == null ? "Default-ringbuffer" : executorName
                                + "-ringbuffer")
                                + (!multiThread ? "" : "-" + counter.incrementAndGet()));
                t.setDaemon(true);
                t.setPriority(Thread.MAX_PRIORITY);
                return t;
            }
        };
        if (!multiThread) {
            executor = Executors.newSingleThreadExecutor(threadFactory);
        } else {
            executor = Executors.newFixedThreadPool(DEFAULT_IO_THREADS, threadFactory);
        }
        disruptor = new Disruptor<RingBufferTask<?>>(new EventFactory<RingBufferTask<?>>() {
            @SuppressWarnings("rawtypes")
            @Override
            public RingBufferTask<?> newInstance() {
                return new RingBufferTask();
            }
        }, bufferSize, executor, producerType, waitStrategy);
        disruptor.handleExceptionsWith(new ExceptionHandler() {
            @Override
            public void handleEventException(Throwable ex, long sequence, Object event) {
                Logger log = LoggerFactory.getLogger(RingbufferDispatcher.class);
                if (log.isErrorEnabled()) {
                    log.error(ex.getMessage(), ex);
                }
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                Logger log = LoggerFactory.getLogger(RingbufferDispatcher.class);
                if (log.isErrorEnabled()) {
                    log.error(ex.getMessage(), ex);
                }
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                Logger log = LoggerFactory.getLogger(RingbufferDispatcher.class);
                if (log.isErrorEnabled()) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });
        if (!multiThread) {
            disruptor.handleEventsWith(new RingBufferTaskHandler());
        } else {
            RingBufferTaskHandler[] handlers = new RingBufferTaskHandler[DEFAULT_IO_THREADS];
            for (int i = 0; i < DEFAULT_IO_THREADS; i++) {
                handlers[i] = new RingBufferTaskHandler();
            }
            disruptor.handleEventsWithWorkerPool(handlers);
        }
        ringBuffer = disruptor.start();
    }

    @Override
    public boolean awaitAndShutdown(long timeout, TimeUnit timeUnit) {
        shutdown();
        try {
            return executor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        executor.shutdown();
        super.shutdown();
    }

    @Override
    public void halt() {
        executor.shutdownNow();
        disruptor.halt();
        super.halt();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <E extends Event<?>> Task<E> createTask() {
        long l = ringBuffer.next();
        RingBufferTask<?> t = ringBuffer.get(l);
        t.setSequenceId(l);
        return (Task<E>) t;
    }

    private class RingBufferTask<E extends Event<?>> extends Task<E> {
        private long sequenceId;

        private RingBufferTask<E> setSequenceId(long sequenceId) {
            this.sequenceId = sequenceId;
            return this;
        }

        @Override
        public void submit() {
            ringBuffer.publish(sequenceId);
        }
    }

    private class RingBufferTaskHandler
            implements
                EventHandler<RingBufferTask<?>>,
                WorkHandler<RingBufferTask<?>> {
        @Override
        public void onEvent(RingBufferTask<?> t, long sequence, boolean endOfBatch)
                throws Exception {
            try {
                t.execute();
            } finally {
                t.reset();
            }
        }

        @Override
        public void onEvent(RingBufferTask<?> t) throws Exception {
            try {
                t.execute();
            } finally {
                t.reset();
            }
        }
    }
}
