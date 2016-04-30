package org.chinasb.common.utility.holders;

/**
 * A holder for byte[]s.
 * 
 * @author zhujuan
 */
public final class ByteArrayHolder implements Holder {
	private byte[] value;

	public ByteArrayHolder() {

	}

	public ByteArrayHolder(byte[] value) {
		this.value = value;
	}
	
	public byte[] get() {
		return value;
	}

	public void set(byte[] value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new String(value);
	}
}
