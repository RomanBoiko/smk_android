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
import com.smarkets.android.domain.SmkContract;
import com.smarkets.android.domain.SmkMarket;

public class PlaceBetDialog {

	private final Dialog placeBetDialog;
	private final Activity parentActivity;
	private SmkMarket market;
	private SmkContract contract;

	public PlaceBetDialog(Activity parentActivity) {
		this.parentActivity = parentActivity;
		placeBetDialog = new AlertDialog.Builder(parentActivity)
				.setView(parentActivity.getLayoutInflater().inflate(R.layout.dialog_place_bet, null))
				.setTitle("Place Bet")
				.setPositiveButton("Bet", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {}
				}).create();
		placeBetDialog.show();
	}

	public void showDialog(final SmkStreamingService smkService, SmkMarket market) {
		this.market = market;
		final ListView contractsList = (ListView) placeBetDialog.findViewById(R.id.contractsList);
		contractsList.setAdapter(contractsSourceAdapter(market.getContracts()));
		contractsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				contract = (SmkContract)(contractsList.getAdapter().getItem(position));
			}

		});

	}
	private ArrayAdapter<SmkContract> contractsSourceAdapter(List<SmkContract> contracts) {
		Collections.sort(contracts, new Comparator<SmkContract>() {
			public int compare(SmkContract contract1, SmkContract contract2) {
				return contract1.getName().compareTo(contract2.getName());
			}
		});
		return new ArrayAdapter<SmkContract>(parentActivity, android.R.layout.simple_list_item_1, contracts);
	}

}
