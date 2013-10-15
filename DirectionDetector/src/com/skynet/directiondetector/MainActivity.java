package com.skynet.directiondetector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";
	private static String url = "http://54.241.33.105:8080/direction";

	private SensorManager mSensorManager;
	private RotationListener mRotationListener;
	private Sensor mRotationSensor;
	private int count = 0;

	private WebRequester mRequester;

	private TextView changesX;
	private TextView changesY;
	private Button reset;
	private TextView numDegrees;
	
	private float baseDeg = 0;
	private float currRotX;
	private float currRotY;

	/*
	private ArrayList<Float> rotationsX;
	private ArrayList<Float> rotationsY;
	private int numForStabilization = 30;
	boolean stabilized = false;
	float stableX;
	float stableY;
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		rotationsX = new ArrayList<Float>();
		rotationsY = new ArrayList<Float>();
		*/
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		changesX = (TextView) findViewById(R.id.changes_x);
		changesY = (TextView) findViewById(R.id.changes_y);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mRotationListener = new RotationListener();
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(mRotationListener, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL); // SensorManager.SENSOR_DELAY_FASTEST);

        mRequester = new WebRequester();

        numDegrees = (TextView) findViewById(R.id.numDegrees);
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		restabilize();
        	}
        });
	}

	private void restabilize() {
		/*
		rotationsX.clear();
		rotationsY.clear();
		*/
		baseDeg = 0;
		baseDeg = getDegree();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	private boolean isStable(ArrayList<Float> values) {
		float sum = 0;
		int num = 0;
		for (Float f : values) {
			sum += f;
			num++;
		}
		float avg = sum / (float) num;
		float sd_accum = 0;
		for (Float f : values) {
			sd_accum = square(f - avg);
		}
		float sd = sd_accum / num;
		return sd < 10e-6f;
	}
	
	private boolean areBothStable() {
		return isStable(rotationsX) && isStable(rotationsY); 
	}
	*/
	
	private float getDegree() {
		float rawDeg = ((currRotY < 0 ? -1 : 1 ) * 90 * (currRotX + 1)) + 180;
		float fromBaseDeg = rawDeg - baseDeg;
		return ((fromBaseDeg + 360) % 360);
	}
	
	private void sendDegrees() {
		// numDegrees.set((maxAbsX - xrot))
		float deg = getDegree();
		numDegrees.setText(deg+"");
		mRequester.sendPost(url, "{ \"degrees\" : " + deg + "}");
	}

	private class RotationListener implements SensorEventListener {
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		public void onSensorChanged(SensorEvent e) {
			count++;
			float[] values = e.values;

			float[] matrix = new float[9];
			SensorManager.getRotationMatrixFromVector(matrix, values);
			/*
			if (rotationsX.size() >= numForStabilization) {
				rotationsX.remove(0);
				rotationsY.remove(0);
			}
			*/

			/* they say i should comment code that's hard to understand
			 * but i've only slept 2 hours and am pretty sure using just
			 * this value is not theoretically sound, and if Professor
			 * James O'Brien saw me using this, he'd probably retroactively
			 * fail me in graphics.
			 */
			float rotx = matrix[0];

			// 1st row, 3rd column (matrix[0][2])
			float roty = matrix[3];
			/*
			rotationsX.add(rotx);
			rotationsY.add(roty);
			*/
			// only check after a few values are received
			if (count % 5 == 0) {
				// don't check for stable unless we have enough values
				/*
				if (!stabilized && rotationsX.size() == numForStabilization && count % 10 == 0) {
					if (areBothStable()) {
						Collections.sort(rotationsX);
						Collections.sort(rotationsY);
						stableX = rotationsX.get(rotationsX.size() / 2);
						stableY = rotationsY.get(rotationsY.size() / 2);
						stabilized = true;
						changesX.setText("Stabilized!");
						changesY.setText("Stabilized!");
					}
				} else if (stabilized) {
				*/
					currRotX = rotx;
					currRotY = roty;
					sendDegrees();
				/*
				} else {
					
				}
				*/
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int acc) {
		}
	}

	/*
	private void printMatrix(float[] matrix, int r, int c) {
		for (int i = 0; i < r; i++) {
			String out = "";
			for (int j = 0; j < c; j++) {
				out += (matrix[i*c+j] + " ");
			}
			Log.d(TAG, out+"\n");
		}
	}
	*/

	private float square(float a) { return a*a; }

}
