package com.smarkets.android.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarkets.android.SmkConfig;

public class JsonEventsSource {
	static {
		trustToAllCertificates();
	}

	private static final SmkConfig CONFIG = new SmkConfig();
	private final JSONObject json;

	public JsonEventsSource(JSONObject json) {
		this.json = json;
	}

	public static JsonEventsSource fetchViaHttp(String jsonUrl) throws IOException, JSONException {
		jsonUrl = CONFIG.smkRestApiRoot + jsonUrl;

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
		if (json.getJSONArray("event").length() > 1) {
			return json.getJSONArray("event");
		}
		return json.getJSONArray("event").getJSONObject(0).getJSONArray(childrenTagName);
	}

	private static void trustToAllCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
