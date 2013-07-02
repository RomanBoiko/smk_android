package com.smarkets.android;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ScreenActivity extends Activity {

	public final static String LOG_TAG = "smarkets";

	private BusinessService smkService;

	public static String EMPTY = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			smkService = new BusinessService();
		} catch (IOException e) {
			Toast.makeText(this, "Smarkets API connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "onCreate");
		new LoginView(this).showLoginView(smkService, new ChangeGuiViewCallback() {
			public void moveToNextView(Activity parentActivity) {
				new EventsListView(parentActivity, smkService).showEventsList();
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.smkmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.account:
			new AccountStateViewDialog(this).showFunds(smkService);
			return true;
		case R.id.currentBets:
			new CurrentBetsViewDialog(this).showBets(smkService);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(LOG_TAG, "saveOnShutdown");
	}
}
