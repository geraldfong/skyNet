package com.skynet.wifimonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";

	public WifiManager wifiManager;

	private EditText experimentField;
	private EditText numMinutesField;
	private Button start;
	
	private String experiment;
	private int numMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		experimentField = (EditText) findViewById(R.id.experiment);
		numMinutesField = (EditText) findViewById(R.id.numMinutes);
		start = (Button) findViewById(R.id.start);
		
		setListeners();
	}

	private void setListeners() {
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					experiment = experimentField.getText().toString();
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the experiment name. Make sure it is a string.").show();
					return;
				}
				try {
					numMin = Integer.parseInt(numMinutesField.getText().toString());
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the minutes! Make sure you have a number there.").show();
					return;
				}

				Intent wifiServiceIntent = new Intent(MainActivity.this, WifiPollService.class);
				wifiServiceIntent.putExtra("numMin", numMin);
				wifiServiceIntent.putExtra("experiment", experiment);
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
