package com.smarkets.android;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class EventsActivity extends Activity {

	public final static String LOG_TAG = "smarkets";

	public static String EMPTY = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "onCreate");
		new EventsListView(this).showEventsList();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.smkmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.account:
			new AccountStateViewDialog(this).showFunds();
			return true;
		case R.id.currentBets:
			new CurrentBetsViewDialog(this).showBets();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(LOG_TAG, "saveOnShutdown");
	}
	
	@Override
	public void onStop() { 
		Log.i(LOG_TAG, "onStop");
		try {
			BusinessService.logout(new BusinessService.Callback<Boolean>(){
				@Override
				public void action(final Boolean response) {
					EventsActivity.this.runOnUiThread(new Runnable() { public void run() {
						if (response) {
							Log.i(LOG_TAG, "Logout successful");
						} else {
							Log.e(LOG_TAG, "Logout Failed");
						}
					}});
				}
			});
		} catch (IOException e) {
			Log.e(LOG_TAG, "logoutError: " + e.getMessage());
		}
		super.onStop();
	}


}
