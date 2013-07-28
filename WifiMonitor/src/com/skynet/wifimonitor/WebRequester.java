package com.skynet.wifimonitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class WebRequester {

	public enum WebRequestType {
		GET, POST
	}

	private static String TAG = "WebRequester";
	public WebRequester() {
		
	}

	public void sendPost(String url) {
		sendPost(url, "");
	}

	public void sendPost(String url, String body) {
		RequestSender task = new RequestSender();
		URLData urlData = new URLData(url, body, WebRequestType.POST);
		task.execute(urlData);
	}

	private class RequestSender extends AsyncTask<URLData, Integer, Integer> {
		@Override
		protected Integer doInBackground(URLData... urlDatas) { // proper english? psh
			for (URLData urlData : urlDatas) {
				try {
					URL url = new URL(urlData.url);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					String method;
					switch (urlData.type) {
					case GET:  method = "GET";  break;
					case POST: method = "POST"; break;
					default:
						Log.e(TAG, "I don't know what to do!");
						return 0;
					}
					urlConnection.setRequestMethod(method);
					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					Log.d(TAG, "Sending request: " + urlData);
					writer.write(urlData.body);
					writer.close();

					// this is what actually sends the request
					urlConnection.getInputStream();

					urlConnection.disconnect();
				} catch (MalformedURLException e) {
					Log.e(TAG, "Couldn't send request to " + urlData + " :(");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e(TAG, "Got an IOException when connecting to " + urlData + " :(");
					e.printStackTrace();
				}
			}
			return 1;
		}
	}
	
	private class URLData {
		String url;
		String body;
		WebRequestType type;

		public URLData(String requestUrl, String requestBody, WebRequestType requestType) {
			url = requestUrl;
			body = requestBody;
			type = requestType;
		}
		
		public String toString() {
			return "" + type.name() + ": " + url + "; Body: " + body;
		}
	}
}
