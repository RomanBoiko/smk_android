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

	private SmarketsEtoPiqi.Payload etoPayload(SmarketsEtoPiqi.PayloadType etoPayloadType) {
		return SmarketsEtoPiqi.Payload.newBuilder()
				.setType(etoPayloadType)
				.setSeq(conversationSequence.nextValue())
				.build();
	}

	private class EtoSequence {
		private long currentValue = 0;
		public long nextValue() {
			return ++currentValue;
		}
	}

}
