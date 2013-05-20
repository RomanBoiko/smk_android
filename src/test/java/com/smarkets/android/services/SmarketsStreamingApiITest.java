package com.smarkets.android.services;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.smarkets.android.SmkConfig;

import smarkets.seto.SmarketsSetoPiqi;

public class SmarketsStreamingApiITest {

	private final Logger log = Logger.getLogger(getClass());
	private SmkConfig smkConfig = new SmkConfig();
	private StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();

	@Test
	public void shouldLoginToSmk() throws UnknownHostException, IOException {
		StreamingApiClient streamingApi = streamingApiClient();
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		log.info("Login request:" + loginRequest.toString());
		SmarketsSetoPiqi.Payload loginResponse = streamingApi.getSmkResponse(loginRequest);
		log.info("Login response:" + loginResponse.toString());
		logoutFromSmk(streamingApi);
		assertThat(loginResponse.getEtoPayload().getSeq(), is(1L));
		assertThat(loginResponse.toString(), containsString("type: PAYLOAD_LOGIN_RESPONSE"));
	}

	@Test
	public void shouldLogoutFromSmk() throws UnknownHostException, IOException {
		StreamingApiClient streamingApi = loggedInSmkApi();
		SmarketsSetoPiqi.Payload logoutRequest = factory.logoutRequest();
		log.info("Logout request:" + logoutRequest.toString());
		SmarketsSetoPiqi.Payload logoutResponse = streamingApi.getSmkResponse(logoutRequest);
		log.info("Logout response:" + logoutResponse.toString());
		assertThat(logoutResponse.toString(), containsString("reason: LOGOUT_CONFIRMATION"));
	}

	@Test
	public void shouldGetAccountState() throws UnknownHostException, IOException {
		StreamingApiClient streamingApi = loggedInSmkApi();
		SmarketsSetoPiqi.Payload accountStateRequest = factory.accountStateRequest();
		log.info("Account state request:" + accountStateRequest.toString());
		SmarketsSetoPiqi.Payload accountStateResponse = streamingApi.getSmkResponse(accountStateRequest);
		log.info("Account state response:" + accountStateResponse.toString());
		logoutFromSmk(streamingApi);
		assertThat(accountStateResponse.toString(), containsString("currency: CURRENCY_GBP"));
		
	}

	@Test
	public void shouldGetFootballEvents() throws UnknownHostException, IOException {
		StreamingApiClient streamingApi = loggedInSmkApi();
		SmarketsSetoPiqi.Payload footballEventsRequest = factory.todaysFootballEventsRequest();
		log.info("Football events request:" + footballEventsRequest.toString());
		SmarketsSetoPiqi.Payload footballEventsResponse = streamingApi.getSmkResponse(footballEventsRequest);
		log.info("Football events response:" + footballEventsResponse.toString());
		logoutFromSmk(streamingApi);
		assertThat(footballEventsResponse.toString(), containsString("http_found {"));
		
//		SmarketsSetoPiqi.Payload footballEvents = streamingApi.getSmkResponseViaHttp(footballEventsResponse.getHttpFound().getUrl());
	}

	private StreamingApiClient loggedInSmkApi () throws UnknownHostException, IOException {
		StreamingApiClient streamingApi = streamingApiClient();
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		streamingApi.getSmkResponse(loginRequest);
		return streamingApi;
	}

	private StreamingApiClient streamingApiClient() throws UnknownHostException, IOException {
		return new StreamingApiClient(new Socket(smkConfig.smkStreamingApiHost, smkConfig.smkStreamingApiPort));
	}
	
	private void logoutFromSmk(StreamingApiClient streamingApi) throws UnknownHostException, IOException {
		streamingApi.getSmkResponse(factory.logoutRequest());
	}
}
