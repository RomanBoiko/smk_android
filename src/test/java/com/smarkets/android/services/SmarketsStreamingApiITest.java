package com.smarkets.android.services;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.Test;

import smarkets.seto.SmarketsSetoPiqi;

import com.smarkets.android.SmkConfig;

public class SmarketsStreamingApiITest {

	private final Logger log = Logger.getLogger(getClass());
	private SmkConfig smkConfig = new SmkConfig();

	@Test
	public void shouldLoginToSmk() throws UnknownHostException, IOException, InterruptedException {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		StreamingApiClient streamingApi = streamingApiClient(factory);
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		log.info("Login request:" + loginRequest.toString());
		SmarketsSetoPiqi.Payload loginResponse = streamingApi.getSmkResponse(loginRequest);
		log.info("Login response:" + loginResponse.toString());
		logoutFromSmk(streamingApi, factory);
		assertThat(loginResponse.getEtoPayload().getSeq(), is(1L));
		assertThat(loginResponse.toString(), containsString("type: PAYLOAD_LOGIN_RESPONSE"));
	}

	@Test
	public void shouldLogoutFromSmk() throws UnknownHostException, IOException, InterruptedException {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		StreamingApiClient streamingApi = loggedInSmkApi(factory);
		SmarketsSetoPiqi.Payload logoutRequest = factory.logoutRequest();
		log.info("Logout request:" + logoutRequest.toString());
		SmarketsSetoPiqi.Payload logoutResponse = streamingApi.getSmkResponse(logoutRequest);
		log.info("Logout response:" + logoutResponse.toString());
		assertThat(logoutResponse.toString(), containsString("reason: LOGOUT_CONFIRMATION"));
	}

	@Test
	public void shouldGetAccountState() throws UnknownHostException, IOException, InterruptedException {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		StreamingApiClient streamingApi = loggedInSmkApi(factory);
		SmarketsSetoPiqi.Payload accountStateRequest = factory.accountStateRequest();
		log.info("Account state request:" + accountStateRequest.toString());
		SmarketsSetoPiqi.Payload accountStateResponse = streamingApi.getSmkResponse(accountStateRequest);
		log.info("Account state response:" + accountStateResponse.toString());
		logoutFromSmk(streamingApi, factory);
		assertThat(accountStateResponse.toString(), containsString("currency: CURRENCY_GBP"));
		
	}

	@Test
	public void shouldRespondToHeartBeatRequestsPeriadicallyAndNotToBeLoggedOutAfterTimeout() throws Exception {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		StreamingApiClient streamingApi = loggedInSmkApi(factory);
		log.info("Pausing...");
		pause(30);
		log.info("Pause finished");
		SmarketsSetoPiqi.Payload accountStateRequest = factory.accountStateRequest();
		log.info("Account state request:" + accountStateRequest.toString());
		SmarketsSetoPiqi.Payload accountStateResponse = streamingApi.getSmkResponse(accountStateRequest);
		log.info("Account state response:" + accountStateResponse.toString());
		logoutFromSmk(streamingApi, factory);
		assertThat(accountStateResponse.toString(), containsString("currency: CURRENCY_GBP"));
		
	}

	private void pause(final int seconds) throws InterruptedException {
		Thread pauseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * seconds);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		pauseThread.start();
		pauseThread.join();
	}

	private StreamingApiClient loggedInSmkApi (StreamingApiRequestsFactory factory) throws UnknownHostException, IOException, InterruptedException {
		StreamingApiClient streamingApi = streamingApiClient(factory);
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		streamingApi.getSmkResponse(loginRequest);
		return streamingApi;
	}

	private StreamingApiClient streamingApiClient(StreamingApiRequestsFactory factory) throws UnknownHostException, IOException {
		return new StreamingApiClient(smkConfig.smkStreamingApiHost, smkConfig.smkStreamingApiPort, smkConfig.smkStreamingApiSslEnabled, factory);
	}
	
	private void logoutFromSmk(StreamingApiClient streamingApi, StreamingApiRequestsFactory factory) throws UnknownHostException, IOException, InterruptedException {
		log.info(streamingApi.getSmkResponse(factory.logoutRequest()));
	}
}
