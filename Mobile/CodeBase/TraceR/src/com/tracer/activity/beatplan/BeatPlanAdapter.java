package com.tracer.activity.beatplan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
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
	Editor editor;
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

		distributor_name.setText(distributorsList.get(position).get(Constants.DISTRIBUTORNAME).toString());
		distributor_visits.setText(distributorsList.get(position).get(Constants.VISITFREQUENCY).toString());
		distributor_codes.setText(distributorsList.get(position).get(Constants.SCHEDULETIME).toString());

		if (position % 2 == 0) {
			collect_caf.setEnabled(false);
			collect_caf.setBackgroundResource(R.drawable.collect_btn_over);
		}

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
							jsonObject.put(Constants.AUTHCODE, preferences.getString(Constants.AUTHCODE, ""));
							jsonObject.put(Constants.DISTRIBUTORCODE, distributorsList.get(position).get(Constants.DISTRIBUTORCODE).toString());

							HttpClient client = new DefaultHttpClient();
							HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
							HttpResponse response;
							HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "user/runner/visitinfo/get");

							StringEntity se = new StringEntity(jsonObject.toString());
							se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
							post.setEntity(se);
							response = client.execute(post);

							/* Checking response */
							if (response != null) {
								InputStream in = response.getEntity().getContent();
								BufferedReader rd = new BufferedReader(new InputStreamReader(in));
								String line;
								while ((line = rd.readLine()) != null) {
									System.out.println("line ::::: " + line);
									JSONObject jsonObject = new JSONObject(line);
									System.out.println(jsonObject.toString());

									visitCount = jsonObject.getString(Constants.VISITCOUNT);
									visitId = jsonObject.getString(Constants.VISITCODE);
								}
							}
							Bundle runnerBundle = new Bundle();
							runnerBundle.putString(Constants.DISTRIBUTORNAME, distributorsList.get(position).get(Constants.DISTRIBUTORNAME).toString());
							runnerBundle.putString(Constants.DISTRIBUTORCODE, distributorsList.get(position).get(Constants.DISTRIBUTORCODE).toString());
							runnerBundle.putString(Constants.VISITCOUNT, visitCount);
							runnerBundle.putString(Constants.VISITCODE, visitId);
							Intent intent = new Intent(mContext, NewCAFActivity.class);
							intent.putExtras(runnerBundle);
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

			}
		});

		return view;
	}
}
