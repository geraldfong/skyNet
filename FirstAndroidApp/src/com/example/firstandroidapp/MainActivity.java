package com.example.firstandroidapp;

import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	private WifiManager mainWifi;
	private TextView gainz;
	private TextView ssid;
	private Button resetButton;
	private ArrayList<String> gainzList;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "HI");

		gainzList = new ArrayList<String>();
		gainz = (TextView) findViewById(R.id.gainz);
		ssid = (TextView) findViewById(R.id.ssid);
		resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View blah) {
					gainzList.clear();
					gainz.setText("");
				}
			}
		);

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
				if (scanResult.SSID.equals("SKYNET")/* || scanResult.SSID.equals("skynet")*/) {
					ssid.setText(scanResult.SSID);
					Log.d("achal","hi");
					if (gainzList.size() < 20) {
						gainzList.add(scanResult.level+"");
						gainz.setText(gainzList.get(0));
						for (int i = 1; i < gainzList.size(); i++) {
							gainz.setText(gainz.getText() + "\n" + gainzList.get(i));
						}
					}
					//Log.d(TAG, scanResult.BSSID);
				}
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
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String message = editText.getText().toString();
		//intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

}
