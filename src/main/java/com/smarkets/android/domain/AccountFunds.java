package com.smarkets.android.domain;

import static com.smarkets.android.domain.CashAmount.cashAmountWithCurrency;


import smarkets.seto.SmarketsSetoPiqi.AccountState;

public class AccountFunds {

	private String currency;
	private Double cash;
	private Double bonus;
	private Double exposure;

	public AccountFunds(String currency, Double cash, Double bonus, Double exposure) {
		this.currency = currency;
		this.cash = cash;
		this.bonus = bonus;
		this.exposure = exposure;
	}

	public static AccountFunds fromSetoAccountState(AccountState accountState) {
		return new AccountFunds(
				accountState.getCurrency().toString().replace("CURRENCY_", ""),
				smkCashAmountToReal(accountState.getCash().getValue()),
				smkCashAmountToReal(accountState.getBonus().getValue()),
				smkCashAmountToReal(accountState.getExposure().getValue()));
	}

	public String getCurrency() {
		return currency;
	}

	public CashAmount getCash() {
		return cashAmountWithCurrency(cash, currency);
	}

	public CashAmount getBonus() {
		return cashAmountWithCurrency(bonus, currency);
	}

	public CashAmount getExposure() {
		return cashAmountWithCurrency(exposure, currency);
	}
	
	private static Double smkCashAmountToReal(Long smkAmount) {
		return smkAmount * 0.0001;
	}
}
