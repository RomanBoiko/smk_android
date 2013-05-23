package com.smarkets.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
					showFunds();
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
	
	private void showFunds () {
		setContentView(R.layout.authorized);
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
