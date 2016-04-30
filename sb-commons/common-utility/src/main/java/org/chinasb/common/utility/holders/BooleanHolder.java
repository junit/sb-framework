package org.chinasb.common.utility.holders;

/**
 * A holder for booleans.
 * 
 * @author zhujuan
 */
public final class BooleanHolder implements Holder {
	private boolean value;

	public BooleanHolder() {

	}

	public BooleanHolder(boolean value) {
		this.value = value;
	}
	
	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
