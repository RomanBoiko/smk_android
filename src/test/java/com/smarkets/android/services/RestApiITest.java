package com.smarkets.android.services;

import static java.lang.String.format;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.smarkets.android.LongRunningActionAlert;
import com.smarkets.android.domain.SmkContract;
import com.smarkets.android.domain.SmkEvent;
import com.smarkets.android.domain.SmkMarket;
import com.smarkets.android.services.rest.RestApiClient;

@RunWith(MockitoJUnitRunner.class)
public class RestApiITest {

	private final Logger log = Logger.getLogger(getClass());

	@Mock LongRunningActionAlert alert;

	@Test
	public void shouldFetchEventsData() throws IOException, JSONException {
		walkThroughEventChildren(RestApiClient.getEventsRoot());
	}
	
	private void walkThroughEventChildren(SmkEvent parentEvent) throws JSONException, IOException {
		List<SmkEvent> children = parentEvent.getChildren(alert);
		log.info(format("REST Events: url=%s, children=%s", parentEvent.url, children));
		if (children.isEmpty()) {
			List<SmkMarket> markets = parentEvent.getMarkets(alert);
			log.info(format("REST Markets: %s", markets));
			assertThat(markets.size(), is(greaterThan(1)));
			SmkMarket market = markets.get(1);
			List<SmkContract> contracts = market.getContracts();
			log.info(format("REST Contracts: %s", contracts));
			assertThat(contracts.size(), is(greaterThan(0)));
		} else {
			walkThroughEventChildren(children.get(1));
		}
	}

	@Test
	public void shouldFetchMarketNameById() throws IOException, JSONException {
		assertThat(RestApiClient.getMarketNameById(128525L), is("Asian handicap Reading FC +4.00"));
	}

	@Test
	public void shouldFetchContractNameById() throws IOException, JSONException {
		assertThat(RestApiClient.getContractNameById(185416L), is("Reading FC +4.00"));
	}
}
