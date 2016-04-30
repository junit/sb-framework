package org.chinasb.common.utility.holders;

/**
 * A holder for shorts.
 * 
 * @author zhujuan
 */
public final class ShortHolder implements Holder {
	private short value;

	public ShortHolder() {

	}

	public ShortHolder(short value) {
		this.value = value;
	}

	public short get() {
		return value;
	}

	public void set(short value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
