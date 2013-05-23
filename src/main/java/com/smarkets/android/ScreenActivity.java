package com.smarkets.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarkets.android.domain.AccountFunds;

public class ScreenActivity extends Activity {

	private SmkStreamingService smkService = new SmkStreamingService();

	private static String TAG = "smarkets";
	private static String EMPTY = "";

	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	private Button btnCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.login);
		txtUserName = (EditText) this.findViewById(R.id.txtUname);
		txtPassword = (EditText) this.findViewById(R.id.txtPwd);
		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (smkService.login(txtUserName.getText().toString(), txtPassword.getText().toString())) {
					Toast.makeText(ScreenActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
					authorizedView();
				} else {
					Toast.makeText(ScreenActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				txtUserName.setText(EMPTY);
				txtPassword.setText(EMPTY);
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_options_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			Toast.makeText(ScreenActivity.this, "about", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.help:
			Toast.makeText(ScreenActivity.this, "help", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void authorizedView() {
		setContentView(R.layout.authorized);
		showFunds();
		ListView listview = (ListView) findViewById(R.id.listview);
	    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
	        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	        "Android", "iPhone", "WindowsMobile" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
	private void showFunds() {
		TextView cash = (TextView) this.findViewById(R.id.cash);
		TextView bonus = (TextView) this.findViewById(R.id.bonus);
		TextView exposure = (TextView) this.findViewById(R.id.exposure);

		try {
			AccountFunds accountFunds = smkService.getAccountStatus();
			cash.setText(accountFunds.getCash().toString());
			bonus.setText(accountFunds.getBonus().toString());
			exposure.setText(accountFunds.getExposure().toString());
		} catch (Exception e) {
			Log.e(TAG, "Account status not retrieved", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(TAG, "saveOnShutdown");
	}
}
