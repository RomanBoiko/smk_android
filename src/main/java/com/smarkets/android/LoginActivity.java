package com.smarkets.android;

import java.io.IOException;

import com.smarkets.android.domain.actionresults.LoginResult;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	public final static String LOG_TAG = "smarkets";

	public static String EMPTY = "";
	private static final String PASSWORD_PROPERTY = "smkpass";
	private static final String LOGIN_PROPERTY = "smkuser";
	private static final String CREDENTIALS_FILE = "smkCredentials";
	
	private SharedPreferences credentialsCache;
	private boolean loginActionAllowed = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.credentialsCache = this.getSharedPreferences(CREDENTIALS_FILE, 0);
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "onCreate");
		showLoginView();
	}

	public void showLoginView() {
		this.setContentView(R.layout.login);
		final EditText txtUserName = (EditText) this.findViewById(R.id.txtUname);
		final EditText txtPassword = (EditText) this.findViewById(R.id.txtPwd);
		String retrievedLogin = credentialsCache.getString(LOGIN_PROPERTY, "hunter.morris@smarkets.com");
		String retrievedPassword = credentialsCache.getString(PASSWORD_PROPERTY, "abc,123");
		txtUserName.setText(retrievedLogin);
		txtPassword.setText(retrievedPassword);
		Log.i(LOG_TAG, String.format("Login/password from cache: %s/%s", retrievedLogin, retrievedPassword));
		
		Button btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(loginActionAllowed) {
					loginActionAllowed = false;
					try {
						String login = txtUserName.getText().toString();
						String password = txtPassword.getText().toString();
						SharedPreferences.Editor editor = credentialsCache.edit();
						editor.putString(LOGIN_PROPERTY, login);
						editor.putString(PASSWORD_PROPERTY, password);
						editor.commit();
						Log.i(LOG_TAG, String.format("Login/password saved to cache: %s/%s", login, password));
						
						BusinessService.login(login.trim(), password.trim(), new BusinessService.Callback<LoginResult>(){
							@Override
							public void action(final LoginResult response) {
								LoginActivity.this.runOnUiThread(new Runnable() { public void run() {
									if (LoginResult.LOGIN_SUCCESS.equals(response)) {
										Log.i(LOG_TAG, "Login successful");
										Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
										Intent intent = new Intent(LoginActivity.this, EventsActivity.class);
										startActivity(intent);
										finish();
									} else {
										Log.i(LOG_TAG, "Login failed");
										Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
									}
								}});
							}
						});
					} catch (IOException e) {
						Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					} finally {
						loginActionAllowed = true;
					}
				} else {
					Toast.makeText(LoginActivity.this, "Login action in progress, please wait", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	public void logout() {
		Log.i(LOG_TAG, "onStop");
		try {
			BusinessService.logout(new BusinessService.Callback<Boolean>(){
				@Override
				public void action(final Boolean response) {
					LoginActivity.this.runOnUiThread(new Runnable() { public void run() {
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
	}

	@Override
	public void onSaveInstanceState(Bundle instanceStateToSave) {
		Log.i(LOG_TAG, "saveOnShutdown");
	}

}
