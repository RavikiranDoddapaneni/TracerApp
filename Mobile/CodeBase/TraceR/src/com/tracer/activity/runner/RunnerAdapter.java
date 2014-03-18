package com.tracer.activity.runner;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tracer.R;
import com.tracer.util.Constants;

public class RunnerAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<HashMap<String, Object>> runnersDataList;

	public RunnerAdapter(Context context, ArrayList<HashMap<String, Object>> runnersDataList) {
		this.mContext = context;
		this.runnersDataList = runnersDataList;
	}

	@Override
	public int getCount() {
		return runnersDataList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_runner_list_item, parent, false);

		TextView runner_name = (TextView) view.findViewById(R.id.item_runner_name);
		TextView runner_status = (TextView) view.findViewById(R.id.item_runner_status);
		TextView runner_cafs = (TextView) view.findViewById(R.id.item_runner_total_cafs);

		ImageView callRunner = (ImageView) view.findViewById(R.id.callRunner);
		ImageView messageRunner = (ImageView) view.findViewById(R.id.messageRunner);

		/**
		 * Called when the user clicks on Call button.
		 */

		callRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + runnersDataList.get(position).get(Constants.CONTACT_NUMBER).toString()));
					mContext.startActivity(callIntent);
				} catch (ActivityNotFoundException activityException) {
					Log.e("dialing-example", "Call failed", activityException);
				}
			}
		});

		/**
		 * Called when the user clicks on Message button.
		 */
		messageRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.putExtra("address", runnersDataList.get(position).get(Constants.CONTACT_NUMBER).toString());
				smsIntent.setType("vnd.android-dir/mms-sms");
				mContext.startActivity(smsIntent);
			}
		});

		runner_name.setText(runnersDataList.get(position).get(Constants.RUNNERNAME).toString());
		if (runnersDataList.get(position).get(Constants.IS_PRESENT).toString().equalsIgnoreCase("true")) {
			runner_status.setText("Present");
		} else {
			runner_status.setText("Absent");
		}
		runner_cafs.setText(runnersDataList.get(position).get(Constants.CAFCOUNT).toString());

		return view;
	}
}
