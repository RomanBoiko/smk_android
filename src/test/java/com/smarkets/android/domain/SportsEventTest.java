package com.smarkets.android.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SportsEventTest {

	@Test
	public void shouldParseDateFromUrl() {
		SmkEvent event = new SmkEvent("eventName", "events/sport/football/irish-fai-cup/2013/06/02/avondale-vs-sheriff-yc", null);
		assertThat(event.date, is("2013/06/02"));
	}

	@Test
	public void shouldUseDefaultDateIfEventDateNotFoundInUrl() {
		SmkEvent event = new SmkEvent("eventName", "events/sport/football/irish-fai-cup/", null);
		assertThat(event.date, is("1970/01/01"));
	}

}
