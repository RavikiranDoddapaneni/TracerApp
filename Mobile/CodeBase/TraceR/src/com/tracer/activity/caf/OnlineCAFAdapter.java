/**
 * @author Jp
 *
 */
package com.tracer.activity.caf;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tracer.R;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;

public class OnlineCAFAdapter extends BaseAdapter {
  Context mContext;
  ArrayList<HashMap<String, Object>> onlineCafsList;
  JSONObject jsonObject;
  SharedPreferences preferences;
  Editor editor;

  //==========================================================================
  
  public OnlineCAFAdapter(OnlineCafActivity onlineCafActivity, ArrayList<HashMap<String, Object>> onlineCafsList) {
    this.mContext = onlineCafActivity;
    this.onlineCafsList = onlineCafsList;
  }

  //==========================================================================
  
  @Override
  public int getCount() {
    return onlineCafsList.size();
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
    Log.i("OnlineCAFAdapter", "In the getView method");
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.activity_online_caf_list_item, parent, false);
    TextView mobile_number = (TextView) view.findViewById(R.id.item_list_online_caf_mobile_no);
    TextView caf_status = (TextView) view.findViewById(R.id.item_list_online_caf_status);
    mobile_number.setText(onlineCafsList.get(position).get(Constants.MOBILE_NO).toString());
    caf_status.setText(onlineCafsList.get(position).get(Constants.CAF_STATUS_STRING).toString());
    return view;
  }

  //==========================================================================
  
  @Override
public void notifyDataSetChanged() {
  super.notifyDataSetChanged();
  Log.i("OnlineCAFAdapter", "In the method notifyDataSetChanged");
}

public void createAlert(String message) {
    CustomizeDialog customizeDialog = new CustomizeDialog(mContext);
    customizeDialog.setTitle("Online CAF's verification");
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();
  }
  
  //==========================================================================
}
