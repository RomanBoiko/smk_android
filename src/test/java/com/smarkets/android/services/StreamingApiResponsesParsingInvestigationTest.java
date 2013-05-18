package com.smarkets.android.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.eto.SmarketsEtoPiqi.LoginResponse;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.AccountState;
import smarkets.seto.SmarketsSetoPiqi.Currency;
import smarkets.seto.SmarketsSetoPiqi.Decimal;
import smarkets.seto.SmarketsSetoPiqi.Uuid128;

public class StreamingApiResponsesParsingInvestigationTest {

	private final Logger log = Logger.getLogger(getClass());

	@Test
	public void shouldGenerateLoginResponseMocks() {
		SmarketsSetoPiqi.Payload loginResponse = loginResponse();
		log.debug(loginResponse);

		assertThat(loginResponse.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO));
		assertThat(loginResponse.getEtoPayload().getType(), is(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN_RESPONSE));
		assertThat(loginResponse.getEtoPayload().getSeq(), is(1L));
		assertThat(loginResponse.getEtoPayload().getLoginResponseOrBuilder().getSession(), is("session2123"));
	}

	private SmarketsSetoPiqi.Payload loginResponse() {
		return SmarketsSetoPiqi.Payload.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO)
				.setEtoPayload(
						SmarketsEtoPiqi.Payload.newBuilder()
							.setType(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN_RESPONSE)
							.setSeq(1L)
							.setLoginResponse(LoginResponse.newBuilder().setSession("session2123").build())
							.build())
				.build();
	}

	@Test
	public void shouldGenerateAccountStateResponseMocks() {
		SmarketsSetoPiqi.Payload accountStateResponse = accountStateResponse();
		log.debug(accountStateResponse);
		
		assertThat(accountStateResponse.getType(), is(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE));
		assertThat(accountStateResponse.getAccountState().getAccount().getLow(), is(34L));
		assertThat(accountStateResponse.getAccountState().getCurrency(), is(Currency.CURRENCY_GBP));
		assertThat(accountStateResponse.getAccountState().getCurrency().toString().replace("CURRENCY_", ""), is("GBP"));
		assertThat(accountStateResponse.getAccountState().getCash().getValue(), is(200L));
		assertThat(accountStateResponse.getAccountState().getBonus().getValue(), is(201L));
		assertThat(accountStateResponse.getAccountState().getExposure().getValue(), is(202L));
	}

	private SmarketsSetoPiqi.Payload accountStateResponse() {
		return SmarketsSetoPiqi.Payload.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE)
				.setEtoPayload(SmarketsEtoPiqi.Payload.newBuilder().setSeq(2L).build())
				.setAccountState(AccountState.newBuilder()
						.setAccount(Uuid128.newBuilder().setLow(34L).build())
						.setCurrency(Currency.CURRENCY_GBP)
						.setCash(Decimal.newBuilder().setValue(200L).build())
						.setBonus(Decimal.newBuilder().setValue(201L).build())
						.setExposure(Decimal.newBuilder().setValue(202L).build())
						.build())
				.build();
	}
}
