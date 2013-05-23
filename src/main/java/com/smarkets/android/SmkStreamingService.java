package com.smarkets.android;

import java.io.IOException;
import java.net.UnknownHostException;

import smarkets.seto.SmarketsSetoPiqi.AccountState;
import smarkets.seto.SmarketsSetoPiqi.Currency;
import smarkets.seto.SmarketsSetoPiqi.Decimal;
import smarkets.seto.SmarketsSetoPiqi.Uuid128;

import com.smarkets.android.domain.AccountFunds;

public class SmkStreamingService {
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
}