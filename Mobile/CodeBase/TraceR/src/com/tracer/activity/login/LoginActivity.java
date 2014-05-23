/**
 * @author Prashanth M
 *
 */
package com.tracer.activity.login;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
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

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.service.gpsservice.GpsService;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
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
  static SharedPreferences preferences;
  static Editor editor;
  RelativeLayout loginRelLayout;
  LocationManager manager;
  boolean gpsStatus;
  boolean gprsStatus;
  JSONObject jsonObject;
  JSONObject jsonResponseObject;
  static int RQS = 1;
  String roleType;
  Context context = this;
  private static final String TAG = "LoginActivity";
  CustomizeDialog customizeDialog;

  //==========================================================================
  
  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    TestFlight.log("LoginActivity.onCreate()");
    loginLayout = (RelativeLayout) findViewById(R.id.login_block);
    loginRelLayout = (RelativeLayout) findViewById(R.id.login_layout);
    imageView = (ImageView) findViewById(R.id.appLogoImage);

    /*
     * preferences = Prefs.get(this); DisplayMetrics metrics = new
     * DisplayMetrics();
     * getWindowManager().getDefaultDisplay().getMetrics(metrics); int
     * screenHeight = metrics.heightPixels; int screenWidth =
     * metrics.widthPixels; Log.i("MY", "Actual Screen Height = " + screenHeight
     * + " Width = " + screenWidth);
     *//**
     * Checking whether Gps and Gprs are enabled or not. If in Disable state
     * enabling them.
     */
    /*
     * manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     * gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
     * gprsStatus = Utils.getConnectivityStatusString(getApplicationContext());
     * Log.i("LoginActivity", "GPRS :" + gprsStatus + "GPS :" + gpsStatus);
     * 
     * 
     * if (!gpsStatus) { turnGPSOn(); }
     * 
     * 
     * if (!gprsStatus) { setMobileDataEnableOrDisable(getApplicationContext(),
     * true); }
     *//**
     * Below code is used to display animation for the App logo on the App
     * launch Screen
     */
    /*
     * 
     * TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0,
     * -screenHeight / 3); tAnimation.setDuration(2500);
     * tAnimation.setRepeatCount(0); tAnimation.setInterpolator(new
     * AccelerateDecelerateInterpolator()); tAnimation.setFillAfter(true);
     * tAnimation.setAnimationListener(new AnimationListener() {
     *//** Called when the animation is started. */
    /*
     * @Override public void onAnimationStart(Animation animation) {
     * 
     * }
     * 
     * @Override public void onAnimationRepeat(Animation animation) {
     * 
     * }
     *//** Called when the animation is completed. */
    /*
     * @Override public void onAnimationEnd(Animation animation) { try { boolean
     * status = preferences.getBoolean(Constants.LOGIN_STATUS, false);
     * Log.i("Login Activity Login Status", status + ""); if (status) { roleType
     * = preferences.getString(Constants.USERTYPE, "TSE"); if
     * (roleType.equals("TSE")) { Log.i("Login Activity",
     * "Login Activity to Runner Home Activity"); startActivity(new
     * Intent(getApplicationContext(), RunnerHomeActivity.class));
     * overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim); }
     * else if (roleType.equals("TSM")) { Log.i("Login Activity",
     * "Login Activity to Runners Activity"); startActivity(new
     * Intent(getApplicationContext(), RunnersActivity.class));
     * overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim); }
     * else { Animation fadeIn = new AlphaAnimation(0, 1);
     * fadeIn.setInterpolator(new DecelerateInterpolator());
     * fadeIn.setDuration(500); Log.i("Login Activity",
     * "Login Activity Enable Login"); loginLayout.setAnimation(fadeIn);
     * loginLayout.setVisibility(View.VISIBLE); } } else { Animation fadeIn =
     * new AlphaAnimation(0, 1); fadeIn.setInterpolator(new
     * DecelerateInterpolator()); fadeIn.setDuration(500);
     * Log.i("Login Activity", "Login Activity Enable Login");
     * loginLayout.setAnimation(fadeIn);
     * loginLayout.setVisibility(View.VISIBLE); } } catch (Exception e) {
     * TestFlight.log("LoginActivity.onAnimationEnd() catch Exception " +
     * e.getMessage()); Log.e(TAG,
     * "LoginActivity.onAnimationEnd(): Animation End. catch IOException" +
     * e.getMessage()); } } }); Log.i("Login Activity", "Enable Screen");
     * imageView.startAnimation(tAnimation); username = (EditText)
     * findViewById(R.id.login_username); password = (EditText)
     * findViewById(R.id.login_password);
     * TestFlight.passCheckpoint("LoginActivity.onCreate()");
     */
  }

  //==========================================================================
  
  /**
   * Method will be called when user clicks on login button.
   * 
   * @param view
   */
  public void login(View view) {
    TestFlight.log("LoginActivity.loginSubmit()");
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
    TestFlight.passCheckpoint("LoginActivity.loginSubmit()");
  }

  //==========================================================================
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void setMobileDataEnableOrDisable(Context context, boolean enabled) {
    
    try {
      TestFlight.log("LoginActivity.setMobileDataEnableOrDisable()");
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
    TestFlight.passCheckpoint("LoginActivity.setMobileDataEnableOrDisable()");
  }

  //==========================================================================
  
  public static void setGpsEnableOrDisable(Context context, boolean enabled) {
    Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
    intent.putExtra("enabled", enabled);
    context.sendBroadcast(intent);
  }

  //==========================================================================
  
  class RetreiveLoginResponse extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;

    //==========================================================================
    
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      try {
        pDialog.dismiss();

        if (jsonResponseObject != null && jsonResponseObject.has("errorMessage")) {
          boolean status = jsonResponseObject.has("errorMessage");
          if (status) {
            if ((jsonResponseObject.get("errorMessage").toString()).equalsIgnoreCase(Constants.INVALID_ACCESS_TIME)) {
              Toast.makeText(getApplicationContext(), "Invalid Access Time", Toast.LENGTH_LONG).show();
            } else if ((jsonResponseObject.get("errorMessage").toString()).equalsIgnoreCase(Constants.INVALID_CREDENTIALS)) {
              Toast.makeText(getApplicationContext(), "Please check username and password", Toast.LENGTH_LONG).show();
            } else if ((jsonResponseObject.get("errorMessage").toString()).equalsIgnoreCase(Constants.USER_ALREADY_LOGGED_IN)) {
              Toast.makeText(getApplicationContext(), "User Already logged In", Toast.LENGTH_LONG).show();
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //==========================================================================
    
    protected String doInBackground(String... urls) {
      
      try {
        TestFlight.log("LoginActivity.RetreiveLoginResponse()");

        // Limit
        HttpResponse response = null;
        
        try {
          HttpClient client = new DefaultHttpClient();
          HttpConnectionParams.setConnectionTimeout(client.getParams(), 50000); // Timeout
          HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "user/authenticate");
          jsonObject = new JSONObject();
          jsonObject.put(Constants.LOGIN_USERNAME, urls[0]);
          jsonObject.put(Constants.LOGIN_PASSWORD, urls[1]);
          StringEntity se = new StringEntity(jsonObject.toString());
          se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
          post.setEntity(se);
          response = client.execute(post);
        } catch (Exception e) {

        } finally {
        }

        /* Checking response */
        if (response != null) {
          /* Get the data in the entity */
          InputStream in = response.getEntity().getContent();
          BufferedReader rd = new BufferedReader(new InputStreamReader(in));
          String line;
          while ((line = rd.readLine()) != null) {
            // Process line...
            //System.out.println ("line ::::: " + line);
            jsonResponseObject = new JSONObject(line);
            if (jsonResponseObject.has(Constants.AUTHCODE)) {
              roleType = jsonResponseObject.getString(Constants.USERTYPE);
              editor = preferences.edit();
              editor.putString(Constants.AUTHCODE, jsonResponseObject.getString(Constants.AUTHCODE));
              editor.putString(Constants.USERTYPE, jsonResponseObject.getString(Constants.USERTYPE));
              editor.putString(Constants.USERNAME, jsonResponseObject.getString(Constants.USERNAME));
              editor.putString(Constants.TEAMLEADERCONTACTNUMBER, Constants.TEAMLEADERCONTACTNUMBER);
              editor.putBoolean(Constants.LOGIN_STATUS, true);
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
          rd.close();
          return line;
        }
      } catch (Exception e) {
        TestFlight.log("LoginActivity.RetreiveLoginResponse() catch Exception " + e.getMessage());
        Log.e(TAG, "LoginActivity.RetreiveLoginResponse(): Failed to Login. catch IOException" + e.getMessage());
      }
      TestFlight.passCheckpoint("LoginActivity.RetreiveLoginResponse()");
      return entered_password;
    }

    //==========================================================================
    
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(LoginActivity.this);
      pDialog.setMessage("Loading Please Wait ...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
      pDialog.show();
    }
  }

  //==========================================================================
  
  @Override
  public void onResume() {
    super.onResume();
  }

  //==========================================================================
  
  public static void stopAlarmManagerService(final Context context) {
    TestFlight.log("LoginActivity.stopAlarmManagerService()");
    new Thread(new Runnable() {
      public void run() {
        try {
          HttpClient client = new DefaultHttpClient();
          HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
          HttpResponse response;
          Log.i("LoginActivity", Constants.WEBSERVICE_BASE_URL + "user/logout/" + preferences.getString(Constants.AUTHCODE, ""));
          HttpGet get = new HttpGet(Constants.WEBSERVICE_BASE_URL + "user/logout/" + preferences.getString(Constants.AUTHCODE, ""));
          response = client.execute(get);

          preferences = Prefs.get(context);
          editor = preferences.edit();
          editor.clear();
          editor.commit();
          //System.out.println ("Cleared");

          /* Checking response */
          if (response != null) {
            InputStream in = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            String line;
            
            while ((line = rd.readLine()) != null) {
              JSONObject jsonObject = new JSONObject(line);
              
              if (jsonObject.get("responseMessage").equals("ok")) {
                Intent intent = new Intent(context, GpsService.class);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                // cancelling the request
                alarmManager.cancel(PendingIntent.getService(context, RQS, intent, PendingIntent.FLAG_UPDATE_CURRENT));
              }
            }
          }
        } catch (Exception e) {
          TestFlight.log("LoginActivity.stopAlarmManagerService() catch Exception " + e.getMessage());
          Log.e(TAG, "LoginActivity.stopAlarmManagerService(): Failed to stop Alarm Manager. catch IOException" + e.getMessage());
        }

      }
    }).start();
    TestFlight.passCheckpoint("LoginActivity.stopAlarmManagerService()");
  }

  //==========================================================================
  
  @Override
  public void onBackPressed() {
    Log.i("LoginActivity", "Finishing");
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  //==========================================================================
  
  public void createAlert(String message) {
    CustomizeDialog customizeDialog = new CustomizeDialog(context);
    customizeDialog.setTitle("New CAF Collection");
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();
  }

  //==========================================================================
  
  public static void sendLongSMS(String phoneNumber, String message) {
    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> parts = smsManager.divideMessage(message);
    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
  }

  //==========================================================================
  
  @Override
  protected void onStart() {
    super.onStart();
    preferences = Prefs.get(this);
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    int screenHeight = metrics.heightPixels;
    int screenWidth = metrics.widthPixels;
    Log.i("MY", "Actual Screen Height = " + screenHeight + " Width = " + screenWidth);
    loginLayout.setVisibility(View.INVISIBLE);

    /**
     * Checking whether Gps and Gprs are enabled or not. If in Disable state
     * enabling them.
     */
    manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    gprsStatus = Utils.getConnectivityStatusString(getApplicationContext());
    Log.i("LoginActivity", "GPRS :" + gprsStatus + "GPS :" + gpsStatus);

    /*
     * if (!gpsStatus) { turnGPSOn(); }
     */

    if (!gprsStatus) {
      setMobileDataEnableOrDisable(getApplicationContext(), true);
    }
    /**
     * Below code is used to display animation for the App logo on the App launch Screen
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
        try {
          boolean status = preferences.getBoolean(Constants.LOGIN_STATUS, false);
          Log.i("Login Activity Login Status", status + "");
          
          if (status) {
            roleType = preferences.getString(Constants.USERTYPE, "TSE");
            
            if (roleType.equals("TSE")) {
              Log.i("Login Activity", "Login Activity to Runner Home Activity");
              startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
              overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
            } else if (roleType.equals("TSM")) {
              Log.i("Login Activity", "Login Activity to Runners Activity");
              startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
              overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
            } else {
              Animation fadeIn = new AlphaAnimation(0, 1);
              fadeIn.setInterpolator(new DecelerateInterpolator());
              fadeIn.setDuration(500);
              Log.i("Login Activity", "Login Activity Enable Login");
              loginLayout.setAnimation(fadeIn);
              loginLayout.setVisibility(View.VISIBLE);
            }
          } else {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);
            Log.i("Login Activity", "Login Activity Enable Login");
            loginLayout.setAnimation(fadeIn);
            loginLayout.setVisibility(View.VISIBLE);
          }
        } catch (Exception e) {
          TestFlight.log("LoginActivity.onAnimationEnd() catch Exception " + e.getMessage());
          Log.e(TAG, "LoginActivity.onAnimationEnd(): Animation End. catch IOException" + e.getMessage());
        }
      }
    });
    Log.i("Login Activity", "Enable Screen");
    imageView.startAnimation(tAnimation);
    username = (EditText) findViewById(R.id.login_username);
    password = (EditText) findViewById(R.id.login_password);
    TestFlight.passCheckpoint("LoginActivity.onCreate()");
  }
  
  //==========================================================================
}
