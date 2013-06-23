package com.smarkets.android.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SmkMarket implements NamedItemWithParent {

	private final String name;
	private final SmkEvent parent;
	public final String id;
	private final List<SmkContract> contracts;

	public SmkMarket(String name, String id, SmkEvent parent) {
		this.name = name;
		this.id = id;
		this.parent = parent;
		this.contracts = new LinkedList<SmkContract>();
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

	public void addContract(SmkContract contract) {
		this.contracts.add(contract);
	}

	public List<SmkContract> getContracts() {
		return new ArrayList<SmkContract>(contracts);
	}

}
