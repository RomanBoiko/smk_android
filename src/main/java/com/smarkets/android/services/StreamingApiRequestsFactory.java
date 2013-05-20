package com.smarkets.android.services;

import java.util.Calendar;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.ContentType;
import smarkets.seto.SmarketsSetoPiqi.Date;
import smarkets.seto.SmarketsSetoPiqi.EventsRequestType;
import smarkets.seto.SmarketsSetoPiqi.Payload;
import smarkets.seto.SmarketsSetoPiqi.SportByDate;
import smarkets.seto.SmarketsSetoPiqi.SportByDateType;

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

	public Payload todaysFootballEventsRequest() {
		Calendar calendar = Calendar.getInstance();
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_EVENTS_REQUEST)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.setEventsRequest(
						SmarketsSetoPiqi.EventsRequest.newBuilder()
							.setType(EventsRequestType.EVENTS_REQUEST_SPORT_BY_DATE)
							.setContentType(ContentType.CONTENT_TYPE_PROTOBUF)
							.setSportByDate(
									SportByDate.newBuilder()
										.setType(SportByDateType.SPORT_BY_DATE_FOOTBALL)
										.setDate(Date.newBuilder()
												.setYear(calendar.get(Calendar.YEAR))
												.setMonth(calendar.get(Calendar.MONTH) + 1)
												.setDay(calendar.get(Calendar.DATE))
												.build())
									.build())
						.build())
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
