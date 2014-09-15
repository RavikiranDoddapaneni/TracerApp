/**
 * @author Prashanth M
 *
 */
package com.tracer.activity.runner;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.tracer.R;
import com.tracer.util.Constants;

public class RunnerAttendanceActivity extends ActionBarActivity {
  ImageView runnerImage;
  String strBitMap;

  //==========================================================================
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_runner_attendance);

    runnerImage = (ImageView) findViewById(R.id.runnerImage);
  }

  //==========================================================================
  
  public void takePhoto(View view) {
    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    this.startActivityForResult(camera, Constants.PICTURE_RESULT);
  }

  //==========================================================================
  
  public void submitAttendance(View view) {
    startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
    overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
  }

  //==========================================================================
  
  /**
   * Method is called in order to capture the data after the user takes digital
   * signature or after scanning the barcode or after taking the picture.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    /**
     * Get the captured image and set the image to the image view for the
     * preview
     */
    if (requestCode == Constants.PICTURE_RESULT) {
      if (resultCode == Activity.RESULT_OK) {
        Bundle b = data.getExtras();
        Bitmap pic = (Bitmap) b.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        strBitMap = Base64.encodeToString(byteArray, Base64.DEFAULT);
        
        if (pic != null) {
          runnerImage.setImageBitmap(pic);
          runnerImage.invalidate();
        }
      }
    }
  }

  //==========================================================================
}