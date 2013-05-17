package com.smarkets.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AuthorizedActivity extends Activity {

	private static String TAG = "smk_authorized";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.authorized);
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(TAG, "saveOnShutdown");
	}
}
