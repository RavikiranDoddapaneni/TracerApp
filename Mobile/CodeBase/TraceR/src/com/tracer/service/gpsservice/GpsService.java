package com.tracer.service.gpsservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.tracer.util.ConnectionChangeReceiver;
import com.tracer.util.Constants;
import com.tracer.util.DataBaseHelper;
import com.tracer.util.Prefs;
import com.tracer.util.Utils;

public class GpsService extends Service {

	String stringLatitude;
	String stringLongitude;
	String stringLocation;
	SharedPreferences preferences;
	Editor editor;
	boolean isMessageSent;
	JSONObject jsonObject;
	JSONObject jsonResponseObject;
	ConnectionChangeReceiver changeReceiver;
	DataBaseHelper dataBaseHelper = DataBaseHelper.getDBAdapterInstance(this);

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		System.out.println("Service Oncreate");
		preferences = Prefs.get(this);
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Service onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		System.out.println("Service onStartCommand");
		GpsTracker gpsTracker = new GpsTracker(this);

		try {
			if (gpsTracker.canGetLocation()) {
				stringLatitude = String.valueOf(gpsTracker.latitude);
				stringLongitude = String.valueOf(gpsTracker.longitude);
				stringLocation = String.valueOf(gpsTracker.getAddressLine(getApplicationContext()));

			} else {
				gpsTracker.showSettingsAlert();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean status = Utils.getConnectivityStatusString(getApplicationContext());
		if (status) {
			new SendLocation().execute(stringLatitude, stringLongitude, stringLocation);
		} else {
			Toast.makeText(getApplicationContext(), "Please check the network connection !.", Toast.LENGTH_SHORT).show();
		}
		System.out.println("Latitude :" + stringLatitude + "&" + "Longitude :" + stringLongitude + "& Location :" + stringLocation);

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
				String phoneNumber = preferences.getString(Constants.TEAMLEADERCONTACTNUMBER, "");
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

	class SendLocation extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {

				System.out.println(urls[0]);
				System.out.println(urls[1]);

				String authCode = preferences.getString(Constants.AUTHCODE, null);

				jsonObject = new JSONObject();
				jsonObject.put("authCode", authCode);
				jsonObject.put("latitude", urls[0]);
				jsonObject.put("longtitude", urls[1]);
				jsonObject.put("location", urls[2]);

				String baseURL = "http://192.168.80.100:8080/TraceR_WS/SaveRunnerLocation/";
				URL url = new URL(baseURL + jsonObject);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(baseURL);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					// Process line...
					System.out.println("line ::::: " + line);
					jsonResponseObject = new JSONObject(line);
					if (jsonResponseObject.has("responseMessage")) {
						System.out.println(jsonResponseObject.getString("responseMessage"));
					}
				}
				wr.close();
				rd.close();

				boolean dbStatus = preferences.getBoolean("hasDBRecords", false);
				if (dbStatus) {
					try {
						dataBaseHelper.checkAndOpenDatabase();
						Cursor cursor = dataBaseHelper.selectRecordsFromDB("Select * from caf_collection_details", null);
						int noOfRecords = cursor.getCount();
						if (noOfRecords > 0) {
							while (cursor.moveToNext()) {
								JSONObject offlineCAFObject = new JSONObject();

								offlineCAFObject.put("authCode", urls[0]);
								offlineCAFObject.put("totalCAF", urls[1]);
								offlineCAFObject.put("acceptedCAF", urls[2]);
								offlineCAFObject.put("rejectedCAF", urls[3]);
								offlineCAFObject.put("returnedCAF", urls[4]);
								offlineCAFObject.put("photo", "");
								offlineCAFObject.put("signature", "");
								offlineCAFObject.put("visitCode", urls[7]);

								String offlinebaseURL = Constants.WEBSERVICE_BASE_URL + "SaveCAFCollectionDetails/";
								URL offlineurl = new URL(offlinebaseURL + offlineCAFObject);
								System.out.println(offlineurl.toString());
								URLConnection offlineconn = offlineurl.openConnection();
								offlineconn.setDoOutput(true);
								OutputStreamWriter wr1 = new OutputStreamWriter(offlineconn.getOutputStream());
								wr1.write(offlinebaseURL);
								wr1.flush();

								// Get the response
								BufferedReader rd1 = new BufferedReader(new InputStreamReader(offlineconn.getInputStream()));
								String line1;
								while ((line1 = rd1.readLine()) != null) {
									// Process line...
									System.out.println("line ::::: " + line1);
									JSONObject jsonObject = new JSONObject(line1);
									String cafResponse = jsonObject.getString("responseMessage");
									System.out.println(jsonObject.toString());

									if (cafResponse.equals("ok")) {
										dataBaseHelper.deleteRecordsInDB("caf_collection_details", "caf_collection_id=?",
												new String[] { String.valueOf(cursor.getInt(0)) });
									}

								}
							}
							editor = preferences.edit();
							editor.putBoolean("hasDBRecords", false);
							editor.commit();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {

					}

				}

				return line;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
