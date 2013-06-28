package com.smarkets.android.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;

import smarkets.seto.SmarketsSetoPiqi.Uuid128;

public class UuidTest {

	@Test
	public void uuidConversionMustBeSymmetricUsingUuid128AsBase() {
		Uuid128 uuid128 = Uuid128.newBuilder().setHigh(333L).setLow(222L).build();
		UUID uuid = Uuid.toUuid(uuid128);
		assertThat(Uuid.fromUuid(uuid).getHigh(), is(uuid128.getHigh()));
		assertThat(Uuid.fromUuid(uuid).getLow(), is(uuid128.getLow()));
	}

	@Test
	public void uuidConversionMustBeSymmetricUsingUuidBase() {
		UUID uuid = UUID.randomUUID();
		Uuid128 uuid128 = Uuid.fromUuid(uuid);
		assertThat(Uuid.toUuid(uuid128), is(uuid));
	}
}
