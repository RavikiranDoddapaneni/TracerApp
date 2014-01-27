package com.tracer.activity.runner;

import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;
import com.tracer.util.Constants;
import com.tracer.util.Prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class RunnerHomeActivity extends ActionBarActivity {

	SharedPreferences prefs;
	String userType;
	String userName;
	String authCode;

	TextView welcomeText;
	TextView userTypeDisplay;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runner_home);

		welcomeText = (TextView) findViewById(R.id.welcomeText);
		userTypeDisplay = (TextView) findViewById(R.id.userTypeDisplay);

		prefs = Prefs.get(this);
		userType = prefs.getString(Constants.USERTYPE, null);
		userName = prefs.getString(Constants.USERNAME, null);
		authCode = prefs.getString(Constants.AUTHCODE, null);

		if (userType.equals("TSE")) {
			welcomeText.setText("Welcome " + userName);
			userTypeDisplay.setText("You are logged in as " + userType);
		} else if (userType.equals("TSM")) {
			welcomeText.setText("Welcome " + userName);
			userTypeDisplay.setText("You are logged in as " + userType);
		}
	}

	/**
	 * Called when the user clicks on Get Beatplan button.
	 * 
	 * @param view
	 */
	public void getBeatPlan(View view) {
		startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
		overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
	}
}
