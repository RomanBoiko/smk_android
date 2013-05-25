package com.smarkets.android;

import static com.smarkets.android.services.RestApiClient.getEventsRoot;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.smarkets.android.domain.SmkEvent;

public class ScreenActivity extends Activity {

	public final static String LOG_TAG = "smarkets";

	private SmkStreamingService smkService = new SmkStreamingService();

	private static String EMPTY = "";

	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	private Button btnCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "onCreate");
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
		// For events development
//		authorizedView();
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
		case R.id.help:
			Toast.makeText(ScreenActivity.this, "help", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void authorizedView() {
		setContentView(R.layout.authorized);
		final ListView listview = (ListView) this.findViewById(R.id.listview);

		listview.setAdapter(eventsSourceAdapter(getEventsRoot().getChildren()));
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<SmkEvent> children = ((SmkEvent)listview.getAdapter().getItem(position)).getChildren();
				if(!children.isEmpty()) {
					listview.setAdapter(eventsSourceAdapter(children));
				}
			}

		});
	}

	private ArrayAdapter<SmkEvent> eventsSourceAdapter(List<SmkEvent> children) {
		return new ArrayAdapter<SmkEvent>(this, android.R.layout.simple_list_item_1, children);
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(LOG_TAG, "saveOnShutdown");
	}
}
