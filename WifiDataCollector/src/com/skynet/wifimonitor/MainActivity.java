package com.skynet.wifimonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";
	private static boolean checkInBackground = false;

	/* he's mine! */
	public WifiManager myWifiMan;
	private WifiReceiver_DataCollection receiver;
	private Thread scannerThread;

	private EditText numFeetField;
	private EditText numMinutesField;
	private Button start;
	
	private int numFeet;
	private int numMin;

	private boolean paused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myWifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		numFeetField = (EditText) findViewById(R.id.numFeet);
		numMinutesField = (EditText) findViewById(R.id.numMinutes);
		start = (Button) findViewById(R.id.start);
		setListeners();

		// but I don't want to share him!
		receiver = new WifiReceiver_DataCollection(myWifiMan);
		registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	private void setListeners() {
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					numFeet = Integer.parseInt(numFeetField.getText().toString());
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the feet! Make sure you have a number there.").show();
					return;
				}
				try {
					numMin = Integer.parseInt(numMinutesField.getText().toString());
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this).setMessage("Couldn't parse the minutes! Make sure you have a number there.").show();
					return;
				}
				scannerThread = new WifiScannerThread();
				scannerThread.start();
				start.setText("Running...");
				start.setEnabled(false);
				
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						receiver.sendData(numFeet, numMin);
						scannerThread.interrupt();
						paused = true;
						start.setText("Start");
						start.setEnabled(true);
					}
				}, numMin * 60 * 1000L);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		if (!checkInBackground && scannerThread != null && scannerThread.isAlive()) {
			Log.d(TAG, "Interrupting thread in onPause()");
			scannerThread.interrupt();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		if (scannerThread != null && !scannerThread.isAlive() && !paused) {
			Log.d(TAG, "Starting thread in onResume()");
			scannerThread = new WifiScannerThread();
			scannerThread.start();
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (scannerThread.isAlive()) {
			Log.d(TAG, "Stopping thread in onDestroy()");
			scannerThread.interrupt();
		}
		super.onDestroy();
	}

	private class WifiScannerThread extends Thread {
		private static final int intervalMs = 500;
		public void run() {
			Log.d(TAG, "Started WifiScannerThread");
			while (true) {
				try {
					Thread.sleep(intervalMs);
				} catch (InterruptedException e) {
					Log.d(TAG, "Stopped WifiScannerThread");
					return;
				}
				myWifiMan.startScan();
			}
		}
	}

}
