package com.tracer.service.gpsservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
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

import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
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
	Context context = this;
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
		System.out.println(status);
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
			isMessageSent = preferences.getBoolean("isMessageSent", true);
			if (!isMessageSent) {
				String phoneNumber = preferences.getString(Constants.TEAMLEADERCONTACTNUMBER, "");
				String message = "My Battery is Running below 30% !";
				sendLongSMS(phoneNumber, message);
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

				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
				HttpResponse response;
				HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "user/runner/location/save");

				System.out.println(urls[0]);
				System.out.println(urls[1]);

				String authCode = preferences.getString(Constants.AUTHCODE, null);
				jsonObject = new JSONObject();
				jsonObject.put("authCode", authCode);
				jsonObject.put("lattitude", urls[0]);
				jsonObject.put("longitude", urls[1]);
				jsonObject.put("location", URLEncoder.encode(urls[2], "UTF-8"));

				StringEntity se = new StringEntity(jsonObject.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);

				/* Checking response */
				if (response != null) {
					InputStream in = response.getEntity().getContent(); // Get the
					BufferedReader rd = new BufferedReader(new InputStreamReader(in));
					String line;
					while ((line = rd.readLine()) != null) {
						// Process line...
						System.out.println("line ::::: " + line);
						jsonResponseObject = new JSONObject(line);
						if (jsonResponseObject.has("responseMessage")) {
							if (jsonResponseObject.has("Logout")) {
								if (jsonResponseObject.getString("Logout").equalsIgnoreCase("true")) {
									LoginActivity.stopAlarmManagerService(getApplicationContext());
									startActivity(new Intent(getApplicationContext(), LoginActivity.class));
									((Activity) context).overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
								}
							}
						}
					}
					rd.close();
				}

				boolean dbStatus = preferences.getBoolean("hasDBRecords", false);
				if (dbStatus) {
					try {
						dataBaseHelper.checkAndOpenDatabase();
						Cursor cursor = dataBaseHelper.selectRecordsFromDB("Select * from caf_collection_details", null);
						int noOfRecords = cursor.getCount();
						if (noOfRecords > 0) {
							while (cursor.moveToNext()) {

								HttpClient offlineCAFClient = new DefaultHttpClient();
								HttpConnectionParams.setConnectionTimeout(offlineCAFClient.getParams(), 10000);
								HttpResponse offlineCAFresponse;
								HttpPost offlineCAFPost = new HttpPost(Constants.WEBSERVICE_BASE_URL + "caf/save");

								JSONObject offlineCAFObject = new JSONObject();

								offlineCAFObject.put("authCode", urls[0]);
								offlineCAFObject.put("totalCAF", urls[1]);
								offlineCAFObject.put("acceptedCAF", urls[2]);
								offlineCAFObject.put("rejectedCAF", urls[3]);
								offlineCAFObject.put("returnedCAF", urls[4]);
								offlineCAFObject.put("photo", "");
								offlineCAFObject.put("signature", "");
								offlineCAFObject.put("visitCode", urls[7]);

								StringEntity stringEntity = new StringEntity(jsonObject.toString());
								stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
								offlineCAFPost.setEntity(stringEntity);
								offlineCAFresponse = offlineCAFClient.execute(post);

								/* Checking response */
								if (offlineCAFresponse != null) {
									InputStream in = offlineCAFresponse.getEntity().getContent(); // Get
																																								// the
									BufferedReader rd = new BufferedReader(new InputStreamReader(in));
									String line;
									while ((line = rd.readLine()) != null) {
										// Process line...
										System.out.println("line ::::: " + line);
										JSONObject jsonObject = new JSONObject(line);
										String cafResponse = jsonObject.getString("responseMessage");
										System.out.println(jsonObject.toString());

										if (cafResponse.equals("ok")) {
											dataBaseHelper.deleteRecordsInDB("caf_collection_details", "caf_collection_id=?",
													new String[] { String.valueOf(cursor.getInt(0)) });
										}
									}
									rd.close();
								}
							}
							editor = preferences.edit();
							editor.putBoolean("hasDBRecords", false);
							editor.commit();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dataBaseHelper.close(); // Closing database connection
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stringLatitude;
		}
	}

	public static void sendLongSMS(String phoneNumber, String message) {
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> parts = smsManager.divideMessage(message);
		smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
	}
}
