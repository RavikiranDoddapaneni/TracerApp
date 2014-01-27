package com.tracer.activity.runner;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.util.Prefs;

public class RunnersActivity extends ActionBarActivity {

	ArrayList<HashMap<String, Object>> runnersDataList;
	SharedPreferences prefs;
	String userName;
	RunnerAdapter runnerAdapter;
	ListView runnersList;
	Context context = this;
	String[] runners;
	String[] status;
	String[] runnersContacts;
	String[] runnersCafs;

	JSONObject jsonObject;
	Editor editor;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runners_list);

		prefs = Prefs.get(this);
		userName = prefs.getString("user", null);

		runners = getResources().getStringArray(R.array.runners_names_array);
		status = getResources().getStringArray(R.array.runners_status_array);
		runnersContacts = getResources().getStringArray(R.array.runners_contacts_array);
		runnersCafs = getResources().getStringArray(R.array.runners_cafs_array);

		runnersList = (ListView) findViewById(R.id.runners_list);

		/**
		 * Called when the user selects any of the item from the list view.
		 */
		runnersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				String runnerStatus = status[position];
				if (runnerStatus.equalsIgnoreCase("absent")) {
					startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
					overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
				} else {
					Toast.makeText(getApplicationContext(), "Active", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	class RetreiveBeatPlanResponse extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			runnerAdapter = new RunnerAdapter(context, runners, status, runnersContacts, runnersCafs);
			runnersList.setAdapter(runnerAdapter);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		protected String doInBackground(String... urls) {
			try {

				System.out.println(urls[0]);
				jsonObject = new JSONObject();
				jsonObject.put("authCode", urls[0]);
				String baseURL = "http://192.168.80.100:8080/TraceR_WS/GetRunners/";
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

					JSONArray runnerObject = jsonObject.getJSONArray("runners");
					System.out.println(runnerObject);

					runnersDataList = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < runnerObject.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject distObject = runnerObject.getJSONObject(i);
						map.put("contactNumber", distObject.getString("contactNumber"));
						map.put("CAFCount", distObject.getString("CAFCount"));
						map.put("runnerName", distObject.getString("runnerName"));
						map.put("isPresent", distObject.getString("isPresent"));
						map.put("runnerCode", distObject.getString("runnerCode"));
						runnersDataList.add(map);
					}

					System.out.println("Runners Data List :" + runnersDataList.toString());

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
			pDialog = new ProgressDialog(RunnersActivity.this);
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

	}
}
