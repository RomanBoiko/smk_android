package com.smarkets.android.services.rest;

import java.io.IOException;
import java.net.MalformedURLException;

import com.smarkets.android.domain.SmkEvent;

public class RestApiClient {
	public static SmkEvent getEventsRoot() {
		return new SmkEvent("All", "/events", null);
	}

	public static String getMarketNameById(Long marketId) throws MalformedURLException, IOException {
		return JsonEventsSource.textViaHttp("/queries/market_by_id/" + marketId);
	}

	public static String getContractNameById(long contractId) throws MalformedURLException, IOException {
		return JsonEventsSource.textViaHttp("/queries/contract_by_id/" + contractId);
	}

}
