package com.smarkets.android;

import static com.smarkets.android.domain.BetType.BUY;
import static com.smarkets.android.domain.BetType.SELL;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import smarkets.seto.SmarketsSetoPiqi.AccountState;
import smarkets.seto.SmarketsSetoPiqi.Currency;
import smarkets.seto.SmarketsSetoPiqi.Decimal;
import smarkets.seto.SmarketsSetoPiqi.Uuid128;

import com.smarkets.android.domain.AccountFunds;
import com.smarkets.android.domain.Bet;
import com.smarkets.android.domain.BetType;

public class SmkStreamingService {

	private Long betSequence = 1012L;
	private final Set<Bet> bets = new HashSet<Bet>(Arrays.asList(
			new Bet(BUY, 211L, 212L, 213L, new BigDecimal("23.12"), new BigDecimal("30.0"), new Date(), this),
			new Bet(SELL, 311L, 312L, 313L, new BigDecimal("15.13"), new BigDecimal("40.0"), new Date(), this)));

	public boolean login(String username, String password) {
		return username.equals("u") && password.equals("p");
	}

	public AccountFunds getAccountStatus() throws UnknownHostException, IOException {
//		SmkConfig smkConfig = new SmkConfig();
//		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
//		StreamingApiClient apiClient = new StreamingApiClient(new Socket(smkConfig.smkStreamingApiHost,
//				smkConfig.smkStreamingApiPort));
//		apiClient.getSmkResponse(factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword));
//		SmarketsSetoPiqi.Payload accountStateResponse = apiClient.getSmkResponse(factory.accountStateRequest());
//		apiClient.getSmkResponse(factory.logoutRequest());
//		return new AccountFunds(accountStateResponse.getAccountState());
		AccountState state = AccountState.newBuilder()
				.setAccount(Uuid128.newBuilder().setLow(34L).build())
				.setCurrency(Currency.CURRENCY_GBP)
				.setCash(Decimal.newBuilder().setValue(200000L).build())
				.setBonus(Decimal.newBuilder().setValue(201000L).build())
				.setExposure(Decimal.newBuilder().setValue(202000L).build())
				.build();
		return new AccountFunds(state);
	}

	public String contractNameForId(Long contractId) {
		return "ContractNameToBeFetched";
	}

	public String marketNameForId(Long marketId) {
		return "MarketNameToBeFetched";
	}

	public List<Bet> currentBets() {
		return new ArrayList<Bet>(bets);
	}

	public Bet placeBet(BetType type, Long marketId, Long contractId, BigDecimal quantity, BigDecimal price) {
		Bet placeBetResult = new Bet(type, marketId, contractId, betSequence++, quantity, price, new Date(), this);
		bets.add(placeBetResult);
		return placeBetResult;
	}

	public boolean cancelBet(Bet bet) {
		return bets.remove(bet);
	}
}