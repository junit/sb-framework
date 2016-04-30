package org.chinasb.common.utility.holders;

/**
 * A holder for Objects.
 * 
 * @author zhujuan
 */
public final class ObjectHolder<T> implements Holder {
	private T value;

	public ObjectHolder() {

	}

	public ObjectHolder(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
