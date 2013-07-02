package com.smarkets.android.domain;

import static java.lang.String.format;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.smarkets.android.BusinessService;
import com.smarkets.android.BusinessService.Callback;

public class Bet {

	public final BetType type;
	public final Long marketId;
	public final Long contractId;
	public final UUID betId;
	public final BigDecimal quantity;
	public final BigDecimal price;
	public final Date createdDate;
	private String marketName;
	private String contractName;
	private final BusinessService smkService;

	public Bet(BetType type, Long marketId, Long contractId, UUID betId, BigDecimal quantity, BigDecimal price,
			Date createdDate, BusinessService smkService) {
		this.type = type;
		this.marketId = marketId;
		this.contractId = contractId;
		this.betId = betId;
		this.quantity = quantity;
		this.price = price;
		this.createdDate = createdDate;
		this.smkService = smkService;
	}


	public String marketName() {
		if (null == marketName) {
			marketName = smkService.marketNameForId(marketId);
		}
		return marketName;
	}

	public String contractName() {
		if (null == contractName) {
			contractName = smkService.contractNameForId(contractId);
		}
		return contractName;
	}

	public void cancel(final Callback<Boolean> action) throws IOException {
		this.smkService.cancelBet(this, action);
	}

	public String toString() {
		return format("%s-%s, %s for %s%%", type, marketName(), quantity, price);
	}

	public String toDetailedString() {
		return format("Bet: %s:\n" +
				"Market: %s(id=%s)\n" +
				"Contract: %s(id=%s)\n" +
				"Order: %s GBP for %s%%\n" +
				"Created at: %s", type, marketName(), marketId, contractName(), contractId, quantity, price, createdDate);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((betId == null) ? 0 : betId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bet other = (Bet) obj;
		return this.betId.equals(other.betId);
	}
}
