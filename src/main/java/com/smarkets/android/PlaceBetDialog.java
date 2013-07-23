package com.smarkets.android;

import java.math.BigDecimal;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.smarkets.android.domain.BetType;
import com.smarkets.android.domain.SmkContract;
import com.smarkets.android.domain.SmkMarket;
import com.smarkets.android.domain.actionresults.PlaceBetResult;

public class PlaceBetDialog {

	private final Dialog placeBetDialog;
	private final Activity parentActivity;
	private SmkMarket market;

	public PlaceBetDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		placeBetDialog = new AlertDialog.Builder(parentActivity)
				.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_place_bet, null))
				.setTitle("Place Bet")
				.setPositiveButton("Bet", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Spinner contractSpinner = ((Spinner)(placeBetDialog.findViewById(R.id.contractsSpinner)));
						Spinner buySellSpinner = ((Spinner)(placeBetDialog.findViewById(R.id.buySellSpinner)));
						try {
							BusinessService.placeBet(
									BetType.valueOf(buySellSpinner.getItemAtPosition(buySellSpinner.getLastVisiblePosition()).toString()),
									Long.parseLong(market.id),
									Long.parseLong(((SmkContract)contractSpinner.getItemAtPosition(contractSpinner.getLastVisiblePosition())).id),
									new BigDecimal(((EditText)(placeBetDialog.findViewById(R.id.betPrice))).getText().toString()),
									new BigDecimal(((EditText)(placeBetDialog.findViewById(R.id.betAmount))).getText().toString()), new BusinessService.Callback<PlaceBetResult>(){
										@Override
										public void action(final PlaceBetResult response) {
											PlaceBetDialog.this.parentActivity.runOnUiThread(new Runnable() { public void run() {
												if (PlaceBetResult.BET_PLACED.equals(response)) {
													Toast.makeText(PlaceBetDialog.this.parentActivity, "Bet placed", Toast.LENGTH_LONG).show();
												} else {
													Toast.makeText(PlaceBetDialog.this.parentActivity, "Bet can't be placed", Toast.LENGTH_LONG).show();
												}
											}});
										}
									});
						} catch (Exception e) {
							Toast.makeText(PlaceBetDialog.this.parentActivity, "Bet place error: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) { }
				}).create();
		placeBetDialog.show();
	}

	public void showDialog(SmkMarket market) {
		this.market = market;
		Spinner contractsListSpinner = (Spinner) placeBetDialog.findViewById(R.id.contractsSpinner);
		ArrayAdapter<SmkContract> contractsAdapter =
				new ArrayAdapter<SmkContract>(parentActivity, android.R.layout.simple_spinner_item, market.getContracts());
		contractsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		contractsListSpinner.setAdapter(contractsAdapter);
		
		Spinner spinner = (Spinner) placeBetDialog.findViewById(R.id.buySellSpinner);
		ArrayAdapter<String> buySellAdapter =
				new ArrayAdapter<String>(parentActivity, android.R.layout.simple_spinner_item, Arrays.asList("BUY", "SELL"));
		buySellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(buySellAdapter);

	}
}
