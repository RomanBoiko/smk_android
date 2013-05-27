package com.smarkets.android;

import android.app.Activity;
import android.widget.Toast;

public class LongRunningActionAlert {
	private Activity activity;

	public LongRunningActionAlert(Activity activity) {
		this.activity = activity;
	}

	public void show(){
		Toast.makeText(activity, "Loading...", Toast.LENGTH_LONG).show();
	}
}
