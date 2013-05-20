package com.smarkets.android.services;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
//import org.junit.Test;

import com.smarkets.android.domain.Events;
import com.smarkets.android.domain.SportsEvent;
import com.smarkets.android.domain.Tournament;

public class RestApiITest {

	//Long running test(goes through full tree of events) 
	//execute only manually for finding corrupted events
//	@Test
	public void shouldFetchEventsData() throws IOException, JSONException {

		Events footballEvents = new Events("Football", "/events/sport/football");
		assertThat(footballEvents.getTournaments().size(), is(greaterThan(0)));
		for (Tournament tournament : footballEvents.getTournaments()) {
			List<SportsEvent> sportEvents = tournament.getTournamentEvents();
			for (SportsEvent event : sportEvents) {
				assertThat(event.getMarkets(), is(notNullValue()));
			}
		}
	}
}
