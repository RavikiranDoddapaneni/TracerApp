package com.tracer.activity.beatplan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tracer.R;
import com.tracer.activity.caf.NewCAFActivity;

public class BeatPlanAdapter extends BaseAdapter {

	String[] distributorsNames;
	String[] distributorsVisits;
	String[] distributorsCodes;
	String[] distributorsSchedules;
	Context mContext;

	/**
	 * 
	 * @param beatPlanActivity
	 * @param distributorsNames
	 * @param distributorsVisits
	 * @param distributorsCodes
	 * @param distributorsSchedules
	 */
	public BeatPlanAdapter(BeatPlanActivity beatPlanActivity, String[] distributorsNames, String[] distributorsVisits,
			String[] distributorsCodes, String[] distributorsSchedules) {
		this.mContext = beatPlanActivity;
		this.distributorsNames = distributorsNames;
		this.distributorsVisits = distributorsVisits;
		this.distributorsCodes = distributorsCodes;
		this.distributorsSchedules = distributorsSchedules;

	}

	@Override
	public int getCount() {
		return distributorsNames.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_beat_plan_list_item, parent, false);
		TextView distributor_name = (TextView) view.findViewById(R.id.item_beat_plan_distributor_name);
		TextView distributor_visits = (TextView) view.findViewById(R.id.item_beat_plan_distributor_visits);
		TextView distributor_codes = (TextView) view.findViewById(R.id.item_beat_plan_distributor_codes);

		Button collect_caf = (Button) view.findViewById(R.id.collect_CAF);

		distributor_name.setText(distributorsNames[position]);
		distributor_visits.setText(distributorsVisits[position]);
		distributor_codes.setText(distributorsSchedules[position]);

		collect_caf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle runnerBundle = new Bundle();
				runnerBundle.putString("dist_name", distributorsNames[position]);
				runnerBundle.putString("dist_code", distributorsCodes[position]);
				Intent intent = new Intent(mContext, NewCAFActivity.class);
				intent.putExtras(runnerBundle);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
			}
		});

		return view;
	}
}
