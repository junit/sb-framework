package org.chinasb.common.utility.holders;

/**
 * A holder for Integers.
 * 
 * @author zhujuan
 */
public final class IntegerWrapperHolder implements Holder {
	private Integer value;

	public IntegerWrapperHolder() {

	}

	public IntegerWrapperHolder(Integer value) {
		this.value = value;
	}
	
	public Integer get() {
		return value;
	}

	public void set(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
