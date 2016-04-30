package org.chinasb.common.utility.holders;

import java.math.BigInteger;

/**
 * A holder for BigIntegers.
 * 
 * @author zhujuan
 */
public final class BigIntegerHolder implements Holder {
	private BigInteger value;

	public BigIntegerHolder() {

	}

	public BigIntegerHolder(BigInteger value) {
		this.value = value;
	}
	
	public BigInteger get() {
		return value;
	}

	public void set(BigInteger value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
