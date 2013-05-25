package com.smarkets.android.services;

import com.smarkets.android.domain.SmkEvent;

public class RestApiClient {
	public static SmkEvent getEventsRoot() {
		return new SmkEvent("All", "/events", null);
	}

}
