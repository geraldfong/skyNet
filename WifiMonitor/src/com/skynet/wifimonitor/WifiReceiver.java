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
	private static String url = "http://54.241.33.105:8080/android";

	// haha he's also mine!
	private WifiManager myWifiMan;
	private WebRequester myRequester;

	public WifiReceiver(WifiManager wifiMan) {
		myWifiMan = wifiMan;
		myRequester = new WebRequester();
	}
	
	@Override
	public void onReceive(Context ctx, Intent intnt) {
	/*						^ i hear no vowels is popular */
		JSONObject data = new JSONObject();
		Log.d(TAG, "Received data");

		try {
			// unix time
			data.put("time", System.currentTimeMillis());
	
			List<ScanResult> theWifis = myWifiMan.getScanResults();
			JSONObject wifiData = new JSONObject();
			for (ScanResult wifi : theWifis) {
				if (wifi.SSID.toLowerCase(Locale.US).equals("skynet")) {
					wifiData.put(wifi.SSID+"", wifi.level);
				}
			}
			data.put("wifiData", wifiData);
			myRequester.sendPost(url, data.toString());
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put JSON data");
			e.printStackTrace();
		}
	}
}
