package com.smarkets.android.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class StreamingApiRequestsFactoryTest {

	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private final Logger log = Logger.getLogger(getClass());

	private StreamingApiRequestsFactory requestsFactory;

	@Before
	public void before() {
		requestsFactory = new StreamingApiRequestsFactory();
	}

	@Test
	public void shouldCreateSerializableAndRestorableFromByteStringPayload() throws InvalidProtocolBufferException {
		SmarketsSetoPiqi.Payload loginPayload = loginRequest();
		ByteString requestBytes = loginPayload.toByteString();
		SmarketsSetoPiqi.Payload payloadUncompressed = SmarketsSetoPiqi.Payload.parseFrom(requestBytes);
		assertThat(payloadUncompressed, is(loginPayload));
	}

	@Test
	public void shouldCreateSerializableAndRestorableFromByteArrayPayload() throws InvalidProtocolBufferException {
		SmarketsSetoPiqi.Payload loginPayload = loginRequest();
		byte[] requestBytes = loginPayload.toByteArray();
		SmarketsSetoPiqi.Payload payloadUncompressed = SmarketsSetoPiqi.Payload.parseFrom(requestBytes);
		assertThat(payloadUncompressed, is(loginPayload));
	}

	@Test
	public void shouldCreateRequestsWithSequentialNumbers() throws InvalidProtocolBufferException {
		assertThat(loginRequest().getEtoPayload().getSeq(), is(1L));
		assertThat(loginRequest().getEtoPayload().getSeq(), is(2L));
		assertThat(loginRequest().getEtoPayload().getSeq(), is(3L));
	}

	@Test
	public void shouldPutUserPasswordAndEtoPayloadIntoLoginRequest() throws InvalidProtocolBufferException {
		Payload loginRequest = loginRequest();
		log.debug(loginRequest);
		assertThat(loginRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_LOGIN));
		assertThat(loginRequest.getLogin().getUsername(), is(USER));
		assertThat(loginRequest.getLogin().getPassword(), is(PASSWORD));
		assertThat(loginRequest.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN));
		assertThat(loginRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldPutEtoPayloadIntoLogoutRequest() throws InvalidProtocolBufferException {
		Payload logoutRequest = requestsFactory.logoutRequest();
		log.debug(logoutRequest);
		assertThat(logoutRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO));
		assertThat(logoutRequest.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGOUT));
		assertThat(logoutRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldPutEtoPayloadIntoAccountStateRequest() throws InvalidProtocolBufferException {
		Payload accountStateRequest = requestsFactory.accountStateRequest();
		log.debug(accountStateRequest);
		assertThat(accountStateRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE_REQUEST));
		assertThat(accountStateRequest.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE));
		assertThat(accountStateRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldGenerateFootballEventsByDateRequest() throws InvalidProtocolBufferException {
		Payload eventsRequest = requestsFactory.todaysFootballEventsRequest();
		log.debug(eventsRequest);
		assertThat(eventsRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_EVENTS_REQUEST));
		assertThat(eventsRequest.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE));
		assertThat(eventsRequest.getEtoPayload().getSeq(), is(1L));
		assertThat(eventsRequest.getEventsRequest().getType(), is(SmarketsSetoPiqi.EventsRequestType.EVENTS_REQUEST_SPORT_BY_DATE));
		assertThat(eventsRequest.getEventsRequest().getContentType(), is(SmarketsSetoPiqi.ContentType.CONTENT_TYPE_PROTOBUF));
		assertThat(eventsRequest.getEventsRequest().getSportByDate().getType(), is(SmarketsSetoPiqi.SportByDateType.SPORT_BY_DATE_FOOTBALL));
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		assertThat(eventsRequest.getEventsRequest().getSportByDate().getDate(), is(SmarketsSetoPiqi.Date.newBuilder().setYear(year).setMonth(month).setDay(day).build()));
	}
//	type: PAYLOAD_EVENTS_REQUEST
//	events_request {
//	  type: EVENTS_REQUEST_SPORT_BY_DATE
//	  content_type: CONTENT_TYPE_PROTOBUF
//	  sport_by_date {
//	    type: SPORT_BY_DATE_FOOTBALL
//	    date {
//	      year: 2013
//	      month: 5
//	      day: 16
//	    }
//	  }
//	}

	private Payload loginRequest() {
		return requestsFactory.loginRequest(USER, PASSWORD);
	}

}
