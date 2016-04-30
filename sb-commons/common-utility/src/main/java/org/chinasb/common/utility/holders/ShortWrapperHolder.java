package org.chinasb.common.utility.holders;

/**
 * A holder for Shorts.
 * 
 * @author zhujuan
 */
public final class ShortWrapperHolder implements Holder {
	private Short value;

	public ShortWrapperHolder() {

	}

	public ShortWrapperHolder(Short value) {
		this.value = value;
	}
	
	public Short get() {
		return value;
	}

	public void set(Short value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
