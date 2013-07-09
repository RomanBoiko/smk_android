package com.smarkets.android.domain;

import static com.smarkets.android.domain.BetType.BUY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.smarkets.android.BusinessService;

public class BetTest {

	private Bet testBet;
	
	public BetTest() throws IOException {
		this.testBet = new Bet(BUY, 111L, 112L, new UUID(12L,23L), new BigDecimal("12.23"), new BigDecimal("25.0"), new Date(1364927290366L), new BusinessService());
	}

	@Test
	public void shouldShowInformativeToString() {
		assertThat(testBet.toDetailedString(),
			is("Bet: BUY:\nMarket: MarketToBeFetched(id=111)\nContract: ContractToBeFetched(id=112)\nOrder: 12.23 GBP for 25.0%\nCreated at: Tue Apr 02 19:28:10 BST 2013"));
	}
	@Test
	public void shouldShowShortToString() {
		assertThat(testBet.toString(),
				is("BUY-MarketToBeFetched, 12.23 for 25.0%"));
	}

}
