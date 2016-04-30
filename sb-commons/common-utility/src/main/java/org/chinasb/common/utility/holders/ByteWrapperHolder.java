package org.chinasb.common.utility.holders;

/**
 * A holder for Bytes.
 * 
 * @author zhujuan
 */
public final class ByteWrapperHolder implements Holder {
	private Byte value;

	public ByteWrapperHolder() {

	}

	public ByteWrapperHolder(Byte value) {
		this.value = value;
	}
	
	public Byte get() {
		return value;
	}

	public void set(Byte value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
