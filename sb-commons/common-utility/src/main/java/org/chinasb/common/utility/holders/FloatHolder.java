package org.chinasb.common.utility.holders;

/**
 * A holder for floats.
 * 
 * @author zhujuan
 */
public final class FloatHolder implements Holder {
	private float value;

	public FloatHolder() {

	}

	public FloatHolder(float value) {
		this.value = value;
	}

	public float get() {
		return value;
	}

	public void set(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
