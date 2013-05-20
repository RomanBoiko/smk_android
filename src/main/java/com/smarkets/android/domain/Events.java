package com.smarkets.android.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarkets.android.services.JsonEventsSource;

public class Events {
	public final String name;
	public final String url;
	private List<Tournament> tournaments;

	public Events(String name, String url){
		this.name = name;
		this.url = url;
	}

	public List<Tournament> getTournaments() throws JSONException, IOException {
		if (null == tournaments) {
			tournaments = new LinkedList<Tournament>();
			JSONArray tournamentJsons = JsonEventsSource.fetchViaHttp(url).getChildEventsAsJsonArray();
			for (int i = 0; i < tournamentJsons.length(); i++) {
				JSONObject jsonTournament = tournamentJsons.getJSONObject(i);
				tournaments.add(new Tournament(jsonTournament.getString("name"), jsonTournament.getString("url")));
			}

		}
		return new ArrayList<Tournament>(tournaments);
	}
}