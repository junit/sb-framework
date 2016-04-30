package org.chinasb.common.utility.holders;

/**
 * A holder for ints.
 * 
 * @author zhujuan
 */
public final class IntHolder implements Holder {
	private int value;

	public IntHolder() {

	}

	public IntHolder(int value) {
		this.value = value;
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
