/**
 * @author Prashanth M
 *
 */
package com.tracer.activity.runner;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.util.Constants;
import com.tracer.util.Prefs;
import com.tracer.util.Utils;

public class RunnerAttendanceActivity extends ActionBarActivity {
  ImageView runnerImage;
  String strBitMap;
  Editor editor;
  SharedPreferences prefs;
  JSONObject jsonObject;
  String cafResponse;
  String authCode;
  private static final String TAG = "RunnerAttendanceActivity";

  //==========================================================================
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_runner_attendance);
    prefs = Prefs.get(this);
    authCode = prefs.getString(Constants.AUTHCODE, null);
    runnerImage = (ImageView) findViewById(R.id.runnerImage);
  }

  //==========================================================================
  
  public void takePhoto(View view) {
    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    this.startActivityForResult(camera, Constants.PICTURE_RESULT);
  }

  //==========================================================================
  
  public void submitAttendance(View view) {
	  boolean gprsStatus = Utils.getConnectivityStatusString(getApplicationContext());
	  if(strBitMap !=null)
	  {
		  if (gprsStatus) {
			  System.out.println(prefs.getString("RunnerAttendanceImage", null));
			  new sendRunnerAttendanceDetails().execute(authCode,prefs.getString("RunnerAttendanceImage", null));
		  } 
	  }else
	  {
		  Toast.makeText(getApplicationContext(), "Please provide photo", Toast.LENGTH_SHORT).show();;
	  }
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
        
        editor = prefs.edit();
        editor.putString("RunnerAttendanceImage", strBitMap);
        editor.commit();
        
        if (pic != null) {
          runnerImage.setImageBitmap(pic);
          runnerImage.invalidate();
        }
      }
    }
  }
  
  /** Create a file Uri for saving an image or video */
  class sendRunnerAttendanceDetails extends AsyncTask<String, Void, String> {

	    private ProgressDialog pDialog;

	    //==========================================================================
	    
	    @Override
	    protected void onPostExecute(String result) {
	      super.onPostExecute(result);
	      // if (cafResponse != null && cafResponse.equalsIgnoreCase("ok")) {
	      pDialog.dismiss();
	      startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
		    overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
	    }

	    //==========================================================================
	    
	    @Override
	    protected void onProgressUpdate(Void... values) {
	      super.onProgressUpdate(values);
	    }

	    //==========================================================================
	    
	    protected String doInBackground(String... urls) {
	      try {
	    	  TestFlight.log("RunnerAttendanceActivity.sendRunnerAttendanceDetails()");
	          jsonObject = new JSONObject();
	          jsonObject.put("runnerPhoto", urls[1]);
	          jsonObject.put("authCode", urls[0]);
	          HttpClient client = new DefaultHttpClient();
	          HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);
	          HttpResponse response;
	          HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "user/runner/attendance/save");

	          StringEntity se = new StringEntity(jsonObject.toString());

	          se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	          post.setEntity(se);
	          response = client.execute(post);
	          if (response != null) {
	              InputStream in = response.getEntity().getContent();
	              BufferedReader rd = new BufferedReader(new InputStreamReader(in));
	              String line;
	              
	              while ((line = rd.readLine()) != null) {
	                JSONObject jsonObject = new JSONObject(line);
	                cafResponse = jsonObject.getString("responseMessage");
	              }
	              System.out.println(cafResponse);
	              rd.close();
	              TestFlight.passCheckpoint("NewCAFActivity.sendCAFDetails()" + cafResponse);
	            }
	      }catch(Exception e){
	    	  TestFlight.log("RunnerAttendanceActivity.sendRunnerAttendanceDetails() catch Exception " + e.getMessage());
	          Log.e(TAG, "RunnerAttendanceActivity.sendRunnerAttendanceDetails():" + e.getMessage());
	      }
		return strBitMap;
	    }

	    //==========================================================================
	    
	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();
	      pDialog = new ProgressDialog(RunnerAttendanceActivity.this);
	      pDialog.setMessage("Sending Data Please Wait ...");
	      pDialog.setIndeterminate(false);
	      pDialog.setCancelable(false);
	      pDialog.setCanceledOnTouchOutside(false);
	      pDialog.show();
	    }
  }
  
  @Override
	public void onBackPressed() {
	  	Log.i("LoginActivity", "Finishing");
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	  
}