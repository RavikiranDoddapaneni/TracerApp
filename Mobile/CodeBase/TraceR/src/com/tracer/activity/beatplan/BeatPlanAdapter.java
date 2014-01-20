package com.tracer.activity.beatplan;

import com.tracer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BeatPlanAdapter extends BaseAdapter {

	String[] distributorsNames;
	String[] distributorsVisits;
	String[] distributorsCodes;
	Context mContext;

	public BeatPlanAdapter(BeatPlanActivity beatPlanActivity, String[] distributorsNames, String[] distributorsVisits,
			String[] distributorsCodes) {
		this.mContext = beatPlanActivity;
		this.distributorsNames = distributorsNames;
		this.distributorsVisits = distributorsVisits;
		this.distributorsCodes = distributorsCodes;

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_beat_plan_list_item, parent, false);
		TextView distributor_name = (TextView) view.findViewById(R.id.item_beat_plan_distributor_name);
		TextView distributor_visits = (TextView) view.findViewById(R.id.item_beat_plan_distributor_visits);
		TextView distributor_codes = (TextView) view.findViewById(R.id.item_beat_plan_distributor_codes);

		distributor_name.setText(distributorsNames[position]);
		distributor_visits.setText(distributorsVisits[position]);
		distributor_codes.setText(distributorsCodes[position]);

		return view;
	}
}
