package com.smarkets.android;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.Test;

import smarkets.seto.SmarketsSetoPiqi;

public class SmarketsStreamingApiIntegrationTest {

	private SmkConfig smkConfig = new SmkConfig();
	private Logger log = Logger.getLogger(getClass());

	@Test
	public void shouldLoginToSmk() throws UnknownHostException, IOException {
		SmkStreamingApi streamingApi = new SmkStreamingApi();
		SmarketsSetoPiqi.Payload loginRequest = streamingApi.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		log.info("Login request:" + loginRequest.toString());
		SmarketsSetoPiqi.Payload loginResponse = streamingApi.getSmkResponse(loginRequest);
		log.info("Login response:" + loginResponse.toString());
		assertThat(loginResponse.getEtoPayload().getSeq(), is(1L));
		assertThat(loginResponse.toString(), containsString("type: PAYLOAD_LOGIN_RESPONSE"));
		logoutFromSmk(streamingApi);
	}

	@Test
	public void shouldLogoutFromSmk() throws UnknownHostException, IOException {
		SmkStreamingApi streamingApi = loggedInSmkApi();
		SmarketsSetoPiqi.Payload logoutRequest = streamingApi.logoutRequest();
		log.info("Logout request:" + logoutRequest.toString());
		SmarketsSetoPiqi.Payload logoutResponse = streamingApi.getSmkResponse(logoutRequest);
		log.info("Logout response:" + logoutResponse.toString());
		assertThat(logoutResponse.toString(), containsString("reason: LOGOUT_CONFIRMATION"));
	}

	@Test
	public void shouldGetAccountState() throws UnknownHostException, IOException {
		SmkStreamingApi streamingApi = loggedInSmkApi();
		SmarketsSetoPiqi.Payload accountStateRequest = streamingApi.accountStateRequest();
		log.info("Account state request:" + accountStateRequest.toString());
		SmarketsSetoPiqi.Payload accountStateResponse = streamingApi.getSmkResponse(accountStateRequest);
		log.info("Account state response:" + accountStateResponse.toString());
		logoutFromSmk(streamingApi);
	}

	private SmkStreamingApi loggedInSmkApi () throws UnknownHostException, IOException {
		SmkStreamingApi streamingApi = new SmkStreamingApi();
		SmarketsSetoPiqi.Payload loginRequest = streamingApi.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		streamingApi.getSmkResponse(loginRequest);
		return streamingApi;
	}
	
	private void logoutFromSmk(SmkStreamingApi streamingApi) throws UnknownHostException, IOException {
		streamingApi.getSmkResponse(streamingApi.logoutRequest());
	}
}
