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
	private static final SmkConfig config;
	private static StreamingApiRequestsFactory requestFactory;
	private static StreamingApiClient apiClient;
	private static boolean loggedIn = false;

	static {
		config = new SmkConfig();
	}
	
	public interface Callback<T> {
		void action(T response);
	}

	public static void login(String username, String password, final Callback<LoginResult> action) throws IOException {
		requestFactory = new StreamingApiRequestsFactory();
		apiClient = new StreamingApiClient(
				config.smkStreamingApiHost(),
				config.smkStreamingApiPort(),
				config.smkStreamingApiSslEnabled(),
				requestFactory);
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

	public static void logout(final Callback<Boolean> action) throws IOException {
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

	public static void getAccountStatus(final Callback<AccountFunds> action) throws IOException {
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


	public static void currentBets(final Callback<List<Bet>> action) throws IOException {
		apiClient.request(requestFactory.currentBetsRequest(), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				List<Bet> currentBets = new LinkedList<Bet>();
				if(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDERS_FOR_ACCOUNT)) {
					for (SmarketsSetoPiqi.orders_for_market market : response.getOrdersForAccount().getMarketsList()) {
						for (SmarketsSetoPiqi.orders_for_contract contract : market.getContractsList()) {
							for (SmarketsSetoPiqi.orders_for_price bid : contract.getBidsList()) {
								BigDecimal price = smkPrice(bid.getPrice());
								for (SmarketsSetoPiqi.OrderState order : bid.getOrdersList()) {
									currentBets.add(new Bet(BetType.BUY,
											market.getMarket().getLow(),
											contract.getContract().getLow(),
											SetoUuid.toUuid(order.getOrder()),
											smkQuantity(order.getQuantity()),
											price,
											smkDate(order.getCreatedMicroseconds())));
								}
							}
							for (SmarketsSetoPiqi.orders_for_price offer : contract.getOffersList()) {
								BigDecimal price = smkPrice(offer.getPrice());
								for (SmarketsSetoPiqi.OrderState order : offer.getOrdersList()) {
									currentBets.add(new Bet(BetType.SELL,
											market.getMarket().getLow(),
											contract.getContract().getLow(),
											SetoUuid.toUuid(order.getOrder()),
											smkQuantity(order.getQuantity()),
											price,
											smkDate(order.getCreatedMicroseconds())));
								}
							}
						}
						
					}
				}
				action.action(currentBets);
			}
		});
	}

	public static void placeBet(BetType type, Long marketId, Long contractId, BigDecimal quantity, BigDecimal price, final Callback<PlaceBetResult> action) throws IOException {
		apiClient.request(requestFactory.placeBetRequest(marketId, contractId, quantity.doubleValue(), price.doubleValue(), (BetType.BUY.equals(type))), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				action.action(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_ACCEPTED)
						? PlaceBetResult.BET_PLACED
						: PlaceBetResult.BET_ERROR);
			}
		});
	}

	public static void cancelBet(Bet bet, final Callback<Boolean> action) throws IOException {
		apiClient.request(requestFactory.cancelBetRequest(bet.betId), new StreamingCallback() {

			@Override
			public void process(Payload response) {
				action.action(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CANCELLED));
			}
		});
	}

	public static void currentPricesForMarket(Long marketId, final Callback<String> action) throws IOException {
		apiClient.request(requestFactory.marketQuotesRequest(marketId), new StreamingCallback() {
			
			@Override
			public void process(Payload response) {
				StringBuffer marketQuotes = new StringBuffer();
				if(response.getType().equals(SmarketsSetoPiqi.PayloadType.PAYLOAD_MARKET_QUOTES)) {
					for (SmarketsSetoPiqi.ContractQuotes contractQuotes : response.getMarketQuotes().getContractQuotesList()) {
						try {
							marketQuotes.append("Contract "+BusinessService.contractNameForId(contractQuotes.getContract().getLow()) +"[\n");
						} catch (Exception e) {
							e.printStackTrace();
							marketQuotes.append("Contract " + contractQuotes.getContract().getLow() + "[\n");
						}
						marketQuotes.append(" Bids {\n");
						for (SmarketsSetoPiqi.Quote bid : contractQuotes.getBidsList()) {
							marketQuotes.append("  " + smkQuantity(bid.getQuantity()) + "(" + smkPrice(bid.getPrice()) + ")%\n");
						}
						marketQuotes.append("}\n Offers {\n");
						for (SmarketsSetoPiqi.Quote offer : contractQuotes.getOffersList()) {
							marketQuotes.append(smkQuantity(offer.getQuantity()) + "(" + smkPrice(offer.getPrice()) + ")%\n");
						}
						marketQuotes.append("}]\n");
					}
				}
				action.action(marketQuotes.toString());
			}
		});
	}

	public static String contractNameForId(Long contractId) throws Exception {
		return RestApiClient.getContractNameById(contractId);
	}
	
	public static String marketNameForId(Long marketId) throws Exception {
		return RestApiClient.getMarketNameById(marketId);
	}

	private static BigDecimal smkQuantity(int quantity) {
		return new BigDecimal(quantity/10000);
	}
	private static BigDecimal smkPrice(int price) {
		return new BigDecimal(price/100);
	}
	private static Date smkDate(long smkTime) {
		return new Date((long)smkTime/1000);
	}
}