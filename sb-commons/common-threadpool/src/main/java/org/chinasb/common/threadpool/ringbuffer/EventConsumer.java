package org.chinasb.common.threadpool.ringbuffer;

/**
 * 事件消费者
 * 
 * @author zhujuan
 *
 * @param <T>
 */
public interface EventConsumer<T> {

	/**
	 * 执行
	 * 
	 * @param t
	 */
	void accept(T t);

}