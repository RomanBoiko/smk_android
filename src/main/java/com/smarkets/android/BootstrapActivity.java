package com.smarkets.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BootstrapActivity extends Activity {

	private static String TAG = "smk_bootstrap";
	private static String EMPTY = "";

	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	private Button btnCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		txtUserName = (EditText) this.findViewById(R.id.txtUname);
		txtPassword = (EditText) this.findViewById(R.id.txtPwd);
		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ((txtUserName.getText().toString()).equals(txtPassword.getText().toString())) {
					Toast.makeText(BootstrapActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
					Intent homepage = new Intent(BootstrapActivity.this, AuthorizedActivity.class);
					startActivity(homepage);
				} else {
					Toast.makeText(BootstrapActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
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

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(TAG, "saveOnShutdown");
	}
}
