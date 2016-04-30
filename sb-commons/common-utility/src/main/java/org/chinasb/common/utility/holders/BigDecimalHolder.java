package org.chinasb.common.utility.holders;

import java.math.BigDecimal;

/**
 * A holder for BigDecimals.
 * 
 * @author zhujuan
 */
public final class BigDecimalHolder implements Holder {
	private BigDecimal value;

	public BigDecimalHolder() {

	}

	public BigDecimalHolder(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal get() {
		return value;
	}

	public void set(BigDecimal value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
