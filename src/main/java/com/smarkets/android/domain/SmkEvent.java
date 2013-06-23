package com.smarkets.android.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.smarkets.android.LongRunningActionAlert;
import com.smarkets.android.services.JsonEventsSource;

public class SmkEvent implements NamedItemWithParent {
	private final static Pattern DATE_IN_URL_PATTERN = Pattern.compile("(\\d{4}/\\d{2}/\\d{2})");
	private final String name;
	private final SmkEvent parent;
	public final String url;
	public final String date;
	private List<SmkEvent> children;
	private List<SmkMarket> markets;

	public SmkEvent(String name, String url, SmkEvent parent) {
		this.name = name;
		this.url = url;
		this.parent = parent;
		Matcher dateMatcher = DATE_IN_URL_PATTERN.matcher(url);
		if (dateMatcher.find()) {
			this.date = dateMatcher.group();
		} else {
			this.date = "1970/01/01";
		}
	}

	public List<SmkEvent> getChildren(LongRunningActionAlert longRunningActionAlert) {
		try {
			if (null == children) {
				longRunningActionAlert.show();
				children = new LinkedList<SmkEvent>();
				JSONArray childrenJsons;
				childrenJsons = JsonEventsSource.fetchViaHttp(url).getChildEventsAsJsonArray();
				for (int i = 0; i < childrenJsons.length(); i++) {
					JSONObject jsonTournament = childrenJsons.getJSONObject(i);
					children.add(new SmkEvent(jsonTournament.getString("name"), jsonTournament.getString("url"), this));
				}

			}
			return new ArrayList<SmkEvent>(children);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<SmkMarket> getMarkets(LongRunningActionAlert longRunningActionAlert) {
		try {
			if (url.contains("switzerland-super-league")) {// corrupted event,
															// TODO: to process
															// excetions here
				return new LinkedList<SmkMarket>();
			}
			if (null == markets) {
				longRunningActionAlert.show();
				markets = new LinkedList<SmkMarket>();
				JSONArray jsonMarkets = JsonEventsSource.fetchViaHttp(url).getMarketsAsJsonArray();
				for (int i = 0; i < jsonMarkets.length(); i++) {
					JSONObject jsonMarket = jsonMarkets.getJSONObject(i);
					SmkMarket smkMarket = new SmkMarket(jsonMarket.getString("name"), jsonMarket.getString("id"), this);
					JSONArray jsonContracts = jsonMarket.getJSONArray("contracts");
					for (int contractNum = 0; contractNum < jsonContracts.length(); contractNum++) {
						JSONObject jsonContract = jsonContracts.getJSONObject(contractNum);
						smkMarket.addContract(new SmkContract(jsonContract.getString("name"), jsonContract.getString("id")));
					}
					markets.add(smkMarket);
				}
			}
			return new ArrayList<SmkMarket>(markets);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String toString() {
		return name;
	}

	public String toDetailedString() {
		return String.format("%s[name=%s, url=%s]", this.getClass().getName(), name, url);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmkEvent other = (SmkEvent) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
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