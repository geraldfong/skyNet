package com.skynet.wifimonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";
	private static boolean checkInBackground = true;

	/* he's mine! */
	public WifiManager myWifiMan;
	private WifiReceiver receiver;
	private Button requestSender;
	private WebRequester myRequester;
	private Thread scannerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myRequester = new WebRequester();
		myWifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// but I don't want to share him!
		receiver = new WifiReceiver(myWifiMan);

		scannerThread = new WifiScannerThread();
		registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));					
		scannerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		if (!checkInBackground && scannerThread.isAlive()) {
			Log.d(TAG, "Interrupting thread in onPause()");
			scannerThread.interrupt();
		}
		super.onPause();
	}
	
	@Override
	public void onResume() {
		if (!scannerThread.isAlive()) {
			Log.d(TAG, "Starting thread in onResume()");
			scannerThread = new WifiScannerThread();
			scannerThread.start();
		} else {
			Log.d(TAG, "Thread was not interrupted");
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
		private static final int intervalMs = 100;
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
