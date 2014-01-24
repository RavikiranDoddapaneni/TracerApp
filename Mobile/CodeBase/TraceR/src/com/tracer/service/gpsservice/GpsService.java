package com.tracer.service.gpsservice;

import com.tracer.util.Prefs;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class GpsService extends Service {

	int i = 0;
	String stringLatitude;
	String stringLongitude;
	SharedPreferences preferences;
	Editor editor;
	boolean isMessageSent;

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		System.out.println("Service Oncreate");
		Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
		preferences = Prefs.get(this);
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		System.out.println("Service onDestroy");
		Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		System.out.println("Service onStartCommand");
		GpsTracker gpsTracker = new GpsTracker(this);

		if (gpsTracker.canGetLocation()) {
			stringLatitude = String.valueOf(gpsTracker.latitude);
			stringLongitude = String.valueOf(gpsTracker.longitude);

		} else {
			gpsTracker.showSettingsAlert();
		}
		i++;
		Toast.makeText(getApplicationContext(), "Service Running ", Toast.LENGTH_SHORT).show();
		System.out.println("Latitude :" + stringLatitude + "&" + "Longitude :" + stringLongitude);

		Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int currentLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		int level = -1;
		if (currentLevel >= 0 && scale > 0) {
			level = (currentLevel * 100) / scale;
		}
		System.out.println("Battery Level Remaining: " + level + "%");

		if (level <= 30) {
			isMessageSent = preferences.getBoolean("isMessageSent", false);
			if (!isMessageSent) {
				String phoneNumber = "9032546466";
				String message = "My Battery is Running below 30% !";
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneNumber, null, message, null, null);
				editor = preferences.edit();
				editor.putBoolean("isMessageSent", true);
				editor.commit();
				System.out.println("Message Sent");
			}
		} else {
			editor = preferences.edit();
			editor.putBoolean("isMessageSent", false);
			editor.commit();
		}

		return super.onStartCommand(intent, flags, startId);
	}
}
