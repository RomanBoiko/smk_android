package com.smarkets.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;

import com.smarkets.android.domain.AccountFunds;

public class AccountStateViewDialog {

	private final Dialog accountStateDialog;

	public AccountStateViewDialog(Activity parentActivity) {
		accountStateDialog = new AlertDialog.Builder(parentActivity)
			.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_account_state, null))
			.setTitle("Account state")
			.setPositiveButton("Hide", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {}
			}).create();
		accountStateDialog.show();
	}

	public void showFunds(SmkStreamingService smkService) {
		TextView cash = (TextView) accountStateDialog.findViewById(R.id.cash);
		TextView bonus = (TextView) accountStateDialog.findViewById(R.id.bonus);
		TextView exposure = (TextView) accountStateDialog.findViewById(R.id.exposure);

		try {
			AccountFunds accountFunds = smkService.getAccountStatus();
			cash.setText(accountFunds.getCash().toString());
			bonus.setText(accountFunds.getBonus().toString());
			exposure.setText(accountFunds.getExposure().toString());
		} catch (Exception e) {
			Log.e(ScreenActivity.LOG_TAG, "Account status not retrieved", e);
			throw new RuntimeException(e);
		}
	}
}
