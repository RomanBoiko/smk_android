package com.smarkets.android.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SportsEventTest {

	@Test
	public void shouldParseDateFromUrl() {
		SportsEvent event = new SportsEvent("eventName", "events/sport/football/irish-fai-cup/2013/06/02/avondale-vs-sheriff-yc");
		assertThat(event.date, is("2013/06/02"));
	}

	@Test
	public void shouldUseDefaultDateIfEventDateNotFoundInUrl() {
		SportsEvent event = new SportsEvent("eventName", "events/sport/football/irish-fai-cup/");
		assertThat(event.date, is("1970/01/01"));
	}

}
