package com.skynet.wifimonitor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class WifiPollService extends IntentService {
	private static String TAG = "WifiPollService";

	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
	
	public WifiPollService() {
		super("Wifi Poll Service");
	}
	
	/** Save a copy of the wifiManager after getting it from the application context. Also create
	 *  a version of the wifiReceiver that will be used throughout this service. 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new WifiReceiver(wifiManager);
	}

	/** Begin scanning in the background for a given number of minutes with an experiment name.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		int numMin = intent.getIntExtra(Constants.NUM_MIN, Constants.DEFAULT_NUM_MIN);
		String experiment = intent.getStringExtra(Constants.EXPERIMENT);
		
		wifiReceiver.setExperiment(experiment);
		long curTime = System.currentTimeMillis()/1000L;
		long endTime = curTime + numMin * 60;
		
		// Register receiver and enter scanning cycle.
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.d(TAG, "Starting to scan");
		while (System.currentTimeMillis()/1000L < endTime) {
			try {
				Thread.sleep(Constants.SCAN_INTERVAL_MS);
			} catch (InterruptedException e) {
				Log.d(TAG, "Stopped WifiScannerThread");
				return;
			}
			wifiManager.startScan();
		}
		Log.d(TAG, "Finished scanning");
		unregisterReceiver(wifiReceiver);
		
		// Create an intent to send data back to the original activity that we have finished the task
		Intent finishIntent = new Intent(Constants.BROADCAST_FINISHED);
		LocalBroadcastManager.getInstance(this).sendBroadcast(finishIntent);
	}
	
	
}
