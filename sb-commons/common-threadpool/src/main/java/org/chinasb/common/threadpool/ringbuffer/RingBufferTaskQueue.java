package org.chinasb.common.threadpool.ringbuffer;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * ringbuffer任务队列
 * 
 * @author zhujuan
 *
 * @param <E>
 */
public class RingBufferTaskQueue extends BasePublisher {

	private final RingBuffer<RingBufferWrapperTask<Event<?>>> ringBuffer;
	private final Sequence gatingSequence;
	private long startSequence;

	public RingBufferTaskQueue(int ringBufferSize, ProducerType producerType) {
		EventFactory<RingBufferWrapperTask<Event<?>>> factory =
				new EventFactory<RingBufferWrapperTask<Event<?>>>() {

					@Override
					public RingBufferWrapperTask<Event<?>> newInstance() {
						return new RingBufferWrapperTask<>();
					}
				};
		switch (producerType) {
			case SINGLE:
				ringBuffer = RingBuffer.createSingleProducer(factory, ringBufferSize, WAIT_STRATEGY);
				break;
			case MULTI:
				ringBuffer = RingBuffer.createMultiProducer(factory, ringBufferSize, WAIT_STRATEGY);
				break;
			default:
				throw new IllegalStateException(producerType.toString());
		}
		gatingSequence = new Sequence();
		ringBuffer.addGatingSequences(gatingSequence);
	}

	/**
	 * Given specified <tt>requiredCapacity</tt> determines if that amount of space is available.
	 * Note, you can not assume that if this method returns <tt>true</tt> that a call to
	 * {@link RingBuffer#next()} will not block. Especially true if this ring buffer is set up to
	 * handle multiple producers.
	 *
	 * @param requiredCapacity The capacity to check for.
	 * @return <tt>true</tt> If the specified <tt>requiredCapacity</tt> is available <tt>false</tt>
	 *         if now.
	 */
	public boolean hasAvailableCapacity(int cap) {
		return ringBuffer.hasAvailableCapacity(cap);
	}

	/**
	 * <p>
	 * Increment and return the next sequence for the ring buffer. Calls of this method should
	 * ensure that they always publish the sequence afterward. E.g.
	 * 
	 * <pre>
	 * long sequence = ringBuffer.next();
	 * try {
	 * 	Event e = ringBuffer.get(sequence);
	 * 	// Do some work with the event.
	 * } finally {
	 * 	ringBuffer.publish(sequence);
	 * }
	 * </pre>
	 * <p>
	 * This method will not block if there is not space available in the ring buffer, instead it
	 * will throw an {@link InsufficientCapacityException}.
	 *
	 * @return The next sequence to publish to.
	 * @throws InsufficientCapacityException if the necessary space in the ring buffer is not
	 *         available
	 * @see RingBuffer#publish(long)
	 * @see RingBuffer#get(long)
	 */
	public long tryNext() throws InsufficientCapacityException {
		return ringBuffer.tryNext();
	}

	/**
	 * Publish the specified sequence. This action marks this particular message as being available
	 * to be read.
	 *
	 * @param sequence the sequence to publish.
	 */
	public void publish(long sequence) {
		ringBuffer.publish(sequence);
	}

	/**
	 * Determines if a particular entry has been published.
	 *
	 * @param gatingSequence The sequence to identify the entry.
	 * @return If the value has been published or not.
	 */
	public boolean isPublished() {
		return ringBuffer.isPublished(startSequence);
	}

	/**
     * Get the highest sequence number that can be safely read from the ring buffer.  Depending
     * on the implementation of the Sequencer this call may need to scan a number of values
     * in the Sequencer.  The scan will range from nextSequence to availableSequence.  If
     * there are no available values <code>&gt;= nextSequence</code> the return value will be
     * <code>nextSequence - 1</code>.  To work correctly a consumer should pass a value that
     * it 1 higher than the last sequence that was successfully processed.
     *
     * @param nextSequence      The sequence to start scanning from.
     * @param availableSequence The sequence to scan to.
     * @return The highest value that can be safely read, will be at least <code>nextSequence - 1</code>.
     */
	private long getHighestPublishedSequence(long startSequence) {
		for (long i = startSequence;; i++) {
			if (!ringBuffer.isPublished(i)) {
				return i - 1;
			}
		}
	}
	
	/**
	 * 消费事件
	 * 
	 * @param consumer
	 */
	public void consume() {
		final long lastToRead = startSequence;

		long highestToRead = getHighestPublishedSequence(lastToRead);
		if (highestToRead < lastToRead) {
			return;
		}

		try {
			RingBufferWrapperTask<?> t;
			for (long i = lastToRead; i <= highestToRead; i++) {
				t = ringBuffer.get(i);
				try {
					t.execute();
				} finally {
					t.reset();
				}
			}
		} finally {
			startSequence = highestToRead + 1;
			gatingSequence.set(highestToRead);
		}
	}

	@SuppressWarnings({"unchecked"})
	@Override
	protected <E extends Event<?>> EventTask<E> createTask() {
		 long l = ringBuffer.next();
		 RingBufferWrapperTask<?> t = ringBuffer.get(l);
		 t.setSequenceId(l);
		 return (EventTask<E>) t;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	protected <E extends Event<?>> EventTask<E> tryCreateTask() {
		RingBufferWrapperTask<Event<?>> t = null;;
		try {
			long l = ringBuffer.tryNext();
			t = ringBuffer.get(l);
			t.setSequenceId(l);
		} catch (InsufficientCapacityException e) {

		}
		return (EventTask<E>) t;
	}
	
    /**
     * Get the remaining capacity for this ringBuffer.
     *
     * @return The number of slots remaining.
     */
    public long getAvailableCapacity(){
        return ringBuffer.remainingCapacity();
    }
    
	private class RingBufferWrapperTask<E extends Event<?>> extends EventTask<E> {
		private long sequenceId;

		private RingBufferWrapperTask<E> setSequenceId(long sequenceId) {
			this.sequenceId = sequenceId;
			return this;
		}

		@Override
		public void submit() {
			ringBuffer.publish(sequenceId);
		}
	}
	
	// ----- wait strategy ------
	private static final NoOpWaitStrategy WAIT_STRATEGY = new NoOpWaitStrategy();

	private static class NoOpWaitStrategy implements WaitStrategy {

		@Override
		public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence,
				SequenceBarrier barrier)
						throws AlertException, InterruptedException, TimeoutException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void signalAllWhenBlocking() {

		}

	}
}
