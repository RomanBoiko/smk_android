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
	private final Activity parentActivity;

	public AccountStateViewDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		accountStateDialog = new AlertDialog.Builder(parentActivity)
			.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_account_state, null))
			.setTitle("Account state")
			.setPositiveButton("Hide", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {}
			}).create();
		accountStateDialog.show();
	}

	public void showFunds() {
		final TextView cash = (TextView) accountStateDialog.findViewById(R.id.cash);
		final TextView bonus = (TextView) accountStateDialog.findViewById(R.id.bonus);
		final TextView exposure = (TextView) accountStateDialog.findViewById(R.id.exposure);

		try {
			BusinessService.getAccountStatus(new BusinessService.Callback<AccountFunds>(){
				@Override
				public void action(final AccountFunds accountFunds) {
					parentActivity.runOnUiThread(new Runnable() { public void run() {
						cash.setText(accountFunds.getCash().toString());
						bonus.setText(accountFunds.getBonus().toString());
						exposure.setText(accountFunds.getExposure().toString());
					}});
				}
			});
		} catch (Exception e) {
			Log.e(LoginActivity.LOG_TAG, "Account status not retrieved", e);
			throw new RuntimeException(e);
		}
	}
}
