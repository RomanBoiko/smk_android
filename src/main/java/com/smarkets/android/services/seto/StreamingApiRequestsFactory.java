package com.smarkets.android.services.seto;

import java.util.UUID;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.OrderCreateType;
import smarkets.seto.SmarketsSetoPiqi.Payload;
import smarkets.seto.SmarketsSetoPiqi.PriceType;
import smarkets.seto.SmarketsSetoPiqi.QuantityType;
import smarkets.seto.SmarketsSetoPiqi.Side;

public class StreamingApiRequestsFactory {
	private EtoSequence conversationSequence;

	public StreamingApiRequestsFactory() {
		conversationSequence = new EtoSequence();
	}

	public SmarketsSetoPiqi.Payload loginRequest(String username, String password) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_LOGIN)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN))
				.setLogin(SmarketsSetoPiqi.Login.newBuilder().setUsername(username).setPassword(password).build())
				.build();
	}

	public Payload logoutRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGOUT))
				.build();
	}

	public Payload accountStateRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE_REQUEST)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.build();
	}
	
	public Payload placeBetRequest(long marketId, long contractId, double quantity, double price, boolean toBuy) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CREATE)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.setOrderCreate(SmarketsSetoPiqi.OrderCreate.newBuilder()
						.setType(OrderCreateType.ORDER_CREATE_LIMIT)
						.setMarket(SetoUuid.fromLowLong(marketId))
						.setContract(SetoUuid.fromLowLong(contractId))
						.setSide(toBuy ? Side.SIDE_BUY : Side.SIDE_SELL)
						.setQuantityType(QuantityType.QUANTITY_PAYOFF_CURRENCY)
						.setQuantity(new Double(quantity * 10000).intValue())
						.setPriceType(PriceType.PRICE_PERCENT_ODDS)
						.setPrice(new Double(price * 100).intValue())
						.build())
				.build();
	}

	public Payload cancelBetRequest(UUID betIdToCancel) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CANCEL)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.setOrderCancel(SmarketsSetoPiqi.OrderCancel.newBuilder()
						.setOrder(SetoUuid.fromUuid(betIdToCancel))
						.build())
				.build();
	}

	public Payload pricesRequest(long marketId) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_MARKET_QUOTES_REQUEST)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.setMarketQuotesRequest(SmarketsSetoPiqi.MarketQuotesRequest.newBuilder()
						.setMarket(SetoUuid.fromLowLong(marketId))
						.build())
				.build();
	}

	public Payload currentBetsRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDERS_FOR_ACCOUNT_REQUEST)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.build();
	}


	private SmarketsEtoPiqi.Payload etoPayload(SmarketsEtoPiqi.PayloadType etoPayloadType) {
		return SmarketsEtoPiqi.Payload.newBuilder()
				.setType(etoPayloadType)
				.setSeq(conversationSequence.nextValue())
				.build();
	}

	public Payload heartbeatResponse() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_HEARTBEAT))
				.build();
	}

	private static class EtoSequence {
		private long currentValue = 0;
		public synchronized long nextValue() {
			return ++currentValue;
		}
	}

}
