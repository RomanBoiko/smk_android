package com.smarkets.android;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toast;

import com.smarkets.android.domain.SmkMarket;

public class MarketPricesDialog {

	private final Dialog placeBetDialog;
	private final Activity parentActivity;

	public MarketPricesDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		placeBetDialog = new Dialog(parentActivity);
		placeBetDialog.setContentView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_market_prices, null));
		placeBetDialog.setTitle("Loading market prices...");
		placeBetDialog.show();
	}

	public void showDialog(SmkMarket market) {
		try {
			BusinessService.currentPricesForMarket(Long.parseLong(market.id), new BusinessService.Callback<String>(){
				@Override
				public void action(final String response) {
					MarketPricesDialog.this.parentActivity.runOnUiThread(new Runnable() { public void run() {
						placeBetDialog.setTitle("Market prices");
						((TextView) placeBetDialog.findViewById(R.id.marketQuotes)).setText(response);
					}});
				}
			});
		} catch (Exception e) {
			Toast.makeText(MarketPricesDialog.this.parentActivity, "Market prices error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		
	}
}
