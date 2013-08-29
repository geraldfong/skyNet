package com.skynet.wifimonitor;

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

public class WifiReceiver extends BroadcastReceiver {
	private static String TAG = "WifiReceiver";
	private static String url = "http://requestb.in/1ey606r1";
>>>>>>> Stashed changes

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
			data.put("time", System.currentTimeMillis() / 1000L);
	
			List<ScanResult> theWifis = myWifiMan.getScanResults();
			JSONArray wifiData = new JSONArray();
			for (ScanResult wifi : theWifis) {
				JSONObject wifiDatum = new JSONObject();
				wifiDatum.put("ssid", wifi.SSID);
				wifiDatum.put("strength", wifi.level);
				wifiData.put(wifiDatum);
			}
			data.put("wifiData", wifiData);
			myRequester.sendPost(url, data.toString());
			Log.d(TAG, "Sending JSON Data: ");
			Log.d(TAG, data.toString());
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't put JSON data");
			e.printStackTrace();
		}
	}
}
