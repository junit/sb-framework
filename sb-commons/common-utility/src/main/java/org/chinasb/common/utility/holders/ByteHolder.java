package org.chinasb.common.utility.holders;

/**
 * A holder for bytes.
 * 
 * @author zhujuan
 */
public final class ByteHolder implements Holder {
	private byte value;

	public ByteHolder() {

	}

	public ByteHolder(byte value) {
		this.value = value;
	}

	public byte get() {
		return value;
	}

	public void set(byte value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
