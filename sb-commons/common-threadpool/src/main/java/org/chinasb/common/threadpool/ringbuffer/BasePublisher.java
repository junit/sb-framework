package org.chinasb.common.threadpool.ringbuffer;

import java.util.concurrent.atomic.AtomicBoolean;

import org.chinasb.common.threadpool.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件发布者抽象
 * 
 * @author zhujuan
 */
public abstract class BasePublisher implements EventPublisher {
	
    protected final AtomicBoolean alive = new AtomicBoolean(true);

	@Override
    public boolean alive() {
        return alive.get();
    }
	
	@Override
	public <E extends Event<?>> void dispatch(E event) {
		dispatch(event, null, null);
	}
	
	@Override
	public <E extends Event<?>> void dispatch(E event, EventConsumer<E> consumer) {
		dispatch(event, consumer, null);
	}
	
	@Override
	public <E extends Event<?>> void dispatch(E event, EventConsumer<E> consumer,
			EventConsumer<Throwable> errorConsumer) {
		if (!alive()) {
			throw new IllegalStateException("This Dispatcher has been shutdown");
		}
		EventTask<E> task = createTask();
		task.setEvent(event);
		task.setConsumer(consumer);
		task.setErrorConsumer(errorConsumer);
		task.submit();
	}
	
	@Override
	public <E extends Event<?>> boolean tryDispatch(E event) {
		return tryDispatch(event, null, null);
	}
	
	@Override
	public <E extends Event<?>> boolean tryDispatch(E event, EventConsumer<E> consumer) {
		return tryDispatch(event, consumer, null);
	}
	
	@Override
	public <E extends Event<?>> boolean tryDispatch(E event, EventConsumer<E> consumer,
			EventConsumer<Throwable> errorConsumer) {
		if (!alive()) {
			return false;
		}
		EventTask<E> task = tryCreateTask();
		if (task == null) {
			return false;
		}
		task.setEvent(event);
		task.setConsumer(consumer);
		task.setErrorConsumer(errorConsumer);
		task.submit();
		return true;
	}
	
	protected abstract <E extends Event<?>> EventTask<E> createTask();
	protected abstract <E extends Event<?>> EventTask<E> tryCreateTask();

	protected abstract class EventTask<E extends Event<?>> implements Task {
		private final Logger logger = LoggerFactory.getLogger(getClass());

		private volatile E event;
		private volatile EventConsumer<E> consumer;
		private volatile EventConsumer<Throwable> errorConsumer;

		EventTask<E> setEvent(E event) {
			this.event = event;
			return this;
		}
		
		EventTask<E> setConsumer(EventConsumer<E> consumer) {
			this.consumer = consumer;
			return this;
		}

		EventTask<E> setErrorConsumer(EventConsumer<Throwable> errorConsumer) {
			this.errorConsumer = errorConsumer;
			return this;
		}
		
		protected abstract void submit();

		@Override
		public void reset() {
			event = null;
			consumer = null;
			errorConsumer = null;
		}

		@Override
		public void execute() {
			if (null != consumer) {
				try {
					consumer.accept(event);
				} catch (Exception e) {
					if (null != errorConsumer) {
						errorConsumer.accept(e);
					} else {
						logger.error("Consumer {} failed: {}", consumer, e.getMessage(), e);
					}
				}
			} else {
				Object runable = event.getData();
				if (runable instanceof Runnable) {
					((Runnable) runable).run();
				} else {
					logger.error("runnable {} failed", runable);
				}
			}
		}
	}
}
