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

public class SportsEvent {
	private final static Pattern DATE_IN_URL_PATTERN = Pattern.compile("(\\d{4}/\\d{2}/\\d{2})");
	public final String name;
	public final String url;
	public final String date;
	private List<Market> markets;

	public SportsEvent(String name, String url) {
		this.name = name;
		this.url = url;
		Matcher dateMatcher = DATE_IN_URL_PATTERN.matcher(url);
		if (dateMatcher.find()) {
			this.date = dateMatcher.group();
		} else {
			this.date = "1970/01/01";
		}
	}

	public List<Market> getMarkets() throws IOException, JSONException {
		if (url.contains("switzerland-super-league")) {//corrupted event, TODO: to process excetions here
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

}