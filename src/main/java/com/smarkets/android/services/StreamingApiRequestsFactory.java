package com.smarkets.android.services;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

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
	
	public Payload placeBetRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CREATE)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.setOrderCreate(builderForValue)
				.build();
	}
	
	
//	DEBUG:smarkets.session:buffering payload with outgoing sequence 10: type: PAYLOAD_ORDER_CREATE
//	order_create {
//	  type: ORDER_CREATE_LIMIT
//	  market {
//	    low: 409866
//	    high: 0
//	  }
//	  contract {
//	    low: 658059
//	    high: 0
//	  }
//	  side: SIDE_BUY
//	  quantity_type: QUANTITY_PAYOFF_CURRENCY
//	  quantity: 20000
//	  price_type: PRICE_PERCENT_ODDS
//	  price: 5000
//	}

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
