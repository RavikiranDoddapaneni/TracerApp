package com.tracer.activity.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

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
import com.tracer.util.Constants;
import com.tracer.util.Prefs;

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

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginLayout = (RelativeLayout) findViewById(R.id.login_block);
		loginRelLayout = (RelativeLayout) findViewById(R.id.login_layout);
		imageView = (ImageView) findViewById(R.id.appLogoImage);

		preferences = Prefs.get(this);

		/**
		 * Checking whether Gps and Gprs are enabled or not. If in Disable state
		 * enabling them.
		 */
		/*
		 * manager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); gpsStatus =
		 * manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 * 
		 * ConnectivityManager connectivityManager = (ConnectivityManager)
		 * getApplicationContext
		 * ().getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo
		 * activeNetInfo = connectivityManager.getActiveNetworkInfo();
		 * 
		 * gprsStatus = activeNetInfo.isAvailable();
		 * 
		 * // gprsStatus = manager.isProviderEnabled(LocationManager.);
		 * 
		 * System.out.println("GPRS :" + gprsStatus + "GPS :" + gpsStatus);
		 */
		/*
		 * if (!gpsStatus) { setGpsEnableOrDisable(getApplicationContext(),
		 * false); }
		 */
		/*
		 * if (!gprsStatus) {
		 * setMobileDataEnableOrDisable(getApplicationContext(), false); }
		 */
		/**
		 * Below code is used to display animation for the App logo on the App
		 * launch Screen
		 */

		TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, -250);
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
			new RetreiveLoginResponse().execute(entered_username, entered_password);
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
		intent.putExtra("enabled", enabled);
		context.sendBroadcast(intent);
	}

	public static boolean getNetworkStatus(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		boolean gprsStatus = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();

		return gprsStatus;
	}

	class RetreiveLoginResponse extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {

				System.out.println(urls[0]);
				System.out.println(urls[1]);

				jsonObject = new JSONObject();
				jsonObject.put("userName", urls[0]);
				jsonObject.put("password", "11IZkFH57I0BtkxFa48WBw==");
				String baseURL = "http://192.168.80.100:8080/TraceR_WS/GetAuthCode/";
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
					JSONObject jsonObject = new JSONObject(line);
					if (jsonObject.has("authCode")) {
						String roleType = jsonObject.getString(Constants.USERTYPE);
						editor = preferences.edit();
						editor.putString(Constants.AUTHCODE, jsonObject.getString(Constants.AUTHCODE));
						editor.putString(Constants.USERTYPE, jsonObject.getString(Constants.USERTYPE));
						editor.putString(Constants.USERNAME, jsonObject.getString(Constants.USERNAME));
						editor.putString(Constants.TEAMLEADERCONTACTNUMBER, Constants.TEAMLEADERCONTACTNUMBER);
						editor.commit();
						if (roleType.equals("TSE")) {
							startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
							overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
						} else if (roleType.equals("TSM")) {
							startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
							overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
						}

					} else if (jsonObject.has("errorMessage")) {
						Toast.makeText(LoginActivity.this, "Please Login again with Correct credentials", Toast.LENGTH_LONG).show();
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
		overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
	}
}
