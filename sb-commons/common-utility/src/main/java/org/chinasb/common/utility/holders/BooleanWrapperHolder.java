package org.chinasb.common.utility.holders;

/**
 * A holder for Booleans.
 * 
 * @author zhujuan
 */
public final class BooleanWrapperHolder implements Holder {
	private Boolean value;

	public BooleanWrapperHolder() {

	}

	public BooleanWrapperHolder(Boolean value) {
		this.value = value;
	}
	
	public Boolean get() {
		return value;
	}

	public void set(Boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
