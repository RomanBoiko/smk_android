package com.smarkets.android.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.smarkets.android.SmkConfig;

public class JsonEventsSource {

	private static final SmkConfig CONFIG = new SmkConfig();
	private final JSONObject json;

	public JsonEventsSource(JSONObject json) {
		this.json = json;
	}

	public static JsonEventsSource fetchViaHttp(String jsonUrl) throws IOException, JSONException {
		jsonUrl = CONFIG.smkRestApiRoot + jsonUrl;

		Log.i("smk_jsonsource", "Url for json: " + jsonUrl);
		URL url = new URL(jsonUrl);
		URLConnection conn = url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String inputLine;
		StringBuffer jsonString = new StringBuffer();

		while ((inputLine = br.readLine()) != null) {
			jsonString.append(inputLine);
		}
		br.close();
		return new JsonEventsSource(new JSONObject(jsonString.toString()));
	}

	public JSONArray getChildEventsAsJsonArray() throws JSONException {
		return getEventChildrenByTagName("children");
	}

	public JSONArray getMarketsAsJsonArray() throws JSONException {
		return getEventChildrenByTagName("markets");
	}

	private JSONArray getEventChildrenByTagName(String childrenTagName) throws JSONException {
		if (json.getJSONArray("event").length()>1) {
			return json.getJSONArray("event");
		}
		return json.getJSONArray("event").getJSONObject(0).getJSONArray(childrenTagName);
	}

}
