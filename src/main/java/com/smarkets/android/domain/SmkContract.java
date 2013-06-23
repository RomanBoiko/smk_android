package com.smarkets.android.domain;

public class SmkContract {
	private final String name;
	public final String id;

	public SmkContract(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String toString() {
		return name + " (contract)";
	}

	public String getName() {
		return name;
	}

}
