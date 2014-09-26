package org.chinasb.common.disruptor.dispatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.chinasb.common.disruptor.Event;
import org.chinasb.common.thread.NamedThreadFactory;
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
 * 
 * @author zhujuan
 */
public class RingbufferDispatcher extends BaseLifecycleDispatcher {
	private static final int BUFFER_SIZE = 1024;

	private final ExecutorService executor;
	private final Disruptor<RingBufferTask<?>> disruptor;
	private final RingBuffer<RingBufferTask<?>> ringBuffer;

	public RingbufferDispatcher(String name) {
		this(name, BUFFER_SIZE);
	}

	public RingbufferDispatcher(String name, int bufferSize) {
		this(name, bufferSize, 1);
	}

	public RingbufferDispatcher(String name, int bufferSize, int eventThreads) {
		this(name, bufferSize, eventThreads, ProducerType.MULTI,
				new BlockingWaitStrategy(), null);
	}

	@SuppressWarnings("unchecked")
	public RingbufferDispatcher(String name, int bufferSize, int eventThreads,
			ProducerType producerType, WaitStrategy waitStrategy,
			ExecutorService executor) {
		if ((bufferSize < 0) || (bufferSize & (bufferSize - 1)) != 0) {
			throw new IllegalArgumentException("bufferSize must be power of 2.");
		}

		ThreadGroup group = new ThreadGroup(name);
		NamedThreadFactory factory = new NamedThreadFactory(group, name);
		if (null == executor) {
			this.executor = Executors.newFixedThreadPool(eventThreads, factory);
		} else {
			this.executor = executor;
		}

		this.disruptor = new Disruptor<RingBufferTask<?>>(
				new EventFactory<RingBufferTask<?>>() {
					@SuppressWarnings("rawtypes")
					@Override
					public RingBufferTask<?> newInstance() {
						return new RingBufferTask();
					}
				}, bufferSize, this.executor, producerType, waitStrategy);
		this.disruptor.handleExceptionsWith(new ExceptionHandler() {
			@Override
			public void handleEventException(Throwable ex, long sequence,
					Object event) {
				Logger log = LoggerFactory
						.getLogger(RingbufferDispatcher.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				Logger log = LoggerFactory
						.getLogger(RingbufferDispatcher.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				Logger log = LoggerFactory
						.getLogger(RingbufferDispatcher.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}
		});
		if (eventThreads > 1) {
			RingBufferTaskHandler[] handlers = new RingBufferTaskHandler[eventThreads];
			for (int i = 0; i < eventThreads; i++) {
				handlers[i] = new RingBufferTaskHandler();
			}
			this.disruptor.handleEventsWithWorkerPool(handlers);
		} else {
			this.disruptor.handleEventsWith(new RingBufferTaskHandler());
		}
		this.ringBuffer = this.disruptor.start();
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

	private class RingBufferTaskHandler implements
			EventHandler<RingBufferTask<?>>, WorkHandler<RingBufferTask<?>> {
		@Override
		public void onEvent(RingBufferTask<?> t, long sequence,
				boolean endOfBatch) throws Exception {
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
