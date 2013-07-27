package com.example.firstandroidapp;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	private WifiManager mainWifi;
	
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "HI");
		
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		WifiReceiver receiverWifi = new WifiReceiver();
		Thread t = new Thread() {
			public void run() {
				for(int i = 0; i < 2000; i++) {
					//Log.d(TAG, "In the loo");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mainWifi.startScan();
				}
			}
		};
		t.start();
		//mainWifi.startScan();
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			List<ScanResult> wifiList = mainWifi.getScanResults();
			for(ScanResult scanResult : wifiList) {
				//if (scanResult.SSID.equals("Ghostbusters")) {
					Log.d(TAG, scanResult.SSID);
					Log.d(TAG, scanResult.level + "");
					//Log.d(TAG, scanResult.BSSID);
				//}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void sendMessage(View view){
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

}
