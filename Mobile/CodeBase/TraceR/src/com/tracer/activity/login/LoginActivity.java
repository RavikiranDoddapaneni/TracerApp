package com.tracer.activity.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.service.gpsservice.GpsService;
import com.tracer.util.Constants;
import com.tracer.util.Prefs;
import com.tracer.util.Utils;

public class LoginActivity extends ActionBarActivity {

	ActionBar actionBar;
	EditText username;
	EditText password;
	String entered_username;
	String entered_password;
	RelativeLayout loginLayout;
	ImageView imageView;
	SharedPreferences preferences;
	Editor editor;
	RelativeLayout loginRelLayout;
	LocationManager manager;
	boolean gpsStatus;
	boolean gprsStatus;
	JSONObject jsonObject;
	JSONObject jsonResponseObject;
	static int RQS = 1;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginLayout = (RelativeLayout) findViewById(R.id.login_block);
		loginRelLayout = (RelativeLayout) findViewById(R.id.login_layout);
		imageView = (ImageView) findViewById(R.id.appLogoImage);

		preferences = Prefs.get(this);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int screenHeight = metrics.heightPixels;
		int screenWidth = metrics.widthPixels;
		Log.i("MY", "Actual Screen Height = " + screenHeight + " Width = " + screenWidth);
		/**
		 * Checking whether Gps and Gprs are enabled or not. If in Disable state
		 * enabling them.
		 */

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		gprsStatus = activeNetInfo.isAvailable();

		// gprsStatus = manager.isProviderEnabled(LocationManager.);

		System.out.println("GPRS :" + gprsStatus + "GPS :" + gpsStatus);

		if (!gprsStatus) {
			setMobileDataEnableOrDisable(getApplicationContext(), true);
		}

		/**
		 * Below code is used to display animation for the App logo on the App
		 * launch Screen
		 */

		TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, -screenHeight / 3);
		tAnimation.setDuration(2500);
		tAnimation.setRepeatCount(0);
		tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		tAnimation.setFillAfter(true);
		tAnimation.setAnimationListener(new AnimationListener() {

			/** Called when the animation is started. */
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			/** Called when the animation is completed. */
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new DecelerateInterpolator());
				fadeIn.setDuration(500);
				loginLayout.setAnimation(fadeIn);
				loginLayout.setVisibility(View.VISIBLE);
			}
		});

		imageView.startAnimation(tAnimation);
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);

	}

	/**
	 * Method will be called when user clicks on login button.
	 * 
	 * @param view
	 * 
	 */
	public void login(View view) {
		entered_username = username.getText().toString();
		entered_password = password.getText().toString();

		if (!entered_username.isEmpty() || !entered_password.isEmpty()) {
			if (Utils.getConnectivityStatusString(getApplicationContext())) {
				new RetreiveLoginResponse().execute(entered_username, entered_password);
			} else {
				Toast.makeText(getApplicationContext(), "Please check the Network Connection!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please enter Username and Password", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setMobileDataEnableOrDisable(Context context, boolean enabled) {
		try {
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setGpsEnableOrDisable(Context context, boolean enabled) {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", false);
		context.sendBroadcast(intent);
	}

	public static boolean getNetworkStatus(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		boolean gprsStatus = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();

		return gprsStatus;
	}

	class RetreiveLoginResponse extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			/*
			 * if (null != jsonResponseObject &
			 * jsonResponseObject.has("errorMessage")) {
			 * Toast.makeText(LoginActivity.this,
			 * "Please Login again with Correct credentials",
			 * Toast.LENGTH_LONG).show(); }
			 */

		}

		protected String doInBackground(String... urls) {
			try {

				System.out.println(urls[0]);
				System.out.println(urls[1]);

				jsonObject = new JSONObject();
				jsonObject.put(Constants.LOGIN_USERNAME, urls[0]);
				jsonObject.put(Constants.LOGIN_PASSWORD, "11IZkFH57I0BtkxFa48WBw==");
				String baseURL = Constants.WEBSERVICE_BASE_URL + "GetAuthCode/";
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
					if (jsonResponseObject.has(Constants.AUTHCODE)) {
						String roleType = jsonResponseObject.getString(Constants.USERTYPE);
						editor = preferences.edit();
						editor.putString(Constants.AUTHCODE, jsonResponseObject.getString(Constants.AUTHCODE));
						editor.putString(Constants.USERTYPE, jsonResponseObject.getString(Constants.USERTYPE));
						editor.putString(Constants.USERNAME, jsonResponseObject.getString(Constants.USERNAME));
						editor.putString(Constants.TEAMLEADERCONTACTNUMBER, Constants.TEAMLEADERCONTACTNUMBER);
						editor.commit();

						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.SECOND, 10);
						Intent intent = new Intent(LoginActivity.this, GpsService.class);
						PendingIntent pintent = PendingIntent.getService(LoginActivity.this, RQS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
						AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
						alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10 * 60 * 1000, pintent);

						if (roleType.equals("TSE")) {
							startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
							overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
						} else if (roleType.equals("TSM")) {
							startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
							overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
						}

					}
				}
				wr.close();
				rd.close();
				return line;
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public static void stopAlarmManagerService(Context context) {
		Intent intent = new Intent(context, GpsService.class);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// cancelling the request
		alarmManager.cancel(PendingIntent.getService(context, RQS, intent, PendingIntent.FLAG_UPDATE_CURRENT));
	}
}
