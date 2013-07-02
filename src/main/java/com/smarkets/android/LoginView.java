package com.smarkets.android;

import java.io.IOException;

import com.smarkets.android.domain.actionresults.LoginResult;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView {
	private Activity parentActivity;

	public LoginView(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}
	
	public void showLoginView(final BusinessService smkService, final ChangeGuiViewCallback changeGuiViewCallback) {
		parentActivity.setContentView(R.layout.login);
		final EditText txtUserName = (EditText) parentActivity.findViewById(R.id.txtUname);
		final EditText txtPassword = (EditText) parentActivity.findViewById(R.id.txtPwd);
		Button btnLogin = (Button) parentActivity.findViewById(R.id.btnLogin);
		Button btnCancel = (Button) parentActivity.findViewById(R.id.btnCancel);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					smkService.login(txtUserName.getText().toString(), txtPassword.getText().toString(), new BusinessService.Callback<LoginResult>(){
						@Override
						public void action(LoginResult response) {
							if (LoginResult.LOGIN_SUCCESS.equals(response)) {
								Toast.makeText(parentActivity, "Login Successful", Toast.LENGTH_LONG).show();
								changeGuiViewCallback.moveToNextView(parentActivity);
							} else {
								Toast.makeText(parentActivity, "Invalid Login", Toast.LENGTH_LONG).show();
							}
						}
					});
				} catch (IOException e) {
					Toast.makeText(parentActivity, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				txtUserName.setText(ScreenActivity.EMPTY);
				txtPassword.setText(ScreenActivity.EMPTY);
			}
		});
	}


}
