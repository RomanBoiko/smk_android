package com.smarkets.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.smarkets.android.domain.SmkMarket;

public class MarketPricesDialog {

	private final Dialog placeBetDialog;
	private final Activity parentActivity;

	public MarketPricesDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		placeBetDialog = new AlertDialog.Builder(parentActivity)
				.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_market_prices, null))
				.setTitle("Market prices")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) { }
				}).create();
		placeBetDialog.show();
	}

	public void showDialog(SmkMarket market) {
		try {
			BusinessService.currentPricesForMarket(Long.parseLong(market.id), new BusinessService.Callback<String>(){
				@Override
				public void action(final String response) {
					MarketPricesDialog.this.parentActivity.runOnUiThread(new Runnable() { public void run() {
						((TextView) placeBetDialog.findViewById(R.id.marketQuotes)).setText(response);
					}});
				}
			});
		} catch (Exception e) {
			Toast.makeText(MarketPricesDialog.this.parentActivity, "Market prices error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		
	}
}
