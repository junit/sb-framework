package org.chinasb.common.threadpool.ringbuffer;

import java.io.Serializable;

/**
 * 事件对象
 * 
 * @author zhujuan
 * @param <T>
 */
public class Event<T> implements Serializable {

	private static final long serialVersionUID = 7447332262815727338L;

	/**
	 * An {@code Event} with {@code null} data.
	 */
	public static final Event<Void> NULL_EVENT = new Event<Void>(null);

	private volatile T data;

	public Event(T data) {
		this.data = data;
	}

	public static <T> Event<T> wrap(T obj) {
		return new Event<T>(obj);
	}

	public T getData() {
		return data;
	}

	public Event<T> setData(T data) {
		this.data = data;
		return this;
	}

	public Event<T> copy() {
		return copy(data);
	}

	public <E> Event<E> copy(E data) {
		return new Event<E>(data);
	}

	@Override
	public String toString() {
		return "Event{" + "data=" + data + '}';
	}
}
