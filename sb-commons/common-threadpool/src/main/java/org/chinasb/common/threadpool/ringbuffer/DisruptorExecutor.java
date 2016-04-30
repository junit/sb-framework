package org.chinasb.common.threadpool.ringbuffer;

import java.util.concurrent.TimeUnit;

import org.chinasb.common.utility.NamedDaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 基于ringbuffer队列的任务线程池
 * 
 * @author zhujuan
 */
public class DisruptorExecutor extends BasePublisher {
	private static final int DEFAULT_BUFFER_SIZE  = 1024;

	private final Disruptor<RingBufferEventTask<Event<?>>> disruptor;
	private final RingBuffer<RingBufferEventTask<Event<?>>> ringBuffer;

	public DisruptorExecutor(String name) {
		this(name, DEFAULT_BUFFER_SIZE);
	}
	
	@SuppressWarnings("unchecked")
	public DisruptorExecutor(String name, int ringBufferSize) {
		if ((ringBufferSize < 0) || (ringBufferSize & (ringBufferSize - 1)) != 0) {
			throw new IllegalArgumentException("bufferSize must be power of 2.");
		}
	
		this.disruptor = new Disruptor<RingBufferEventTask<Event<?>>>(new EventFactory<RingBufferEventTask<Event<?>>>() {
			@SuppressWarnings("rawtypes")
			@Override
			public RingBufferEventTask<Event<?>> newInstance() {
				return new RingBufferEventTask();
			}
		}, ringBufferSize, new NamedDaemonThreadFactory(name + "-ringbuffer"));
		this.disruptor.setDefaultExceptionHandler(new ExceptionHandler<RingBufferEventTask<Event<?>>>() {

			@Override
			public void handleEventException(Throwable ex, long sequence, RingBufferEventTask<Event<?>> event) {
				Logger log = LoggerFactory
						.getLogger(DisruptorExecutor.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				Logger log = LoggerFactory
						.getLogger(DisruptorExecutor.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				Logger log = LoggerFactory
						.getLogger(DisruptorExecutor.class);
				if (log.isErrorEnabled()) {
					log.error(ex.getMessage(), ex);
				}
			}

		});
		this.disruptor.handleEventsWith(new RingBufferEventTaskHandler());
		this.ringBuffer = disruptor.start();
	}
    
    /**
     * <p>Waits until all events currently in the disruptor have been processed by all event processors
     * and then halts the processors.</p>
     * <p>
     * <p>This method will not shutdown the executor, nor will it await the final termination of the
     * processor threads.</p>
     *
     * @param timeout  the amount of time to wait for all events to be processed. <code>-1</code> will give an infinite timeout
     * @param timeUnit the unit the timeOut is specified in
     */
	public void shutdown(long timeout, TimeUnit timeUnit) {
		try {
			disruptor.shutdown(timeout, timeUnit);
		} catch (TimeoutException e) {
			Thread.currentThread().interrupt();
		}
        alive.compareAndSet(true, false);
	}

    /**
     * Waits until all events currently in the disruptor have been processed by all event processors
     * and then halts the processors.  It is critical that publishing to the ring buffer has stopped
     * before calling this method, otherwise it may never return.
     * <p>
     * <p>This method will not shutdown the executor, nor will it await the final termination of the
     * processor threads.</p>
     */
	public void shutdown() {
		disruptor.shutdown();
		alive.compareAndSet(true, false);
	}

    /**
     * Calls {@link com.lmax.disruptor.EventProcessor#halt()} on all of the event processors created via this disruptor.
     */
	public void halt() {
		disruptor.halt();
		alive.compareAndSet(true, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <E extends Event<?>> EventTask<E> createTask() {
		long l = ringBuffer.next();
		RingBufferEventTask<Event<?>> t = ringBuffer.get(l);
		t.setSequenceId(l);
		return (EventTask<E>) t;
	}
    
	@SuppressWarnings({"unchecked"})
	@Override
	protected <E extends Event<?>> EventTask<E> tryCreateTask() {
		RingBufferEventTask<Event<?>> t = null;;
		try {
			long l = ringBuffer.tryNext();
			t = ringBuffer.get(l);
			t.setSequenceId(l);
		} catch (InsufficientCapacityException e) {

		}
		return (EventTask<E>) t;
	}
	
    public int getAvailableCapacity(){
        return (int) ringBuffer.remainingCapacity();
    }
    
	private class RingBufferEventTask<E extends Event<?>> extends EventTask<E> {
		private long sequenceId;

		private RingBufferEventTask<E> setSequenceId(long sequenceId) {
			this.sequenceId = sequenceId;
			return this;
		}

		@Override
		public void submit() {
			ringBuffer.publish(sequenceId);
		}
	}

	private class RingBufferEventTaskHandler implements EventHandler<RingBufferEventTask<Event<?>>> {
		@Override
		public void onEvent(RingBufferEventTask<Event<?>> t, long sequence, boolean endOfBatch)
				throws Exception {
			try {
				t.execute();
			} finally {
				t.reset();
			}
		}
	}
}
