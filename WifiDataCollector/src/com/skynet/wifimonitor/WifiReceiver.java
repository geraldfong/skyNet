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
	private static String url = "http://requestb.in/1i2r7kn1";

	private static String currentSSID = "skynet";
	
	private WifiManager wifiManager;
	private WebRequester webRequester;
	
	private String experiment;

	public WifiReceiver(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
		this.webRequester = new WebRequester();
		this.experiment = "";
	}
	
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		JSONObject data = new JSONObject();
		Log.d(TAG, "Received data");

		try {
			// unix time
			data.put("time", System.currentTimeMillis() / 1000L);
	
			List<ScanResult> scanResults = wifiManager.getScanResults();
			JSONArray wifiData = new JSONArray();
			for (ScanResult scanResult : scanResults) {
				if ((scanResult.SSID.toLowerCase(Locale.US)).equals(currentSSID)) {
					JSONObject datum = new JSONObject();
					datum.put("timestamp", System.currentTimeMillis() / 1000L);
					//datum.put("experiment", this.experiment);
					datum.put("sensor_id", scanResult.SSID);
					datum.put("value", scanResult.level);
					datum.put("sensor_type", "wifi");
					webRequester.sendPost(url, "data=" + data.toString());
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put JSON data");
			e.printStackTrace();
		}
	}
}
