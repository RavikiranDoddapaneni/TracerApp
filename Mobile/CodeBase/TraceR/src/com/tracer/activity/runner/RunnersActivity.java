package com.tracer.activity.runner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
import com.tracer.util.Prefs;

public class RunnersActivity extends ActionBarActivity {
  ArrayList<HashMap<String, Object>> runnersDataList;
  SharedPreferences prefs;
  RunnerAdapter runnerAdapter;
  ListView runnersList;
  Context context = this;
  String authCode;
  JSONObject jsonObject;
  Editor editor;
  private static final String TAG = "RunnersActivity";

  //==========================================================================
  
  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TestFlight.log("RunnersActivity.onCreate()");
    setContentView(R.layout.activity_runners_list);
    prefs = Prefs.get(this);
    authCode = prefs.getString(Constants.AUTHCODE, null);
    runnersList = (ListView) findViewById(R.id.runners_list);
    new RetreiveBeatPlanResponse().execute(authCode);
    /**
     * Called when the user selects any of the item from the list view.
     */
    runnersList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        TestFlight.log("RunnersActivity.runnerlist.setOnItemClickListener()");
        String runnerStatus = (String) runnersDataList.get(position).get(Constants.IS_PRESENT);
        
        if (runnerStatus != null && runnerStatus.equalsIgnoreCase("false13")) {
          startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
          overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
        } else {
          // Toast.makeText(getApplicationContext(), "Active",
          // Toast.LENGTH_LONG).show();
        }
      }
    });
    TestFlight.passCheckpoint("RunnersActivity.runnerlist.setOnItemClickListener()");
  }

  //==========================================================================
  
  /**
   * Method for creating Menus in the current View
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    MenuItem item = menu.findItem(R.id.home_button);
    item.setVisible(false);
    return true;
  }

  //==========================================================================
  
  /**
   * Action to be performed when the clicked on Menu icons.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.logout) {
      LoginActivity.stopAlarmManagerService(getApplicationContext());
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    }
    return true;
  }

  //==========================================================================
  
  class RetreiveBeatPlanResponse extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;

    //==========================================================================
    
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      pDialog.dismiss();
      if (runnersDataList != null && runnersDataList.size() > 0) {
        runnerAdapter = new RunnerAdapter(context, runnersDataList);
        runnersList.setAdapter(runnerAdapter);
      } else {
        createAlert("No Data Available", "Runners");
      }
    }
    
    //==========================================================================

    protected String doInBackground(String... urls) {
      
      try {
        TestFlight.log("RunnersActivity.RetreiveBeatPlanResponse()");
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
        HttpResponse response;
        HttpGet get = new HttpGet(Constants.WEBSERVICE_BASE_URL + "user/runners/get/" + urls[0]);
        response = client.execute(get);
        
        if (response != null) {
          InputStream in = response.getEntity().getContent();
          BufferedReader rd = new BufferedReader(new InputStreamReader(in));
          String line;
         
          while ((line = rd.readLine()) != null) {
            // Process line...
            JSONObject jsonObject = new JSONObject(line);
            JSONArray runnerObject = jsonObject.getJSONArray(Constants.RUNNERS);
            runnersDataList = new ArrayList<HashMap<String, Object>>();
            
            for (int i = 0; i < runnerObject.length(); i++) {
              HashMap<String, Object> map = new HashMap<String, Object>();
              JSONObject distObject = runnerObject.getJSONObject(i);
              map.put(Constants.CONTACT_NUMBER, distObject.getString(Constants.CONTACT_NUMBER));
              map.put(Constants.CAFCOUNT, distObject.getString(Constants.CAFCOUNT));
              map.put(Constants.RUNNERNAME, distObject.getString(Constants.RUNNERNAME));
              map.put(Constants.IS_PRESENT, distObject.getString(Constants.IS_PRESENT));
              map.put(Constants.RUNNERCODE, distObject.getString(Constants.RUNNERCODE));
              runnersDataList.add(map);
            }
          }
          rd.close();
        }
        TestFlight.passCheckpoint("RunnersActivity.RetreiveBeatPlanResponse()");
      } catch (Exception e) {
        TestFlight.log("RunnersActivity.RetreiveBeatPlanResponse() catch Exception " + e.getMessage());
        Log.e(TAG, "RunnersActivity.RetreiveBeatPlanResponse():" + e.getMessage());
      } finally {
        /*
         * if (Utils.getConnectivityStatusString(getApplicationContext())) {
         * Toast.makeText(getApplicationContext(),
         * "Please check the Network Connection and Try again!",
         * Toast.LENGTH_SHORT).show(); }
         */
      }
      return authCode;
    }

    //==========================================================================
    
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(RunnersActivity.this);
      pDialog.setMessage("Getting Data ...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
      pDialog.show();
    }
  }

  //==========================================================================
  
  @Override
  public void onBackPressed() {
    Log.i("RunnersActivity", "Finishing");
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  //==========================================================================
  
  public void createAlert(String message, String title) {
    CustomizeDialog customizeDialog = new CustomizeDialog(context);
    customizeDialog.setTitle(title);
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();

  }
  
  //==========================================================================
}
