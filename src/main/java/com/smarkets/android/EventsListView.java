package com.smarkets.android;

import static com.smarkets.android.services.RestApiClient.getEventsRoot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.smarkets.android.domain.NamedItemWithParent;
import com.smarkets.android.domain.SmkMarket;
import com.smarkets.android.domain.SmkEvent;


public class EventsListView {

	private Activity parentActivity;
	private final SmkStreamingService smkService;

	public EventsListView(Activity parentActivity, SmkStreamingService smkService) {
		this.parentActivity = parentActivity;
		this.smkService = smkService;
	}

	public void showEventsList() {
		parentActivity.setContentView(R.layout.authorized);
		final ListView listview = (ListView) parentActivity.findViewById(R.id.listview);

		listview.setAdapter(eventsSourceAdapter(getEventsRoot().getChildren(new LongRunningActionAlert(parentActivity))));
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Object objectUnderAction = listview.getAdapter().getItem(position);
				if (objectUnderAction instanceof SmkMarket) {
					new AlertDialog.Builder(parentActivity)
						.setTitle("Market Actions")
						.setPositiveButton("Current prices", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Toast.makeText(parentActivity, "show current prices for market", Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("Place Bet", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SmkMarket market = (SmkMarket)objectUnderAction;
								new PlaceBetDialog(parentActivity, smkService).showDialog(market);
							}
						}).create().show();
				} else {
					SmkEvent eventUnderAction = (SmkEvent)objectUnderAction;
					List<SmkEvent> children = eventUnderAction.getChildren(new LongRunningActionAlert(parentActivity));
					if(!children.isEmpty()) {
						listview.setAdapter(eventsSourceAdapter(children));
					} else if(!eventUnderAction.getMarkets(new LongRunningActionAlert(parentActivity)).isEmpty()) {
						listview.setAdapter(marketsSourceAdapter(eventUnderAction.getMarkets(new LongRunningActionAlert(parentActivity))));
					} else {
						Toast.makeText(parentActivity, "No child events or markets available", Toast.LENGTH_SHORT).show();
					}
				}
			}

		});
		Button btnEventsBack = (Button) parentActivity.findViewById(R.id.btnEventsBack);
		btnEventsBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NamedItemWithParent child = (NamedItemWithParent)listview.getAdapter().getItem(0);
				if(child.getParent() != null && child.getParent().getParent() != null) {
					listview.setAdapter(eventsSourceAdapter(child.getParent().getParent().getChildren(new LongRunningActionAlert(parentActivity))));
				} else {
					Toast.makeText(parentActivity, "No way back - list of parent events already", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private ArrayAdapter<SmkEvent> eventsSourceAdapter(List<SmkEvent> children) {
		Collections.sort(children, new NamedItemComparator());
		return new ArrayAdapter<SmkEvent>(parentActivity, android.R.layout.simple_list_item_1, children);
	}

	private ArrayAdapter<SmkMarket> marketsSourceAdapter(List<SmkMarket> markets) {
		Collections.sort(markets, new NamedItemComparator());
		return new ArrayAdapter<SmkMarket>(parentActivity, android.R.layout.simple_list_item_1, markets);
	}

	private final class NamedItemComparator implements Comparator<NamedItemWithParent> {
		public int compare(NamedItemWithParent item1, NamedItemWithParent item2) {
			return item1.getName().compareTo(item2.getName());
		}
	}
}
