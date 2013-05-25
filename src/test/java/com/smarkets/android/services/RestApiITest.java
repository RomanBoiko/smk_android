package com.smarkets.android.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.smarkets.android.domain.SmkEvent;

public class RestApiITest {

	//Long running test(goes through full tree of events) 
	//execute only manually for finding corrupted events
//	@Test
	public void shouldFetchEventsData() throws IOException, JSONException {
		walkThroughEventChildren(RestApiClient.getEventsRoot());
	}
	
	private void walkThroughEventChildren(SmkEvent parentEvent) throws JSONException, IOException {
		List<SmkEvent> children = parentEvent.getChildren();
		if (children.isEmpty()) {
			assertThat(parentEvent.getMarkets(), is(notNullValue()));
		}
		for (SmkEvent child : children) {
			walkThroughEventChildren(child);
		}
	}
}
