package com.smarkets.android.services;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.Test;

import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.smarkets.android.SmkConfig;

public class SmarketsStreamingApiITest {

	private final Logger log = Logger.getLogger(getClass());
	private SmkConfig smkConfig = new SmkConfig();

	@Test
	public void shouldGetAccountState() throws UnknownHostException, IOException, InterruptedException {
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		log.info("============LOGIN===============");
		StreamingApiClient streamingApi = loggedInSmkApi(factory);
		pause(2);

		log.info("============AccountState===============");
		streamingApi.request(factory.accountStateRequest(), new SmkCallback() {
			@Override
			public void process(Payload response) {
				log.info("Account state response got: " + response);
			}
		});
		pause(2);

		log.info("============CurrentBets===============");
		streamingApi.request(factory.currentBetsRequest(), new SmkCallback() {
			@Override
			public void process(Payload response) {
				log.info("Current bets response: " + response);
			}
		});

		pause(2);
		log.info("============PlaceBet===============");
		streamingApi.request(factory.placeBetRequest(23422, 234232, 2.3, 11.2, true), new SmkCallback() {
			@Override
			public void process(Payload response) {
				log.info("Order create response: " + response);
			}
		});
		pause(2);

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
		SmarketsSetoPiqi.Payload loginRequest = factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword);
		streamingApi.request(loginRequest, new SmkCallback() {
			public void process(Payload response) {
				log.info("Login response got: " + response);
			}
		});
		return streamingApi;
	}

	private StreamingApiClient streamingApiClient(StreamingApiRequestsFactory factory) throws UnknownHostException, IOException {
		return new StreamingApiClient(smkConfig.smkStreamingApiHost, smkConfig.smkStreamingApiPort, smkConfig.smkStreamingApiSslEnabled, factory);
	}
	
	private void logoutFromSmk(StreamingApiClient streamingApi, StreamingApiRequestsFactory factory) throws UnknownHostException, IOException, InterruptedException {
		streamingApi.request(factory.logoutRequest(), new SmkCallback() {
			public void process(Payload response) {
				log.info("Logout response got: " + response);
			}
		});
	}
}
