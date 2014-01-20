package com.tracer.activity.beatplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.tracer.R;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.activity.teamleader.TeamLeaderHomeActivity;
import com.tracer.util.Prefs;

public class BeatPlanActivity extends ActionBarActivity {

	String[] distributorsNames;
	String[] distributorsVisits;
	String[] distributorsCodes;
	BeatPlanAdapter beatPlanAdapter;
	ListView beatPlanList;
	SharedPreferences prefs;
	String userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beat_plan);

		beatPlanList = (ListView) findViewById(R.id.beatPlanList);
		prefs = Prefs.get(this);
		userName = prefs.getString("user", null);

		distributorsNames = getResources().getStringArray(R.array.Manoj_names_array);
		distributorsVisits = getResources().getStringArray(R.array.Manoj_visits_array);
		distributorsCodes = getResources().getStringArray(R.array.Manoj_codes_array);

		beatPlanAdapter = new BeatPlanAdapter(this, distributorsNames, distributorsVisits, distributorsCodes);
		beatPlanList.setAdapter(beatPlanAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			if (userName.equalsIgnoreCase("manager")) {
				startActivity(new Intent(getApplicationContext(), TeamLeaderHomeActivity.class));
			} else if (userName.equalsIgnoreCase("teamleader")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			} else if (userName.equalsIgnoreCase("runner")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		} else if (item.getItemId() == R.id.home_button) {
			if (userName.equalsIgnoreCase("manager")) {
				startActivity(new Intent(getApplicationContext(), TeamLeaderHomeActivity.class));
			} else if (userName.equalsIgnoreCase("teamleader")) {
				startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
			} else if (userName.equalsIgnoreCase("runner")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		}
		return true;
	}
}
