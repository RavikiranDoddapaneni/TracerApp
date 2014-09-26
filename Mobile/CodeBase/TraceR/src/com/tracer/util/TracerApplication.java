/**
 * @author Prashanth M
 *
 */
package com.tracer.util;

import android.app.Application;

import com.testflightapp.lib.TestFlight;

public class TracerApplication extends Application {
  private static TracerApplication _instance;

  //==========================================================================
  
  public TracerApplication() {
    _instance = this;
  }

  //==========================================================================
  
  public static TracerApplication getApplication() {
    return _instance;
  }

  //==========================================================================
  
  @Override
  public void onCreate() {
    super.onCreate();
    // Initialize TestFlight with your app token.
//    TestFlight.takeOff(this, "46b8d339-836e-45ec-8131-85445db73bd1");
  }
  
  //==========================================================================
}