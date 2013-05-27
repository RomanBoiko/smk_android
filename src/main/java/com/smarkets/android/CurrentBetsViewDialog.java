package com.smarkets.android;

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

	public void showBets(SmkStreamingService smkService) {
		final ListView betsList = (ListView) currentBetsDialog.findViewById(R.id.betsList);
		betsList.setAdapter(betsSourceAdapter(smkService.currentBets()));
		betsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bet betUnderAction = (Bet)(betsList.getAdapter().getItem(position));
				new AlertDialog.Builder(parentActivity)
					.setTitle("Bet Details")
					.setMessage(betUnderAction.toDetailedString())
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {}
					})
					.setNegativeButton("Cancel Bet", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(parentActivity, "show 'Confirm' dialog for bet cancellation", Toast.LENGTH_SHORT).show();
						}
					}).create().show();
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
