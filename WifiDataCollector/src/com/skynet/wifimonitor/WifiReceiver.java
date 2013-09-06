package com.skynet.wifimonitor;

import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
	private static String TAG = "WifiReceiver";
	private static String url = "http://54.215.14.147:5000/data";

	private static String currentSSID = "skynet";
	
	private WifiManager wifiManager;
	private WebRequester webRequester;
	
	private String experiment;

	public WifiReceiver(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
		this.webRequester = new WebRequester();
		this.experiment = "blah";
	}
	
	public void setExperiment(String experiment) {
		Log.d(TAG, "Setting experiment to " + experiment);
		this.experiment = experiment;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received data");

		try {
			List<ScanResult> scanResults = wifiManager.getScanResults();
			for (ScanResult scanResult : scanResults) {
				if ((scanResult.SSID.toLowerCase(Locale.US)).equals(currentSSID)) {
					JSONObject data = new JSONObject();
					data.put("timestamp", System.currentTimeMillis() / 1000L);
					data.put("experiment", this.experiment);
					Log.d(TAG, "Current experiment is: " + this.experiment);
					data.put("sensor_id", scanResult.SSID);
					data.put("value", scanResult.level);
					data.put("sensor_type", "wifi");
					Log.d(TAG, "Sending data" + data.toString());
					webRequester.sendPost(url, "data=" + data.toString());
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put JSON data");
			e.printStackTrace();
		}
	}
}
