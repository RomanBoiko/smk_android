package com.smarkets.android;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import smarkets.seto.SmarketsSetoPiqi;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.smarkets.android.domain.AccountFunds;
import com.smarkets.android.services.StreamingApiClient;
import com.smarkets.android.services.StreamingApiRequestsFactory;

public class AuthorizedActivity extends Activity {

	private static String TAG = "smk_authorized";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.authorized);
		TextView cash = (TextView) this.findViewById(R.id.cash);
		TextView bonus = (TextView) this.findViewById(R.id.bonus);
		TextView exposure = (TextView) this.findViewById(R.id.exposure);

		try {
			AccountFunds accountFunds = getAccountStatus();
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

	private AccountFunds getAccountStatus() throws UnknownHostException, IOException {
		SmkConfig smkConfig = new SmkConfig();
		StreamingApiRequestsFactory factory = new StreamingApiRequestsFactory();
		StreamingApiClient apiClient = new StreamingApiClient(new Socket(smkConfig.smkStreamingApiHost,
				smkConfig.smkStreamingApiPort));
		apiClient.getSmkResponse(factory.loginRequest(smkConfig.smkTestUserLogin, smkConfig.smkTestUserPassword));
		SmarketsSetoPiqi.Payload accountStateResponse = apiClient.getSmkResponse(factory.accountStateRequest());
		apiClient.getSmkResponse(factory.logoutRequest());
		return new AccountFunds(accountStateResponse.getAccountState());
	}
}
