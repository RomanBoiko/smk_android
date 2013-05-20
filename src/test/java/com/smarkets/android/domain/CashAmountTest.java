package com.smarkets.android.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class CashAmountTest {

	@Test
	public void shouldCreateCashAmountAndFormatItToString() {
		CashAmount amount = CashAmount.cashAmountWithCurrency(12.0, "GBP");
		assertThat(amount.toString(), is("12.0 GBP"));
	}
}
