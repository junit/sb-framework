package org.chinasb.common.threadpool.ordered;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import org.chinasb.common.URL;
import org.chinasb.common.threadpool.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for producing executors that run all tasks in order, which delegate
 * to a single common executor instance.
 *
 * @author zhujuan
 */
public final class OrderedExecutorFactory implements ExecutorFactory {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OrderedExecutorFactory.class);

	private final Executor parent;
    
	/**
	 * Construct a new instance delegating to the given parent executor.
	 *
	 * @param parent
	 *            the parent executor
	 */
    public OrderedExecutorFactory(final Executor parent) {
        this.parent = parent;
    }

	/**
	 * Get an executor that always executes tasks in order.
	 *
	 * @return an ordered executor
	 */
	@Override
    public Executor getExecutor() {
        return new OrderedExecutor(parent);
    }

    @Override
    public Executor getExecutor(URL url) {
        throw new IllegalAccessError();
    }
	/**
	 * An executor that always runs all tasks in order, using a delegate
	 * executor to run the tasks.
	 * <p/>
	 * More specifically, any call B to the {@link #execute(Runnable)} method
	 * that happens-after another call A to the same method, will result in B's
	 * task running after A's.
	 */
	private static final class OrderedExecutor implements Executor {
		private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

		// @protected by tasks
		private boolean running;

		private final Executor parent;

		private final Runnable runner;

		/**
		 * Construct a new instance.
		 *
		 * @param parent
		 *            the parent executor
		 */
		public OrderedExecutor(final Executor parent) {
			this.parent = parent;
			runner = new Runnable() {
				public void run() {
					for (;;) {
						// Optimization, first try without any locks
						Runnable task = tasks.poll();
						if (task == null) {
							synchronized (tasks) {
								// if it's null we need to retry now holding the
								// lock on tasks
								// this is because running=false and tasks.empty
								// must be an atomic operation
								// so we have to retry before setting the tasks
								// to false
								// this is a different approach to the
								// anti-pattern on synchronize-retry,
								// as this is just guaranteeing the
								// running=false and tasks.empty being an atomic
								// operation
								task = tasks.poll();
								if (task == null) {
									running = false;
									return;
								}
							}
						}
						try {
							task.run();
						} catch (Throwable t) {
							LOGGER.error("Caught unexpected Throwable", t);
						}
					}
				}
			};
		}

		/**
		 * Run a task.
		 *
		 * @param command
		 *            the task to run.
		 */
		public void execute(final Runnable command) {
			synchronized (tasks) {
				tasks.add(command);
				if (!running) {
					running = true;
					parent.execute(runner);
				}
			}
		}

		public String toString() {
			return "OrderedExecutor(running=" + running + ", tasks=" + tasks
					+ ")";
		}
	}
}