package com.skynet.wifimonitor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiPollService extends IntentService {
	private static String TAG = "WifiPollService";

	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
	
	private static final int intervalMs = 500;
	
	public WifiPollService() {
		super("Wifi Poll Service");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Starting te wifi poll service");
		Log.d(TAG, Context.WIFI_SERVICE);
		Log.d(TAG, "Below");
		wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiReceiver = new WifiReceiver(wifiManager);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		int numMin = intent.getIntExtra("numMin", 1);
		String experiment = intent.getStringExtra("experiment");
		wifiReceiver.setExperiment(experiment);
		Log.d(TAG, "Num min " + numMin);
		long curTime = System.currentTimeMillis()/1000L;
		long endTime = curTime + numMin * 60;
		Log.d(TAG, "Started WifiScannerThread");
		while (System.currentTimeMillis()/1000L < endTime) {
			try {
				Thread.sleep(intervalMs);
			} catch (InterruptedException e) {
				Log.d(TAG, "Stopped WifiScannerThread");
				return;
			}
			wifiManager.startScan();
		}
		Log.d(TAG, "Finished scanning");
		
		
	}
	
	
}
