package com.smarkets.android.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarkets.android.services.JsonEventsSource;

public class SmkEvent {
	private final static Pattern DATE_IN_URL_PATTERN = Pattern.compile("(\\d{4}/\\d{2}/\\d{2})");
	public final String name;
	public final String url;
	public final String date;
	public final SmkEvent parent;
	private List<SmkEvent> children;
	private List<Market> markets;

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

	public List<SmkEvent> getChildren() {
		try {
			if (null == children) {
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

	public List<Market> getMarkets() throws IOException, JSONException {
		if (url.contains("switzerland-super-league")) {// corrupted event, TODO: to process excetions here
			return new LinkedList<Market>();
		}
		if (null == markets) {
			markets = new LinkedList<Market>();
			JSONArray events = JsonEventsSource.fetchViaHttp(url).getMarketsAsJsonArray();
			for (int i = 0; i < events.length(); i++) {
				JSONObject jsonEvent = events.getJSONObject(i);
				markets.add(new Market(jsonEvent.getString("name"), jsonEvent.getString("id")));
			}
		}
		return new ArrayList<Market>(markets);
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
}