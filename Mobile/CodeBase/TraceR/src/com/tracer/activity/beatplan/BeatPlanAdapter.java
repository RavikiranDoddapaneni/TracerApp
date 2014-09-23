package com.tracer.activity.beatplan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.caf.NewCAFActivity;
import com.tracer.service.gpsservice.GpsTracker;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
import com.tracer.util.Prefs;

public class BeatPlanAdapter extends BaseAdapter {
  Context mContext;
  ArrayList<HashMap<String, Object>> distributorsList;
  JSONObject jsonObject;
  SharedPreferences preferences;
  Editor editor;
  String visitCount;
  String visitId;
  String visitFrequency;
  double currentLatitude;
  double currentLongitude;
  
  

  //==========================================================================
  
  public BeatPlanAdapter(BeatPlanActivity beatPlanActivity, ArrayList<HashMap<String, Object>> distributorsList) {
    this.mContext = beatPlanActivity;
    this.distributorsList = distributorsList;
  }

  //==========================================================================
  
  @Override
  public int getCount() {
    return distributorsList.size();
  }

  //==========================================================================
  
  @Override
  public Object getItem(int position) {
    return null;
  }

  //==========================================================================
  
  @Override
  public long getItemId(int position) {
    return position;
  }

  //==========================================================================
  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.activity_beat_plan_list_item, parent, false);
    TextView distributor_name = (TextView) view.findViewById(R.id.item_beat_plan_distributor_name);
    TextView distributor_visits = (TextView) view.findViewById(R.id.item_beat_plan_distributor_visits);
    TextView distributor_codes = (TextView) view.findViewById(R.id.item_beat_plan_distributor_codes);

    Button collect_caf = (Button) view.findViewById(R.id.collect_CAF);
    Button update_latlong = (Button) view.findViewById(R.id.getLatLong);
    
    distributor_name.setText(distributorsList.get(position).get(Constants.DISTRIBUTORNAME).toString());
    distributor_visits.setText(distributorsList.get(position).get(Constants.VISITNUMBER).toString());
    distributor_codes.setText(distributorsList.get(position).get(Constants.SCHEDULETIME).toString());

    if ((Boolean) distributorsList.get(position).get(Constants.ISCAFSUBMITTED)) {
      collect_caf.setEnabled(false);
      collect_caf.setBackgroundResource(R.drawable.collect_btn_disabled);
    }
    if(!(Boolean) distributorsList.get(position).get(Constants.DISPLAYSAVEDLLBUTTON))
    {
    	update_latlong.setVisibility(View.GONE);
    }
    
    update_latlong.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			
		 try {
	          GpsTracker gpsTracker = new GpsTracker(mContext);
	          
	          if (gpsTracker.canGetLocation()) {
	            currentLatitude = gpsTracker.latitude;
	            currentLongitude = gpsTracker.longitude;
	          } else {
	            gpsTracker.showSettingsAlert();
	          }
	          preferences = Prefs.get(mContext);
              preferences.getString(Constants.AUTHCODE, "");
              jsonObject = new JSONObject();
              jsonObject.put(Constants.AUTHCODE, preferences.getString(Constants.AUTHCODE, ""));
              jsonObject.put(Constants.DISTRIBUTOR_ID, distributorsList.get(position).get(Constants.DISTRIBUTOR_ID).toString());
	          jsonObject.put(Constants.CURRENTLATITIUDE, currentLatitude);
	          jsonObject.put(Constants.CURRENTLONGITUDE, currentLongitude);
	          
	          new Thread(new Runnable() {
	              public void run() {
	            	  
	            	  try {
	            		  HttpClient client = new DefaultHttpClient();
						  HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
						  HttpResponse response;
						  HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "beatplans/distributor/latlong/update");
						  Log.i("BeatPlanAdapter", Constants.WEBSERVICE_BASE_URL + "beatplans/distributor/latlong/update");

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
						    }
						  }
						  	Intent intent = ((Activity) mContext).getIntent();
						  	((Activity) mContext).finish();
						  	((Activity) mContext).startActivity(intent);
						  
					} catch (Exception e) {
						e.printStackTrace();
					}
	            	  
	              }
	          }).start();
	          
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
		}
	});
    
    collect_caf.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        try {
        	
        	
          GpsTracker gpsTracker = new GpsTracker(mContext);
          
          if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.latitude;
            currentLongitude = gpsTracker.longitude;
          } else {
            gpsTracker.showSettingsAlert();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        float[] result = new float[5];
        int d;
        
        if (currentLatitude == 0.0 || currentLongitude == 0.0) {
          d = 10;
        } else {
          Location.distanceBetween(Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLATITIUDE).toString()),
              Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLONGITUDE).toString()), currentLatitude,
              currentLongitude, result);
          d = (int) result[0];
        }
        String data = "DistLat : " + Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLATITIUDE).toString())
            + "   Dist Long : " + Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLONGITUDE).toString())
            + "   Current Lat : " + currentLatitude + "  Current Long : " + currentLongitude;
        
