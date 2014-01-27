package com.tracer.activity.beatplan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tracer.util.Constants;
import com.tracer.util.Prefs;

public class BeatPlanAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<HashMap<String, Object>> distributorsList;
	JSONObject jsonObject;
	SharedPreferences preferences;
	String visitCount;
	String visitId;

	public BeatPlanAdapter(BeatPlanActivity beatPlanActivity, ArrayList<HashMap<String, Object>> distributorsList) {
		this.mContext = beatPlanActivity;
		this.distributorsList = distributorsList;
	}

	@Override
	public int getCount() {
		return distributorsList.size();
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

		distributor_name.setText(distributorsList.get(position).get("distributorName").toString());
		distributor_visits.setText(distributorsList.get(position).get("visitFrequency").toString());
		distributor_codes.setText(distributorsList.get(position).get("scheduleTime").toString());

		collect_caf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {
					public void run() {

						URLConnection conn;
						try {
							preferences = Prefs.get(mContext);
							preferences.getString(Constants.AUTHCODE, "");
							jsonObject = new JSONObject();
							jsonObject.put("authCode", preferences.getString(Constants.AUTHCODE, ""));
							jsonObject.put("distributorCode", distributorsList.get(position).get("distributorCode").toString());
							String baseURL = "http://192.168.80.100:8080/TraceR_WS/GetVisitInfo/";
							URL url = new URL(baseURL + jsonObject);
							conn = url.openConnection();
							conn.setDoOutput(true);
							OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
							wr.write(baseURL);
							wr.flush();
							// Get the response
							BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String line;

							while ((line = rd.readLine()) != null) {
								System.out.println("line ::::: " + line);
								JSONObject jsonObject = new JSONObject(line);
								System.out.println(jsonObject.toString());

								visitCount = jsonObject.getString("visitCount");
								visitId = jsonObject.getString("visitCode");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

				Bundle runnerBundle = new Bundle();
				runnerBundle.putString("dist_name", distributorsList.get(position).get("distributorName").toString());
				runnerBundle.putString("dist_code", distributorsList.get(position).get("distributorCode").toString());
				runnerBundle.putString("visit_count", visitCount);
				runnerBundle.putString("visit_id", visitId);
				Intent intent = new Intent(mContext, NewCAFActivity.class);
				intent.putExtras(runnerBundle);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
			}
		});

		return view;
	}
}
