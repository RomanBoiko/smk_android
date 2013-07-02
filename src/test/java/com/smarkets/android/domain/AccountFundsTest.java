package com.smarkets.android.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.smarkets.android.domain.AccountFunds;

import smarkets.seto.SmarketsSetoPiqi.AccountState;
import smarkets.seto.SmarketsSetoPiqi.Currency;
import smarkets.seto.SmarketsSetoPiqi.Decimal;
import smarkets.seto.SmarketsSetoPiqi.Uuid128;

public class AccountFundsTest {

	@Test
	public void shouldConstructAccountStateFromSetoPayload() {
		AccountFunds accountFunds = AccountFunds.fromSetoAccountState(accountState());
		assertThat(accountFunds.getCurrency(), is("GBP"));
		assertThat(accountFunds.getCash().amount, is(20.0));
		assertThat(accountFunds.getBonus().amount, is(20.1));
		assertThat(accountFunds.getExposure().amount, is(20.2));
	}

	@Test
	public void shouldConstructAccountStateByConstructor() {
		AccountFunds accountFunds = new AccountFunds("GBP", 20.0, 20.1, 20.2);
		assertThat(accountFunds.getCurrency(), is("GBP"));
		assertThat(accountFunds.getCash().amount, is(20.0));
		assertThat(accountFunds.getBonus().amount, is(20.1));
		assertThat(accountFunds.getExposure().amount, is(20.2));
	}

	private AccountState accountState() {
		return AccountState.newBuilder()
			.setAccount(Uuid128.newBuilder().setLow(34L).build())
			.setCurrency(Currency.CURRENCY_GBP)
			.setCash(Decimal.newBuilder().setValue(200000L).build())
			.setBonus(Decimal.newBuilder().setValue(201000L).build())
			.setExposure(Decimal.newBuilder().setValue(202000L).build())
			.build();
	}
}
