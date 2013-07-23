package com.smarkets.android;

import static com.smarkets.android.ScreenActivity.LOG_TAG;

import java.io.IOException;

import com.smarkets.android.domain.actionresults.LoginResult;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView {
	private static final String PASSWORD_PROPERTY = "smkpass";
	private static final String LOGIN_PROPERTY = "smkuser";
	private static final String CREDENTIALS_FILE = "smkCredentials";

	private final SharedPreferences credentialsCache;
	private final Activity parentActivity;
	private boolean loginActionAllowed = true;

	public LoginView(Activity parentActivity) {
		this.parentActivity = parentActivity;
		this.credentialsCache = parentActivity.getSharedPreferences(CREDENTIALS_FILE, 0);

	}
	
	public void showLoginView(final BusinessService smkService, final ChangeGuiViewCallback changeGuiViewCallback) {
		parentActivity.setContentView(R.layout.login);
		final EditText txtUserName = (EditText) parentActivity.findViewById(R.id.txtUname);
		final EditText txtPassword = (EditText) parentActivity.findViewById(R.id.txtPwd);
		String retrievedLogin = credentialsCache.getString(LOGIN_PROPERTY, "jason.trost@smarkets.com");
		String retrievedPassword = credentialsCache.getString(PASSWORD_PROPERTY, "");
		txtUserName.setText(retrievedLogin);
		txtPassword.setText(retrievedPassword);
		Log.i(LOG_TAG, String.format("Login/password from cache: %s/%s", retrievedLogin, retrievedPassword));
		
		Button btnLogin = (Button) parentActivity.findViewById(R.id.btnLogin);
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
						
						smkService.login(login.trim(), password.trim(), new BusinessService.Callback<LoginResult>(){
							@Override
							public void action(final LoginResult response) {
								parentActivity.runOnUiThread(new Runnable() { public void run() {
									if (LoginResult.LOGIN_SUCCESS.equals(response)) {
										Log.i(LOG_TAG, "Login successful");
										Toast.makeText(parentActivity, "Login Successful", Toast.LENGTH_LONG).show();
										changeGuiViewCallback.moveToNextView(parentActivity);
									} else {
										Log.i(LOG_TAG, "Login failed");
										Toast.makeText(parentActivity, "Invalid Login", Toast.LENGTH_LONG).show();
									}
								}});
							}
						});
					} catch (IOException e) {
						Toast.makeText(parentActivity, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					} finally {
						loginActionAllowed = true;
					}
				} else {
					Toast.makeText(parentActivity, "Login action in progress, please wait", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


}
