package com.smarkets.android;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.smarkets.android.domain.AccountFunds;
import com.smarkets.android.domain.Bet;
import com.smarkets.android.domain.BetType;
import com.smarkets.android.domain.actionresults.LoginResult;
import com.smarkets.android.domain.actionresults.PlaceBetResult;
import com.smarkets.android.services.rest.RestApiClient;
import com.smarkets.android.services.seto.SetoUuid;
import com.smarkets.android.services.seto.StreamingApiClient;
import com.smarkets.android.services.seto.StreamingApiRequestsFactory;
import com.smarkets.android.services.seto.StreamingCallback;

public class BusinessService {
	private final SmkConfig config;
	private final StreamingApiRequestsFactory requestFactory;
	private final StreamingApiClient apiClient;
	private boolean loggedIn = false;

	public BusinessService() throws IOException {
		this.config = new SmkConfig();
		this.requestFactory = new StreamingApiRequestsFactory();
		this.apiClient = new StreamingApiClient(
			config.smkStreamingApiHost(),
			config.smkStreamingApiPort(),
			config.smkStreamingApiSslEnabled(),
			requestFactory);
	}
	
	public interface Callback<T> {
		void action(T response);
	}

	public void login(String username, String password, final Callback<LoginResult> action) throws IOException {
		apiClient.request(requestFactory.loginRequest(username, password), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				if (response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO) &&
						response.getEtoPayload().getType().equals(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN_RESPONSE)) {
					action.action(LoginResult.LOGIN_SUCCESS);
					loggedIn = true;
				} else {
					action.action(LoginResult.LOGIN_FAILURE);
				}
			}
		});
	}

	public void logout(final Callback<Boolean> action) throws IOException {
		if(loggedIn) {
			apiClient.request(requestFactory.logoutRequest(), new StreamingCallback() {
				@Override
				public void process(Payload response) {
					if (response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO) &&
							response.getEtoPayload().getType().equals(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGOUT)) {
						action.action(true);
					} else {
						action.action(false);
					}
				}
			});
		}
	}

	public void getAccountStatus(final Callback<AccountFunds> action) throws IOException {
		apiClient.request(requestFactory.accountStateRequest(), new StreamingCallback() {

			@Override
			public void process(Payload response) {
				if (response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE)) {
					action.action(AccountFunds.fromSetoAccountState(response.getAccountState()));
				} else {
					//account state not received
				}
			}});
	}


	public void currentBets(final Callback<List<Bet>> action) throws IOException {
		apiClient.request(requestFactory.currentBetsRequest(), new StreamingCallback() {
			
			@Override
			public void process(Payload response) {
				List<Bet> currentBets = new LinkedList<Bet>();
				if(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDERS_FOR_ACCOUNT)) {
					for (SmarketsSetoPiqi.orders_for_market market : response.getOrdersForAccount().getMarketsList()) {
						for (SmarketsSetoPiqi.orders_for_contract contract : market.getContractsList()) {
							for (SmarketsSetoPiqi.orders_for_price bid : contract.getBidsList()) {
								double price = bid.getPrice() / 100;
								for (SmarketsSetoPiqi.OrderState order : bid.getOrdersList()) {
									currentBets.add(new Bet(BetType.BUY, market.getMarket().getLow(), contract.getContract().getLow(), SetoUuid.toUuid(order.getOrder()), new BigDecimal(order.getQuantity()/10000), new BigDecimal(price), new Date((long)order.getCreatedMicroseconds()/1000), BusinessService.this));
								}
							}
							for (SmarketsSetoPiqi.orders_for_price offer : contract.getOffersList()) {
								double price = offer.getPrice() / 100;
								for (SmarketsSetoPiqi.OrderState order : offer.getOrdersList()) {
									currentBets.add(new Bet(BetType.SELL, market.getMarket().getLow(), contract.getContract().getLow(), SetoUuid.toUuid(order.getOrder()), new BigDecimal(order.getQuantity()/10000), new BigDecimal(price), new Date((long)order.getCreatedMicroseconds()/1000), BusinessService.this));
								}
							}
						}
						
					}
				}
				action.action(currentBets);
			}
		});
	}

	public void placeBet(BetType type, Long marketId, Long contractId, BigDecimal quantity, BigDecimal price, final Callback<PlaceBetResult> action) throws IOException {
		apiClient.request(requestFactory.placeBetRequest(marketId, contractId, quantity.doubleValue(), price.doubleValue(), (BetType.BUY.equals(type))), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				action.action(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_ACCEPTED)
						? PlaceBetResult.BET_PLACED
						: PlaceBetResult.BET_ERROR);
			}
		});
	}

	public void cancelBet(Bet bet, final Callback<Boolean> action) throws IOException {
		apiClient.request(requestFactory.cancelBetRequest(bet.betId), new StreamingCallback() {

			@Override
			public void process(Payload response) {
				action.action(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CANCELLED));
			}
		});
	}

	public String contractNameForId(Long contractId) throws Exception {
		return RestApiClient.getContractNameById(contractId);
	}
	
	public String marketNameForId(Long marketId) throws Exception {
		return RestApiClient.getMarketNameById(marketId);
	}
}