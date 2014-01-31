package com.tracer.activity.beatplan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.activity.teamleader.TeamLeaderHomeActivity;
import com.tracer.util.Constants;
import com.tracer.util.Prefs;

public class BeatPlanActivity extends ActionBarActivity {

	ArrayList<HashMap<String, Object>> distributorsList;
	BeatPlanAdapter beatPlanAdapter;
	ListView beatPlanList;
	SharedPreferences prefs;

	String authCode;
	String userType;

	JSONObject jsonObject;
	Editor editor;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beat_plan);

		beatPlanList = (ListView) findViewById(R.id.beatPlanList);
		prefs = Prefs.get(this);
		authCode = prefs.getString(Constants.AUTHCODE, null);
		userType = prefs.getString(Constants.USERTYPE, null);

		new RetreiveBeatPlanResponse().execute(authCode);
	}

	/**
	 * Method for creating Menus in the current View
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Action to be performed when the clicked on Menu icons.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(), TeamLeaderHomeActivity.class));
			} else if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			} else if (userType.equalsIgnoreCase("TSE")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		} else if (item.getItemId() == R.id.home_button) {
			if (userType.equalsIgnoreCase("TSM")) {
				startActivity(new Intent(getApplicationContext(), TeamLeaderHomeActivity.class));
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

		return true;
	}

	class RetreiveBeatPlanResponse extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			beatPlanAdapter = new BeatPlanAdapter(BeatPlanActivity.this, distributorsList);
			beatPlanList.setAdapter(beatPlanAdapter);
		}

		protected String doInBackground(String... urls) {
			try {

				System.out.println(urls[0]);
				jsonObject = new JSONObject();
				jsonObject.put(Constants.AUTHCODE, urls[0]);
				String baseURL = Constants.WEBSERVICE_BASE_URL + "GetBeatPlan/";
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
					System.out.println(jsonObject.toString());

					JSONArray distributorObject = jsonObject.getJSONArray(Constants.DISTRIBUTORS);
					System.out.println(distributorObject);

					distributorsList = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < distributorObject.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject distObject = distributorObject.getJSONObject(i);
						map.put(Constants.DISTRIBUTORNAME, distObject.getString(Constants.DISTRIBUTORNAME));
						map.put(Constants.DISTRIBUTORCODE, distObject.getString(Constants.DISTRIBUTORCODE));
						map.put(Constants.SCHEDULETIME, distObject.getString(Constants.SCHEDULETIME));
						map.put(Constants.VISITFREQUENCY, distObject.getString(Constants.VISITFREQUENCY));
						distributorsList.add(map);
					}

					System.out.println("Distributor List :" + distributorsList.toString());

				}
				wr.close();
				rd.close();
				return line;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BeatPlanActivity.this);
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

	}
}
