package com.tracer.activity.beatplan;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.activity.teamleader.TeamLeaderHomeActivity;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
import com.tracer.util.Prefs;

public class BeatPlanActivity extends ActionBarActivity {
	ArrayList<HashMap<String, Object>> beatPlansList;
	BeatPlanAdapter beatPlanAdapter;
	ListView beatPlansListView;
	SharedPreferences prefs;

	String authCode;
	String userType;

	Context context;
	JSONObject jsonObject;
	Editor editor;
    LoginActivity loginActivity;
	private static final String TAG = "BeatPlanActivity";

	// ==========================================================================

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beat_plan);
		context = this;
		 loginActivity = new LoginActivity();
		beatPlansListView = (ListView) findViewById(R.id.beatPlanList);
		prefs = Prefs.get(this);
		authCode = prefs.getString(Constants.AUTHCODE, null);
		userType = prefs.getString(Constants.USERTYPE, null);
		
		new RetreiveBeatPlanResponse(context,authCode).execute();
		
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
		if (item.getItemId() == android.R.id.home) {
			finish();
			if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(),TeamLeaderHomeActivity.class));
			} else if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(),RunnerHomeActivity.class));
			} else if (userType.equalsIgnoreCase("TSE")) {
				startActivity(new Intent(getApplicationContext(),RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim,R.anim.to_right_anim);
		} else if (item.getItemId() == R.id.home_button) {
			if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(),TeamLeaderHomeActivity.class));
			} else if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(),RunnersActivity.class));
			} else if (userType.equalsIgnoreCase("TSE")) {
				startActivity(new Intent(getApplicationContext(),RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim,
					R.anim.to_right_anim);
		} else if (item.getItemId() == R.id.logout) {
			
               synchronized (this) {
            	   loginActivity.stopAlarmManagerService(getApplicationContext());
			}
				
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				overridePendingTransition(R.anim.from_left_anim,R.anim.to_right_anim);
			
		}
		return true;
	}

	
	
	
	// ==========================================================================

	class RetreiveBeatPlanResponse extends AsyncTask<String, Void, String> {
		
		
		ProgressDialog pDialog;
		Context beatplanContet;
		String auth;

		public RetreiveBeatPlanResponse(Context context,String authcod){
			this.beatplanContet = context;
			this.auth = authcod;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*pDialog = new ProgressDialog(beatplanContet);
			pDialog.setMessage("Getting Data ...");
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();*/
			
		}
		
		

		// ==========================================================================

		protected String doInBackground(String... urls) {

			try {
//				TestFlight.log("BeatPlanActivity.RetreiveBeatPlanResponse()");
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),100000);
				HttpResponse response;

				Log.i(TAG, "Request URL :" + Constants.WEBSERVICE_BASE_URL+ "beatplans/get/" + urls[0]);
				
				HttpGet get = new HttpGet(Constants.WEBSERVICE_BASE_URL+ "beatplans/get/" + urls[0]);
				response = client.execute(get);

				if (response != null) {
					InputStream in = response.getEntity().getContent();
					BufferedReader rd = new BufferedReader(new InputStreamReader(in));
					String line;
					while ((line = rd.readLine()) != null) {
						JSONObject jsonObject = new JSONObject(line);
						JSONArray distributorObject = jsonObject
								.getJSONArray(Constants.DISTRIBUTORS);
						beatPlansList = new ArrayList<HashMap<String, Object>>();

						for (int i = 0; i < distributorObject.length(); i++) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							JSONObject distObject = distributorObject
									.getJSONObject(i);
							map.put(Constants.DISTRIBUTORNAME, distObject
									.getString(Constants.DISTRIBUTORNAME));
							map.put(Constants.DISTRIBUTOR_ID, distObject
									.getString(Constants.DISTRIBUTOR_ID));
							map.put(Constants.DISTRIBUTORCODE, distObject
									.getString(Constants.DISTRIBUTORCODE));
							map.put(Constants.SCHEDULETIME, distObject
									.getString(Constants.SCHEDULETIME));
							map.put(Constants.VISITFREQUENCY,
									distObject.getInt(Constants.VISITFREQUENCY));
							map.put(Constants.ISCAFSUBMITTED, distObject
									.getBoolean(Constants.ISCAFSUBMITTED));
							map.put(Constants.VISITNUMBER,
									distObject.getInt(Constants.VISITNUMBER));
							map.put(Constants.DISTRIBUTORCONTACTNUMBER,
									distObject
											.getInt(Constants.DISTRIBUTORCONTACTNUMBER));
							map.put(Constants.DISTRIBUTORLATITIUDE, distObject
									.getDouble(Constants.DISTRIBUTORLATITIUDE));
							map.put(Constants.DISTRIBUTORLONGITUDE, distObject
									.getDouble(Constants.DISTRIBUTORLONGITUDE));
							map.put(Constants.DISPLAYSAVEDLLBUTTON, distObject
									.getBoolean(Constants.DISPLAYSAVEDLLBUTTON));
							beatPlansList.add(map);
						}
						
						jsonObject = null;
						distributorObject = null;
					}
					line = null;
					in.close();
					rd.close();
//					TestFlight.passCheckpoint("BeatPlanActivity.RetreiveBeatPlanResponse()");
				}
			} catch (Exception e) {
//				TestFlight.log("BeatPlanActivity.RetreiveBeatPlanResponse() catch Exception "+ e.getMessage());
				Log.e(TAG,"BeatPlanActivity.RetreiveBeatPlanResponse():"+ e.getMessage());
			} finally {
			}
			return authCode;
		}

		
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			/*if(pDialog.isShowing())
			{
			pDialog.dismiss();
			}*/
			
			if (beatPlansList != null && beatPlansList.size() > 0) {
				beatPlanAdapter = new BeatPlanAdapter(BeatPlanActivity.this, beatPlansList);
				beatPlansListView.setAdapter(beatPlanAdapter);
			} else {
				createAlert("No Data Available", "Beat Plans");
			}
		}
		
	}

	// ==========================================================================

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(),RunnerHomeActivity.class);
		startActivity(intent);
		
//		finish();
	}

	// ==========================================================================

	public void createAlert(String message, String title) {
		
		/*try {
			CustomizeDialog customizeDialog = new CustomizeDialog(context);
			customizeDialog.setTitle(title);
			customizeDialog.setMessage(message);
			customizeDialog.show();

			if (!customizeDialog.isShowing())
			{
				customizeDialog.show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog, null))
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
		
		
		
		// custom dialog
		
					/*Dialog dialog = new Dialog(context);
					requestWindowFeature(Window.FEATURE_NO_TITLE);
					getWindow().getDecorView();
					dialog.setContentView(R.layout.dialog);
					
					
					// set the custom dialog components - text, image and button
					TextView text = (TextView) dialog.findViewById(R.id.dialogTitle);
					text.setText(title);
					TextView messages = (TextView) dialog.findViewById(R.id.dialogMessage);
					messages.setText(message);
		 
					Button close = (Button) dialog.findViewById(R.id.OkButton);
					// if button is clicked, close the custom dialog
					close.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					
										
					dialog.show();*/
				  }
	}

	// ==========================================================================

