package com.smarkets.android;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smarkets.android.BusinessService.Callback;
import com.smarkets.android.domain.Bet;

public class CurrentBetsViewDialog {

	private final Dialog currentBetsDialog;
	private final Activity parentActivity;

	public CurrentBetsViewDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		currentBetsDialog = new AlertDialog.Builder(parentActivity)
				.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_current_bets, null))
				.setTitle("My Current Bets")
				.setPositiveButton("Hide", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				}).create();
		currentBetsDialog.show();
	}

	public void showBets(final BusinessService smkService) {
		final ListView betsList = (ListView) currentBetsDialog.findViewById(R.id.betsList);
		try {
			showBetsList(smkService, betsList);
		} catch (IOException e) {
			Toast.makeText(parentActivity, "Current bets show error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		betsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Bet betUnderAction = (Bet)(betsList.getAdapter().getItem(position));
				new AlertDialog.Builder(parentActivity)
					.setTitle("Bet Details")
					.setMessage(betUnderAction.toDetailedString())
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {}
					})
					.setNegativeButton("Cancel Bet", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							try {
								betUnderAction.cancel(new Callback<Boolean>() {
									@Override
									public void action(final Boolean response) {
										parentActivity.runOnUiThread(new Runnable() { public void run() {
											if (response) {
												showBets(smkService);
												Toast.makeText(parentActivity, "Bet cancelled", Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(parentActivity, "Can't cancel bet", Toast.LENGTH_LONG).show();
											}
										}});
									}
								});
							} catch (Exception e) {
								Toast.makeText(parentActivity, "Cancel bet error: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						}
					}).create().show();
			}
		});
	}

	private void showBetsList(final BusinessService smkService, final ListView betsList) throws IOException {
		smkService.currentBets(new Callback<List<Bet>>() {
			@Override
			public void action(final List<Bet> response) {
				parentActivity.runOnUiThread(new Runnable() { public void run() {
					betsList.setAdapter(betsSourceAdapter(response));
				}});
			}
		});
	}

	private ArrayAdapter<Bet> betsSourceAdapter(List<Bet> bets) {
		Collections.sort(bets, new Comparator<Bet>() {
			public int compare(Bet bet1, Bet bet2) {
				return bet1.createdDate.compareTo(bet2.createdDate);
			}
		});
		return new ArrayAdapter<Bet>(parentActivity, android.R.layout.simple_list_item_1, bets);
	}

}
