package com.smarkets.android.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarkets.android.services.JsonEventsSource;

public class Tournament {
	public final String name;
	public final String url;
	private List<SportsEvent> tournamentEvents;

	public Tournament(String name, String url) {
		this.name = name;
		this.url = url;
	}


	public String toString() {
		return String.format("%s[name=%s, url=%s]", this.getClass().getName(), name, url);
	}

	public List<SportsEvent> getTournamentEvents() throws IOException, JSONException {
		if (null == tournamentEvents) {
			tournamentEvents = new LinkedList<SportsEvent>();
			JSONArray events = JsonEventsSource.fetchViaHttp(url).getChildEventsAsJsonArray();
			for (int i = 0; i < events.length(); i++) {
				JSONObject jsonEvent = events.getJSONObject(i);
				tournamentEvents.add(new SportsEvent(jsonEvent.getString("name"), jsonEvent.getString("url")));
			}
		}
		return new ArrayList<SportsEvent>(tournamentEvents);
	}
}
