package com.tracer.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
  AlertDialog alert;
  Context context;
  NetworkInfo activeNetInfo;
  Dialog dialog;
  DataBaseHelper dataBaseHelper;

  //==========================================================================
  
  public ConnectionChangeReceiver(Context context) {
    super();
    this.context = context;
    dataBaseHelper = DataBaseHelper.getDBAdapterInstance(context);
  }

  //==========================================================================
  
  @Override
  public void onReceive(final Context context, final Intent intent) {
    boolean status = Utils.getConnectivityStatusString(context);
    if (status) {
      System.out.println("Ena");
    } else {
      System.out.println("Dis");
    }
  }
  
  //==========================================================================
}
