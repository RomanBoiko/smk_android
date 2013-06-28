package com.smarkets.android.services;

import java.util.UUID;

import smarkets.seto.SmarketsSetoPiqi.Uuid128;

public class Uuid {

	public static Uuid128 fromUuid(UUID uuid) {
		return Uuid128.newBuilder()
				.setLow(uuid.getLeastSignificantBits())
				.setHigh(uuid.getMostSignificantBits())
				.build();
	}

	public static Uuid128 fromLowLong(long lowLong) {
		return Uuid128.newBuilder()
				.setLow(lowLong)
				.setHigh(0)
				.build();
	}

	public static UUID toUuid(Uuid128 uuid) {
		return new UUID(uuid.getHigh(), uuid.getLow());
	}
}
