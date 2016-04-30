package org.chinasb.common.utility.holders;

/**
 * A holder for longs.
 * 
 * @author zhujuan
 */
public final class LongHolder implements Holder {
	private long value;

	public LongHolder() {

	}

	public LongHolder(long value) {
		this.value = value;
	}
	
	public long get() {
		return value;
	}

	public void set(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
