package com.smarkets.android.domain;

import static com.smarkets.android.domain.CashAmount.cashAmountWithCurrency;


import smarkets.seto.SmarketsSetoPiqi.AccountState;

public class AccountFunds {

	private AccountState accountState;
	private String currency;

	public AccountFunds(AccountState accountState) {
		this.accountState = accountState;
		this.currency = accountState.getCurrency().toString().replace("CURRENCY_", "");
	}

	public String getCurrency() {
		return currency;
	}

	public CashAmount getCash() {
		return cashAmountWithCurrency(smkCashAmountToReal(accountState.getCash().getValue()), currency);
	}

	public CashAmount getBonus() {
		return cashAmountWithCurrency(smkCashAmountToReal(accountState.getBonus().getValue()), currency);
	}

	public CashAmount getExposure() {
		return cashAmountWithCurrency(smkCashAmountToReal(accountState.getExposure().getValue()), currency);
	}
	
	private Double smkCashAmountToReal(Long smkAmount) {
		return smkAmount * 0.0001;
	}
}
