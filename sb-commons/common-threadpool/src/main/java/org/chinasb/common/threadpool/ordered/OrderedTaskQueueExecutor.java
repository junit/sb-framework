package org.chinasb.common.threadpool.ordered;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.chinasb.common.threadpool.ringbuffer.DisruptorExecutor;
import org.chinasb.common.threadpool.ringbuffer.Event;
import org.chinasb.common.threadpool.ringbuffer.EventConsumer;
import org.chinasb.common.utility.NamedThreadFactory;
import org.chinasb.common.utility.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于有序任务队列的线程池
 * 
 * @author zhujuan
 *
 */
public class OrderedTaskQueueExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderedTaskQueueExecutor.class);
	private static final EventConsumer<Event<AbstractDelayTask>> DELAY_TASK_CONSUMER =
			new EventConsumer<Event<AbstractDelayTask>>() {

				@Override
				public void accept(Event<AbstractDelayTask> event) {
					AbstractDelayTask task = event.getData();
					long start = System.currentTimeMillis();
					if (!task.canExec(start)) {
						task.getTaskQueue().enDelayQueue(task);
					}
					long end = System.currentTimeMillis();
					long interval = end - start;
					if (interval > 1000) {
						LOGGER.warn("DelayEventConsumer is spent too much time:" + interval + "ms");
					}
				}
			};
			
	private final ExecutorService taskExecutor;
	private final DisruptorExecutor delayTaskExecutor;
	private final TaskQueue defaultQueue;
			
	public OrderedTaskQueueExecutor() {
		this(null);
	}
	
	public OrderedTaskQueueExecutor(String name) {
		this(Runtime.getRuntime().availableProcessors(), name);
	}
	
	public OrderedTaskQueueExecutor(int corePoolSize, String name) {
		String prefix = name == null ? "OrderedTaskQueueExecutor" : name;
		taskExecutor = Executors.newFixedThreadPool(corePoolSize, new NamedThreadFactory(prefix));
		delayTaskExecutor = new DisruptorExecutor(prefix + "-延时任务处理器");
		defaultQueue = new OrderedTaskQueue(this);
	}

	/**
	 * 获取任务队列
	 * @return
	 */
    public TaskQueue getDefaultQueue() {
		return defaultQueue;
	}

	/**
     * 添加任务
     * @param task
     */
	public void enDefaultQueue(AbstractTask task) {
		defaultQueue.enqueue(task);
	}

	/**
	 * 添加延时任务
	 * @param delayTask
	 */
	public void enDelayQueue(AbstractDelayTask delayTask) {
		delayTaskExecutor.dispatch(Event.wrap(delayTask), DELAY_TASK_CONSUMER);
	}
	
	/**
	 * 执行{@link Runnable}
	 * @param action
	 */
	public void execute(Runnable runnable) {
		taskExecutor.execute(runnable);
	}
	
	/**
	 * 立即关闭
	 */
	public void shutdownNow() {
		delayTaskExecutor.halt();
		ThreadPoolUtils.shutdownNow(taskExecutor);
	}
	
	/**
	 * 超时关闭
	 * @param timeout
	 */
	public void shutdown(long timeout) {
		delayTaskExecutor.shutdown(timeout, TimeUnit.MILLISECONDS);
		ThreadPoolUtils.shutdownGraceful(taskExecutor, timeout);
	}
	
	/**
	 * 阻塞完成所有任务之后关闭
	 */
	public void shutdown() {
		delayTaskExecutor.shutdown();
		ThreadPoolUtils.shutdownGraceful(taskExecutor, Integer.MAX_VALUE);
	}	
}
