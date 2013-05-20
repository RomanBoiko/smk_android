package com.smarkets.android.services;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

public class JsonEventsSourceITest {

	@Test
	public void shouldRetrieveAndParseJsonContentFromHttpSource() throws JSONException, IOException {
		JsonEventsSource jsonEventsSource = JsonEventsSource.fetchViaHttp("https://api.smarkets.com/events/sport/football");
		assertThat(jsonEventsSource.getChildEventsAsJsonArray().length(), greaterThan(0));
	}
}
