package com.smarkets.android.services;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.junit.Test;

import com.smarkets.android.domain.Events;

public class RestApiIntegrationTest {

	@Test
	public void shouldFetchEventsData() throws IOException, JSONException {

		URL url = new URL("https://api.smarkets.com/events/sport/football");
		URLConnection conn = url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String inputLine;
		StringBuffer jsonString = new StringBuffer();

		while ((inputLine = br.readLine()) != null) {
			jsonString.append(inputLine);
		}
		br.close();
		System.out.println(jsonString.toString());
		
		Events footballEvents = Events.fromJson(jsonString.toString());
		assertThat(footballEvents.tournaments().size(), is(greaterThan(0)));
	}
}
