package com.smarkets.android.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.smarkets.android.services.seto.StreamingApiRequestsFactory;

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
	public void shouldCreateHeartBeatResponse() throws InvalidProtocolBufferException {
		Payload heartbeatResponse = requestsFactory.heartbeatResponse();
		log.debug(heartbeatResponse);
		assertThat(heartbeatResponse.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO));
		assertThat(heartbeatResponse.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_HEARTBEAT));
		assertThat(heartbeatResponse.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldCreatePlaceBetRequest() throws InvalidProtocolBufferException {
		Payload placeBetRequest = requestsFactory.placeBetRequest(12L, 13L, 12.02, 31.09, true);
		log.debug(placeBetRequest);
		assertThat(placeBetRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CREATE));
		assertThat(placeBetRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldCreateCurrentBetsRequest() throws InvalidProtocolBufferException {
		Payload currentBetsRequest = requestsFactory.currentBetsRequest();
		log.debug(currentBetsRequest);
		assertThat(currentBetsRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDERS_FOR_ACCOUNT_REQUEST));
		assertThat(currentBetsRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldCreateCancelBetRequest() throws InvalidProtocolBufferException {
		Payload cancelBetRequest = requestsFactory.cancelBetRequest(UUID.randomUUID());
		log.debug(cancelBetRequest);
		assertThat(cancelBetRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ORDER_CANCEL));
		assertThat(cancelBetRequest.getEtoPayload().getSeq(), is(1L));
	}

	@Test
	public void shouldCreatePricesRequest() throws InvalidProtocolBufferException {
		Payload pricesRequest = requestsFactory.marketQuotesRequest(123L);
		log.debug(pricesRequest);
		assertThat(pricesRequest.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_MARKET_QUOTES_REQUEST));
		assertThat(pricesRequest.getEtoPayload().getSeq(), is(1L));
	}

	private Payload loginRequest() {
		return requestsFactory.loginRequest(USER, PASSWORD);
	}

}
