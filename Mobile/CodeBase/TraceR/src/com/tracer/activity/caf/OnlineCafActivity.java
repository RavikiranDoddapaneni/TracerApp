/**
 * @author Jp
 *
 */
package com.tracer.activity.caf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;
import com.tracer.activity.login.LoginActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
import com.tracer.util.Prefs;

public class OnlineCafActivity extends ActionBarActivity {
  SharedPreferences prefs;
  String userType;
  String authCode;
  Context context = this;
  Bundle bundle;
  Long distributorId;
  JSONObject jsonObj;
  JSONArray uploadedCafJSONArray;

  OnlineCAFAdapter onlineCAFAdapter;
  ArrayList<HashMap<String, Object>> onlineCafsList;
  ListView onlineCafsListView;

  private static final String TAG = "OnlineCafActivity";

  // ==========================================================================

  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "START of the method onCreate");
    setContentView(R.layout.activity_online_caf);

    prefs = Prefs.get(this);
    authCode = prefs.getString(Constants.AUTHCODE, null);
    userType = prefs.getString(Constants.USERTYPE, null);
    bundle = this.getIntent().getExtras();
    distributorId = Long.parseLong(bundle.getString(Constants.DISTRIBUTOR_ID));
    onlineCafsListView = (ListView) findViewById(R.id.onlineCafsList);
    new RetreiveOnlineCAFs().execute(authCode);
    Log.i(TAG, "END of the method onCreate");
  }

  // ==========================================================================

  /**
   * Method for creating Menus in the current View
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  // ==========================================================================

  /**
   * Action to be performed when the clicked on Menu icons.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.i(TAG, "START of the method onOptionsItemSelected");
    if (item.getItemId() == android.R.id.home) {
      finish();
      startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    } else if (item.getItemId() == R.id.home_button) {
      if (userType.equalsIgnoreCase("TSM")) {
        startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
      } else if (userType.equalsIgnoreCase("TSM")) {
        startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
      } else if (userType.equalsIgnoreCase("TSE")) {
        startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
      }
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    } else if (item.getItemId() == R.id.logout) {
      LoginActivity.stopAlarmManagerService(getApplicationContext());
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    }
    Log.i(TAG, "END of the method onOptionsItemSelected");
    return true;
  }

  // ==========================================================================

  class RetreiveOnlineCAFs extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;

    // ==========================================================================

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      pDialog.dismiss();
      
      if (onlineCafsList != null && onlineCafsList.size() > 0) {
        onlineCAFAdapter = new OnlineCAFAdapter(OnlineCafActivity.this, onlineCafsList);
        onlineCafsListView.setAdapter(onlineCAFAdapter);
        
        onlineCafsListView.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Dialog dialog = new Dialog(context);
            final int clickedPosition = position;
            
            dialog.setContentView(R.layout.activity_online_caf_status);
            dialog.setTitle("Online CAF Status");
            final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.onlineCafRadioGroup);
            HashMap<String, Object> clickedItemMap = onlineCafsList.get(position);
            String cafStatus = (String) clickedItemMap.get(Constants.CAF_STATUS);
            
            if(cafStatus != null) {
             switch (Integer.parseInt(cafStatus)) {
               case 1: ((RadioButton) dialog.findViewById(R.id.radioButton1)).setChecked(true); break;
               case 2: ((RadioButton) dialog.findViewById(R.id.radioButton2)).setChecked(true); break;
               case 3: ((RadioButton) dialog.findViewById(R.id.radioButton3)).setChecked(true); break;
               case 4: ((RadioButton) dialog.findViewById(R.id.radioButton4)).setChecked(true); break;
               default:break;
             }
            }
            radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        
              @Override
              public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find the radio button by returned id
                RadioButton radioButton = (RadioButton) dialog.findViewById(radioGroup.getCheckedRadioButtonId());
                dialog.dismiss();
                
                String cafStatusString = (String) radioButton.getText();
                int cafStatus = 0;
                
                if(Constants.ACCEPTED.equalsIgnoreCase(cafStatusString)) {
                  cafStatus = 1;
                } else if(Constants.REJECTED.equalsIgnoreCase(cafStatusString)) {
                  cafStatus = 2;
                } else if(Constants.CUSTOMER_NOT_INTERESTED.equalsIgnoreCase(cafStatusString)) {
                  cafStatus = 3;
                } else if(Constants.INCOMPLETE_DOCUMENTATION.equalsIgnoreCase(cafStatusString)) {
                  cafStatus = 4;
                }
                onlineCafsList.get(clickedPosition).put(Constants.CAF_STATUS_STRING, cafStatusString);
                onlineCafsList.get(clickedPosition).put(Constants.CAF_STATUS, String.valueOf(cafStatus));
                onlineCAFAdapter.notifyDataSetChanged(); // To refresh the list view
              }
            });
            dialog.show();
          }
        });
      } else {
        createAlert("No Data Available", "Online CAF's");
      }
    }

    // ==========================================================================

    @Override
    protected String doInBackground(String... params) {
      Log.e(TAG, "OnlineCafActivity.RetreiveOnlineCAFs()");

      try {
        TestFlight.log("OnlineCafActivity.RetreiveOnlineCAFs()");
        
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); HttpResponse response;
        HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "caf/uploaded/get"); 
        JSONObject inputJSONObj = new JSONObject();
        inputJSONObj.put(Constants.AUTHCODE, authCode);
        
        inputJSONObj.put("distributorId", distributorId);
        Log.i(TAG, inputJSONObj.toString());
        StringEntity se = new StringEntity(inputJSONObj.toString());
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
        post.setEntity(se);
        response = client.execute(post);

        if (response != null) {
          InputStream in = response.getEntity().getContent();
          BufferedReader rd = new BufferedReader(new InputStreamReader(in));
          String line;
          String cafStatus = null;
          String cafStatusString = " ";

          while ((line = rd.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.UPLOADED_CAF);
            onlineCafsList = new ArrayList<HashMap<String, Object>>();

            for (int i = 0; i < jsonArray.length(); i++) {
              HashMap<String, Object> map = new HashMap<String, Object>();
              JSONObject jsonObj = jsonArray.getJSONObject(i);
              cafStatus = jsonObj.getString(Constants.CAF_STATUS);

              map.put(Constants.UPLOAD_CAF_DETAILS_ID, jsonObj.getLong(Constants.UPLOAD_CAF_DETAILS_ID));
              map.put(Constants.MOBILE_NO,jsonObj.getString(Constants.MOBILE_NO));
              map.put(Constants.CAF_STATUS, cafStatus);

              switch (Integer.parseInt(cafStatus)) {
                case 1: cafStatusString = Constants.ACCEPTED; break;
                case 2: cafStatusString = Constants.REJECTED; break;
                case 3: cafStatusString = Constants.CUSTOMER_NOT_INTERESTED; break;
                case 4: cafStatusString = Constants.INCOMPLETE_DOCUMENTATION; break;
                default: cafStatusString = "Click to update "; break;
              }
              map.put(Constants.CAF_STATUS_STRING,cafStatusString);
              onlineCafsList.add(map);
            }
          }
          rd.close();
        }
      } catch (Exception e) {
        TestFlight.log("OnlineCafActivity.RetreiveOnlineCAFs() catch Exception "+ e.getMessage());
        Log.e(TAG,"OnlineCafActivity.RetreiveOnlineCAFs():"+ e.getMessage());
      } finally {
      }
      return authCode;
    }

    // ==========================================================================

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(OnlineCafActivity.this);
      pDialog.setMessage("Getting Data ...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
      pDialog.show();
    }
  } // End of RetreiveOnlineCAFs class

  // ==========================================================================

  @Override
  public void onBackPressed() {
    finish();
  }

  // ==========================================================================

  public void createAlert(String message, String title) {
    CustomizeDialog customizeDialog = new CustomizeDialog(context);
    customizeDialog.setTitle(title);
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();
  }

  // ==========================================================================

  public void updateOnlineCAFsStaus(View view) {
    Log.i(TAG, "START of the method updateOnlineCAFsStaus");
    int noOfCafsToUpdate = 0;
    String cafStatus = null;

    try {
      if (onlineCafsList != null && onlineCafsList.size() > 0) {
        uploadedCafJSONArray = new JSONArray();

        for (int i = 0; i < onlineCafsList.size(); i++) {
          HashMap<String, Object> onlineCafMap = onlineCafsList.get(i);
          cafStatus = String.valueOf(onlineCafMap.get(Constants.CAF_STATUS));

          if (Integer.parseInt(cafStatus) > 0) {
            JSONObject cafDetailsJson = new JSONObject();
            cafDetailsJson.put(Constants.UPLOAD_CAF_DETAILS_ID,(Long) onlineCafMap.get(Constants.UPLOAD_CAF_DETAILS_ID));
            cafDetailsJson.put(Constants.CAF_STATUS, String.valueOf(onlineCafMap.get(Constants.CAF_STATUS)));
            uploadedCafJSONArray.put(i, cafDetailsJson);
            noOfCafsToUpdate++;
          }
        }

        if (noOfCafsToUpdate > 0) {
          new SendOnlineCafs().execute(authCode);
        } else {
          Toast.makeText(getApplicationContext(),"Update the CAF's status", Toast.LENGTH_LONG).show();
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "PROBLEM in the method updateOnlineCAFsStaus");
    }
    Log.i(TAG, "END of the method updateOnlineCAFsStaus");
  }

  // ==========================================================================

  public void showNewCAFActivity(View view) {
    finish();
  }

  // ==========================================================================

  class SendOnlineCafs extends AsyncTask<String, Void, String> {
    private ProgressDialog pDialog;
    String responseMessage = null;

    @Override
    protected String doInBackground(String... params) {
      try {
        // Update the online cafs status
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);
        HttpResponse response;
        HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL+ "caf/uploaded/update/status");
        jsonObj = new JSONObject();
        jsonObj.put(Constants.AUTHCODE, authCode);
        jsonObj.put(Constants.UPLOADED_CAF_LIST, uploadedCafJSONArray);
        StringEntity se = new StringEntity(jsonObj.toString());
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
        post.setEntity(se);
        response = client.execute(post);

        if (response != null) {
          InputStream in = response.getEntity().getContent();
          BufferedReader rd = new BufferedReader(new InputStreamReader(in));
          String line;
          while ((line = rd.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            responseMessage = jsonObject.getString("responseMessage");
          }
          rd.close();
          TestFlight.passCheckpoint("NewCAFActivity.sendCAFDetails()"+ responseMessage);
        }
      } catch (Exception e) {
        Log.e(TAG,"PROBLEM in the method updateOnlineCAFsStaus Async Task");
      }
      return authCode;
    }

    // ==========================================================================
    
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      pDialog.dismiss();
      OnlineCafActivity.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (responseMessage != null) {
            if (responseMessage.equalsIgnoreCase("ok")) {
              Toast.makeText(OnlineCafActivity.this,"CAF's status updated successfully.",Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(OnlineCafActivity.this,"Unable to update the CAF's status at this time.",Toast.LENGTH_LONG).show();
            }
            finish();
          }
        }
      });
    }

    // ==========================================================================
    
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(OnlineCafActivity.this);
      pDialog.setMessage("Sending Data Please Wait ...");
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
      pDialog.show();
    }
    
    // ==========================================================================
  }

  // ==========================================================================
}
