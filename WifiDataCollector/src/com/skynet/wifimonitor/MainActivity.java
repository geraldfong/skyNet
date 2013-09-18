package com.skynet.wifimonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";

	private EditText experimentField;
	private EditText numMinutesField;
	private Button start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		experimentField = (EditText) findViewById(R.id.experiment);
		numMinutesField = (EditText) findViewById(R.id.numMinutes);
		start = (Button) findViewById(R.id.start);
	
		setReceivers();
		setListeners();
	}
	
	private void setReceivers() {
		// This will re-enable the start button when we receive a finished BROADCAST_FINISHED message
		IntentFilter finishedScanIntentFilter = new IntentFilter(Constants.BROADCAST_FINISHED);
		BroadcastReceiver finishedScanReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				start.setText("Start");
				start.setEnabled(true);
			}
		};
		LocalBroadcastManager.getInstance(MainActivity.this).
			registerReceiver(finishedScanReceiver, finishedScanIntentFilter);
	}

	private void setListeners() {
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String experiment;
				int numMin;
				
				// Try to parse the experiment field
				try {
					experiment = experimentField.getText().toString();
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the experiment name. Make sure it is a string.").show();
					return;
				}
				// Try to parse the number of minutes field
				try {
					numMin = Integer.parseInt(numMinutesField.getText().toString());
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the minutes! Make sure you have a number there.").show();
					return;
				}

				// Create an intent to send over to WifiPollService
				// An intent is an information package to send over to another entity
				Intent wifiServiceIntent = new Intent(MainActivity.this, WifiPollService.class);
				wifiServiceIntent.putExtra(Constants.NUM_MIN, numMin);
				wifiServiceIntent.putExtra(Constants.EXPERIMENT, experiment);
				
				// Prevent the user from pressing the start button again.
				// The start button will be re-enabled by the scanFinishedReceiver
				start.setText("Running...");
				start.setEnabled(false);
				
				// Begin the scanning service
				MainActivity.this.startService(wifiServiceIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
