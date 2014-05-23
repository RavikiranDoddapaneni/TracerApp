/**
 * @author Prashanth M
 *
 */
package com.tracer.activity.caf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.activity.login.LoginActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Constants;
import com.tracer.util.CustomizeDialog;
import com.tracer.util.Prefs;

public class DigitalSignatureActivity extends ActionBarActivity implements OnGesturePerformedListener {
  GestureOverlayView gestureOverlayView;
  Context context = this;
  ActionBar actionBar;
  SharedPreferences prefs;
  String userType;
  Bundle bundle;
  private GestureLibrary gestureLib;
  boolean isSignatureEnabled = false;

  //==========================================================================
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_digital_signature);
    bundle = new Bundle();
    bundle = getIntent().getExtras();
    prefs = Prefs.get(this);
    userType = prefs.getString(Constants.USERTYPE, null);
    actionBar = getSupportActionBar();
    gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
    gestureOverlayView.addOnGesturePerformedListener(this);
    gestureOverlayView.setFadeEnabled(false);
    gestureOverlayView.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        isSignatureEnabled = true;
        return false;
      }
    });
  }

  //==========================================================================
  
  /**
   * Called when the user clicked on Save button. Saves the digital signature in
   * the application cache after saving.
   * 
   * @param view
   */
  public void digitalSignatureDone(View view) {
    if (isSignatureEnabled) {
      gestureOverlayView.setDrawingCacheEnabled(true);
      Bitmap bitmap = gestureOverlayView.getDrawingCache();
      File imagePath = saveBitmap(bitmap);
      Intent intent = new Intent();
      intent.putExtra("digital_signature_path", imagePath.toString());
      setResult(RESULT_OK, intent);
      finish();
    } else {
      createAlert("Please provide Digital Signature");
    }
  }

  //==========================================================================
  
  /**
   * Called when the user clicked on Reset button in order to clear the digital
   * signature screen for giving new digital signature input.
   * 
   * @param view
   */
  public void onResetButton(View view) {
    gestureOverlayView.clear(false);
    isSignatureEnabled = false;
  }

  //==========================================================================
  
  /**
   * Method for creating Menus in the current View
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  //==========================================================================
  
  /**
   * Action to be performed when the clicked on Menu icons.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      startActivity(new Intent(getApplicationContext(), NewCAFActivity.class).putExtras(bundle));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    } else if (item.getItemId() == R.id.home_button) {
      if (userType.equalsIgnoreCase("TSM")) {
        startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
      } else if (userType.equalsIgnoreCase("TSM")) {
        startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
      } else if (userType.equalsIgnoreCase("TSE")) {
        startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
      }
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    } else if (item.getItemId() == R.id.logout) {
      LoginActivity.stopAlarmManagerService(getApplicationContext());
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
    }
    return true;
  }

  //==========================================================================
  
  /**
   * Method is used to the save the digital signature to the application cache.
   * @param bitmap
   * @return Image path where digital signature is saved.
   */
  public File saveBitmap(Bitmap bitmap) {
    File cachePath = new File(context.getCacheDir() + "/" + "TraceR");
    
    if (!cachePath.exists()) {
      cachePath.mkdir();
    }
    Calendar cal = Calendar.getInstance();
    File imagePath = new File(cachePath, (cal.getTimeInMillis() + ".jpg"));
    //System.out.println(imagePath.toString());
    FileOutputStream fos;
    
    try {
      fos = new FileOutputStream(imagePath);
      bitmap.compress(CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      Log.e("GREC", e.getMessage(), e);
    } catch (IOException e) {
      Log.e("GREC", e.getMessage(), e);
    }
    return imagePath;
  }

  //==========================================================================
  
  @Override
  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
    for (Prediction prediction : predictions) {
      if (prediction.score > 1.0) {
        Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
      }
    }
  }

  //==========================================================================
  
  @Override
  public void onBackPressed() {
    Intent intent = new Intent(getApplicationContext(), NewCAFActivity.class);
    intent.putExtras(bundle);
    startActivity(intent);
  }

  //==========================================================================
  
  public void createAlert(String message) {
    CustomizeDialog customizeDialog = new CustomizeDialog(context);
    customizeDialog.setTitle("Digital Signature");
    customizeDialog.setMessage(message);
    customizeDialog.show();

    if (!customizeDialog.isShowing())
      customizeDialog.show();
  }
  
  //==========================================================================
}
