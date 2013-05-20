package com.smarkets.android.domain;

import static java.lang.String.format;

public class CashAmount {

	public final Double amount;
	public final String currency;

	private CashAmount(Double amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public static CashAmount cashAmountWithCurrency(Double amount, String currency) {
		return new CashAmount(amount, currency);
	}

	public String toString() {
		return format("%s %s", amount, currency);
	}

}
