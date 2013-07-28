package com.skynet.wifimonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver_DataCollection extends BroadcastReceiver {
	private static String TAG = "WifiReceiver";
	private static String url = "http://requestb.in/sxr1q6sx";

	// haha he's also mine!
	private WifiManager myWifiMan;
	private WebRequester myRequester;
	private ArrayList<JSONObject> currentData;
	private static String currentSSID = "skynet";

	public WifiReceiver_DataCollection(WifiManager wifiMan) {
		myWifiMan = wifiMan;
		myRequester = new WebRequester();
		currentData = new ArrayList<JSONObject>();
	}

	public void sendData(int feet) {
		Log.d(TAG, "Sending data");
		JSONObject data = new JSONObject();
		try {
			data.put("feet", feet);
			data.put("data", new JSONArray(currentData));
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put data in json object");
			e.printStackTrace();
		}
		myRequester.sendPost(url, data.toString());
		currentData.clear();
	}

	@Override
	public void onReceive(Context ctx, Intent intnt) {
	/*						^ i hear no vowels is popular */
		try {
			// unix time
			List<ScanResult> theWifis = myWifiMan.getScanResults();
			for (ScanResult wifi : theWifis) {
				if ((wifi.SSID.toLowerCase(Locale.US)).equals(currentSSID)) {
					JSONObject datum = new JSONObject();
					datum.put("time", System.currentTimeMillis() / 1000L);
					datum.put("ssid", wifi.SSID);
					datum.put("strength", wifi.level);
					currentData.add(datum);
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put JSON data");
			e.printStackTrace();
		}
	}
}
