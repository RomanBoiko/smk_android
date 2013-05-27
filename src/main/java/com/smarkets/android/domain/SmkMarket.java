package com.smarkets.android.domain;

public class SmkMarket implements NamedItemWithParent {

	private final String name;
	private final SmkEvent parent;
	public final String id;

	public SmkMarket(String name, String id, SmkEvent parent) {
		this.name = name;
		this.id = id;
		this.parent = parent;
	}

	public String toString() {
		return name + " (market)";
	}

	@Override
	public SmkEvent getParent() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
	}

}
