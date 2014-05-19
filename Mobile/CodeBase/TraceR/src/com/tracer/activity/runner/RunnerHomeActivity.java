package com.tracer.activity.runner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;
import com.tracer.activity.login.LoginActivity;
import com.tracer.util.Constants;
import com.tracer.util.Prefs;
import com.tracer.util.Utils;

public class RunnerHomeActivity extends ActionBarActivity {
  SharedPreferences prefs;
  String userType;
  String userName;
  String authCode;

  TextView welcomeText;
  TextView userTypeDisplay;

  LocationManager manager;
  boolean gpsStatus;
  boolean gprsStatus;
  Context context = this;

  //==========================================================================
  
  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TestFlight.log("RunnerHomeActivity.onCreate()");
    setContentView(R.layout.activity_runner_home);

    welcomeText = (TextView) findViewById(R.id.welcomeText);
    userTypeDisplay = (TextView) findViewById(R.id.userTypeDisplay);

    prefs = Prefs.get(this);
    userType = prefs.getString(Constants.USERTYPE, null);
    userName = prefs.getString(Constants.USERNAME, null);
    authCode = prefs.getString(Constants.AUTHCODE, null);

    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    gprsStatus = Utils.getConnectivityStatusString(getApplicationContext());
    Log.i("LoginActivity", "GPRS :" + gprsStatus + "GPS :" + gpsStatus);

    if (!gprsStatus) {
      LoginActivity.setMobileDataEnableOrDisable(getApplicationContext(), true);
    }

  /*  if (!gpsStatus) {
      turnGPSOn();
    }*/

    if (userType != null && userType.equals("TSE")) {
      welcomeText.setText("Welcome " + userName);
      userTypeDisplay.setText("You are logged in as Runner");
    } else if (userType != null && userType.equals("TSM")) {
      welcomeText.setText("Welcome " + userName);
      userTypeDisplay.setText("You are logged in as TeamLeader");
    }
    TestFlight.passCheckpoint("RunnerHomeActivity.onCreate()");
  }

  //==========================================================================
  
  /**
   * Method for creating Menus in the current View
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    if (userType.equalsIgnoreCase("TSE")) {
      MenuItem item = menu.findItem(R.id.home_button);
      item.setVisible(false);
    }
    return true;
  }

  //==========================================================================
  
  /**
   * Action to be performed when the clicked on Menu icons.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.home_button) {
      startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    } else if (item.getItemId() == R.id.logout) {
      LoginActivity.stopAlarmManagerService(getApplicationContext());
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    }
    return true;
  }

  //==========================================================================
  
  /**
   * Called when the user clicks on Get Beatplan button.
   * 
   * @param view
   */
  public void getBeatPlan(View view) {
    TestFlight.log("RunnerHomeActivity.getBeatPlan()");
    if (Utils.getConnectivityStatusString(getApplicationContext())) {
      startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
      overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
    } else {
      Toast.makeText(getApplicationContext(), R.string.check_network_connection, Toast.LENGTH_LONG).show();
    }
    TestFlight.passCheckpoint("RunnerHomeActivity.getBeatPlan()");
  }

  //==========================================================================
  
  @Override
  public void onBackPressed() {
    if (userType.equals("TSE")) {
      Log.i("RunnerHomeActivity", "Finishing");
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    } else if (userType.equals("TSM")) {
      Intent intent = new Intent(getApplicationContext(), RunnersActivity.class);
      startActivity(intent);
    }
  }

  //==========================================================================
  
  public void turnGPSOn() {
    @SuppressWarnings("deprecation")
	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if (!provider.contains("gps")) { // if gps is disabled final
      Intent poke = new Intent();
      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
      poke.setData(Uri.parse("3"));
      sendBroadcast(poke);
    }
  }

  //==========================================================================
  
  public void turnGPSOff() {
    @SuppressWarnings("deprecation")
	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if (provider.contains("gps")) { // if gps is enabled final
      Intent poke = new Intent();
      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
      poke.setData(Uri.parse("3"));
      sendBroadcast(poke);
    }
  }
  
  //==========================================================================
}
