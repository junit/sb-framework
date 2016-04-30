package org.chinasb.common.utility.holders;

/**
 * A holder for Floats.
 * 
 * @author zhujuan
 */
public final class FloatWrapperHolder implements Holder {
	private Float value;

	public FloatWrapperHolder() {

	}

	public FloatWrapperHolder(Float value) {
		this.value = value;
	}

	public Float get() {
		return value;
	}

	public void set(Float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
