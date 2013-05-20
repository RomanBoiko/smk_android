package com.smarkets.android.domain;

public class Tournament {
	public Tournament(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public final String name;
	public final String url;

	public String toString() {
		return String.format("%s[name=%s, url=%s]", this.getClass().getName(), name, url);
	}
}
