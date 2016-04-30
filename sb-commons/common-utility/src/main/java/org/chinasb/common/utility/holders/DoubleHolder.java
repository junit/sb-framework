package org.chinasb.common.utility.holders;

/**
 * A holder for doubles.
 * 
 * @author zhujuan
 */
public final class DoubleHolder implements Holder {
	private double value;

	public DoubleHolder() {

	}

	public DoubleHolder(double value) {
		this.value = value;
	}

	public double get() {
		return value;
	}

	public void set(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
