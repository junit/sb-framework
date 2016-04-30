package org.chinasb.common.utility.holders;

/**
 * A holder for Doubles.
 * 
 * @author zhujuan
 */
public final class DoubleWrapperHolder implements Holder {
	private Double value;

	public DoubleWrapperHolder() {

	}

	public DoubleWrapperHolder(Double value) {
		this.value = value;
	}
	
	public Double get() {
		return value;
	}

	public void set(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
