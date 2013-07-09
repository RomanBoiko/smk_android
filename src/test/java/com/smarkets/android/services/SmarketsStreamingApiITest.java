package com.smarkets.android.services;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Test;

import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.smarkets.android.SmkConfig;
import com.smarkets.android.services.seto.StreamingApiClient;
import com.smarkets.android.services.seto.StreamingApiClientReal;
import com.smarkets.android.services.seto.StreamingApiRequestsFactory;
import com.smarkets.android.services.seto.StreamingCallback;

public class SmarketsStreamingApiITest {

	private final Logger log = Logger.getLogger(getClass());
	private SmkConfig smkConfig = new SmkConfig();
	private static final String SMK_TEST_USER_LOGIN = System.getProperty("smk.user");
	private static final String SMK_TEST_USER_PASSWORD = System.getProperty("smk.pass");

	@Test
	public void shouldRunSetOfStreamingApiRequests() throws UnknownHostException, IOException, InterruptedException {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		log.info("============LOGIN===============");
		StreamingApiClient streamingApi = loggedInSmkApi(factory);
		pause(2);

		log.info("============AccountState===============");
		streamingApi.request(factory.accountStateRequest(), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				log.info("Account state response got: " + response);
			}
		});
		pause(2);

		log.info("============CurrentBets===============");
		streamingApi.request(factory.currentBetsRequest(), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				log.info("Current bets response: " + response);
			}
		});
		pause(2);

		log.info("============PlaceBet===============");
		streamingApi.request(factory.placeBetRequest(23422, 234232, 2.3, 11.2, true), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				log.info("Order create response: " + response);
			}
		});
		pause(2);

		log.info("============CancelBet===============");
		streamingApi.request(factory.cancelBetRequest(UUID.randomUUID()), new StreamingCallback() {
			@Override
			public void process(Payload response) {
				log.info("Order cancel response: " + response);
			}
		});
		pause(2);

//		log.info("============MarketPrices===============");
//		streamingApi.request(factory.pricesRequest(123L), new SmkCallback() {
//			@Override
//			public void process(Payload response) {
//				log.info("Market prices response: " + response);
//			}
//		});
//		pause(2);

		log.info("============HeartBeats===============");
		log.info("Pausing to test heartbeats...");
		pause(15);

		log.info("============Logout===============");
		logoutFromSmk(streamingApi, factory);
		pause(2);
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
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(SMK_TEST_USER_LOGIN, SMK_TEST_USER_PASSWORD);
		streamingApi.request(loginRequest, new StreamingCallback() {
			public void process(Payload response) {
				log.info("Login response got: " + response);
			}
		});
		return streamingApi;
	}

	private StreamingApiClient streamingApiClient(StreamingApiRequestsFactory factory) throws UnknownHostException, IOException {
		return new StreamingApiClientReal(smkConfig.smkStreamingApiHost(), smkConfig.smkStreamingApiPort(), smkConfig.smkStreamingApiSslEnabled(), factory);
	}
	
	private void logoutFromSmk(StreamingApiClient streamingApi, StreamingApiRequestsFactory factory) throws UnknownHostException, IOException, InterruptedException {
		streamingApi.request(factory.logoutRequest(), new StreamingCallback() {
			public void process(Payload response) {
				log.info("Logout response got: " + response);
			}
		});
	}
}
