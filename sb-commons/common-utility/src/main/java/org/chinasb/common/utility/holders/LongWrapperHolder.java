package org.chinasb.common.utility.holders;

/**
 * A holder for Longs.
 * 
 * @author zhujuan
 */
public final class LongWrapperHolder implements Holder {
	private Long value;

	public LongWrapperHolder() {

	}

	public LongWrapperHolder(Long value) {
		this.value = value;
	}

	public Long get() {
		return value;
	}

	public void set(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
