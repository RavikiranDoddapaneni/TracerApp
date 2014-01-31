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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
import com.tracer.util.Constants;
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

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				String runnerStatus = (String) runnersDataList.get(position).get(Constants.IS_PRESENT);
				if (runnerStatus.equalsIgnoreCase("false")) {
					startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
					overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
				} else {
					Toast.makeText(getApplicationContext(), "Active", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

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
			runnerAdapter = new RunnerAdapter(context, runnersDataList);
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
				jsonObject.put(Constants.AUTHCODE, urls[0]);
				String baseURL = Constants.WEBSERVICE_BASE_URL + "GetRunners/";
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

					JSONArray runnerObject = jsonObject.getJSONArray(Constants.RUNNERS);
					System.out.println(runnerObject);

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
