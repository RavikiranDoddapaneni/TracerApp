package com.tracer.activity.runner;

import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class RunnerHomeActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runner_home);
	}

	public void getBeatPlan(View view) {
		startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
	}
}
