/**
 * @author Prashanth M
 *
 */
package com.tracer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

  //==========================================================================

  public static SharedPreferences get(Context context) {
    return context.getSharedPreferences("TRACER_PREFS", 0);
  }
  
  //==========================================================================
  
  
}
