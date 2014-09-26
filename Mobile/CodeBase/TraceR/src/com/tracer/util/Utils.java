/**
 * @author Prashanth M
 *
 */
package com.tracer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;


public class Utils {
  public static int TYPE_WIFI = 1;
  public static int TYPE_MOBILE = 2;
  public static int TYPE_NOT_CONNECTED = 0;
  
  static SharedPreferences sp;

  //==========================================================================
  
  public static int getConnectivityStatus(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    
    if (null != activeNetwork) {
      if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
        return TYPE_WIFI;

      if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
        return TYPE_MOBILE;
    }
    return TYPE_NOT_CONNECTED;
  }

  //==========================================================================
  
  public static boolean getConnectivityStatusString(Context context) {
    int conn = Utils.getConnectivityStatus(context);
    boolean status = false;
    if (conn == Utils.TYPE_WIFI) {
      status = true;
    } else if (conn == Utils.TYPE_MOBILE) {
      status = true;
    } else if (conn == Utils.TYPE_NOT_CONNECTED) {
      status = false;
    }
    return status;
  }
  
  //==========================================================================
  
  //=============================================================================
  
  public static void setCheckAttendance(Context context,String version,String yn)
  {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = sp.edit();
      editor.putString(version,yn);
      editor.commit();
  }

  public static String getCheckAttendance(Context context,String version)
  {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      String _yn = preferences.getString(version,null);

     return _yn;

  }
  
  //=========================================================================================
}


