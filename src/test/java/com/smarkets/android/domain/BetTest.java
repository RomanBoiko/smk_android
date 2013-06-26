package com.smarkets.android.domain;

import static com.smarkets.android.domain.BetType.BUY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.smarkets.android.SmkStreamingService;

public class BetTest {

	private static final Bet TEST_BET = new Bet(BUY, 111L, 112L, 113L, new BigDecimal("12.23"), new BigDecimal("25.0"), new Date(1364927290366L), new SmkStreamingService());
	
	@Test
	public void shouldShowInformativeToString() {
		assertThat(TEST_BET.toDetailedString(),
			is("Bet: BUY (id=113):\nMarket: MarketNameToBeFetched(id=111)\nContract: ContractNameToBeFetched(id=112)\nOrder: 12.23 GBP for 25.0%\nCreated at: Tue Apr 02 19:28:10 BST 2013"));
	}
	@Test
	public void shouldShowShortToString() {
		assertThat(TEST_BET.toString(),
				is("BUY-MarketNameToBeFetched, 12.23 for 25.0%"));
	}

}
