package com.smarkets.android.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Events {
	private Events(){ };
	private final List<Tournament> tournaments = new LinkedList<Tournament>();

	public static Events fromJson(String jsonEvents) throws JSONException {
		Events events = new Events();
		JSONArray tournaments = new JSONObject(jsonEvents).getJSONArray("event").getJSONObject(0).getJSONArray("children");
		for (int i = 0; i < tournaments.length(); i++) {
			JSONObject jsonTournament = tournaments.getJSONObject(i);
			events.tournaments.add(new Tournament(jsonTournament.getString("name"), jsonTournament.getString("url")));
		}
		return events;
	}

	public List<Tournament> tournaments() {
		return new ArrayList<Tournament>(tournaments);
	}
}