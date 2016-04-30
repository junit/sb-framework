package org.chinasb.common.utility.holders;

/**
 * A holder for Strings.
 * 
 * @author zhujuan
 */
public final class StringHolder implements Holder {
	private String value;

	public StringHolder() {

	}

	public StringHolder(String value) {
		this.value = value;
	}
	
	public String get() {
		return value;
	}

	public void set(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