//        createAlert(data);
        
        /*Toast.makeText(
            mContext,
            "DistLat :" + Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLATITIUDE).toString()) + "Dist Long"
                + Double.parseDouble(distributorsList.get(position).get(Constants.DISTRIBUTORLONGITUDE).toString()) + "Current Lat"
                + currentLatitude + "Current Long" + currentLongitude, Toast.LENGTH_SHORT).show();
       */
        Toast.makeText(mContext, "Distance :" + d, Toast.LENGTH_SHORT).show();
        TestFlight.log("Distance :" + d);

        if (d < 500) {
          new Thread(new Runnable() {
            public void run() {

              try {
                preferences = Prefs.get(mContext);
                preferences.getString(Constants.AUTHCODE, "");
                jsonObject = new JSONObject();
                jsonObject.put(Constants.AUTHCODE, preferences.getString(Constants.AUTHCODE, ""));
                jsonObject.put(Constants.DISTRIBUTORCODE, distributorsList.get(position).get(Constants.DISTRIBUTORCODE).toString());
                jsonObject.put(Constants.VISITNUMBER, Integer.parseInt(String.valueOf(distributorsList.get(position).get(Constants.VISITNUMBER))));
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
                    JSONObject jsonObject = new JSONObject(line);
                    visitId = jsonObject.getString(Constants.RUNNER_VISIT_ID);
                    visitFrequency = jsonObject.getString(Constants.VISITFREQUENCY);
                  }
                }
                Bundle runnerBundle = new Bundle();
                runnerBundle.putString(Constants.DISTRIBUTORNAME, distributorsList.get(position).get(Constants.DISTRIBUTORNAME).toString());
                runnerBundle.putString(Constants.DISTRIBUTOR_ID, distributorsList.get(position).get(Constants.DISTRIBUTOR_ID).toString());
                runnerBundle.putString(Constants.DISTRIBUTORCODE, distributorsList.get(position).get(Constants.DISTRIBUTORCODE).toString());
                runnerBundle.putString(Constants.VISITCOUNT, distributorsList.get(position).get(Constants.VISITNUMBER).toString());
                runnerBundle.putString(Constants.DISTRIBUTORCONTACTNUMBER, distributorsList.get(position).get(Constants.DISTRIBUTORCONTACTNUMBER).toString());
                runnerBundle.putString(Constants.DISTRIBUTORLATITIUDE, distributorsList.get(position).get(Constants.DISTRIBUTORLATITIUDE).toString());
                runnerBundle.putString(Constants.DISTRIBUTORLONGITUDE, distributorsList.get(position).get(Constants.DISTRIBUTORLONGITUDE).toString());
                runnerBundle.putString(Constants.RUNNER_VISIT_ID, visitId);
                runnerBundle.putString(Constants.VISITFREQUENCY, visitFrequency);
                Intent intent = new Intent(mContext, NewCAFActivity.class);
                intent.putExtras(runnerBundle);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
              } catch (Exception e) {
                e.printStackTrace();
              } finally {
              }
            }
          }).start();
        } else {
        	
          Toast.makeText(mContext, "Please collect at Distributor location", Toast.LENGTH_SHORT).show();
        }
      }
    });
    return view;
  }

  //==========================================================================
  
  public void createAlert(String message) {
    CustomizeDialog customizeDialog = new CustomizeDialog(mContext);
    customizeDialog.setTitle("New CAF Collection");
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();
  }
  
  //==========================================================================
}
