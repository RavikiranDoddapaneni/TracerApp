package com.tracer.activity.runner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tracer.R;
import com.tracer.util.Prefs;

public class RunnersActivity extends ActionBarActivity {

	SharedPreferences prefs;
	String userName;
	RunnerAdapter runnerAdapter;
	ListView runnersList;
	Context context = this;
	String[] runners;
	String[] status;
	String[] runnersContacts;
	String[] runnersCafs;

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
		runnerAdapter = new RunnerAdapter(context, runners, status, runnersContacts, runnersCafs);
		runnersList.setAdapter(runnerAdapter);

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
}
