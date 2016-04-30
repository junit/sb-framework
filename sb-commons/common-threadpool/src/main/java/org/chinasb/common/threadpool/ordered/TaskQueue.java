package org.chinasb.common.threadpool.ordered;

/**
 * 任务队列接口
 * @author zhujuan
 *
 */
public interface TaskQueue {
	/**
	 * 获取任务队列
	 * @return
	 */
	TaskQueue getTaskQueue();

	/**
	 * 添加普通任务
	 * @param task
	 * @return
	 */
	boolean enqueue(AbstractTask task);
	
	/**
	 * 添加延时任务
	 * @param delayTask
	 */
	void enDelayQueue(AbstractDelayTask delayTask);
	
	/**
	 * 获取任务数量
	 * @return
	 */
	int size();
}
