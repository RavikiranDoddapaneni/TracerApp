package com.tracer.activity.runner;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tracer.R;

public class RunnerAdapter extends BaseAdapter {

	Context mContext;
	String[] runnersNames;
	String[] runnerStatus;
	String[] runnerContacts;
	String[] runnerCafs;

	public RunnerAdapter(Context context, String[] runners, String[] status, String[] runnersContacts, String[] runnersCafs) {
		this.mContext = context;
		this.runnersNames = runners;
		this.runnerStatus = status;
		this.runnerContacts = runnersContacts;
		this.runnerCafs = runnersCafs;
	}

	@Override
	public int getCount() {
		return runnersNames.length;
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

		callRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "Call Runner" + runnerContacts[position], Toast.LENGTH_LONG).show();
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + runnerContacts[position]));
					mContext.startActivity(callIntent);
				} catch (ActivityNotFoundException activityException) {
					Log.e("dialing-example", "Call failed", activityException);
				}
			}
		});

		messageRunner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "Message Runner" + runnerContacts[position], Toast.LENGTH_LONG).show();
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.putExtra("address", runnerContacts[position]);
				smsIntent.setType("vnd.android-dir/mms-sms");
				mContext.startActivity(smsIntent);
			}
		});

		runner_name.setText(runnersNames[position]);
		runner_status.setText(runnerStatus[position]);
		runner_cafs.setText(runnerCafs[position]);

		return view;
	}
}
