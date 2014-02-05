package com.tracer.activity.caf;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.testflightapp.lib.TestFlight;
import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;
import com.tracer.activity.login.LoginActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Constants;
import com.tracer.util.DataBaseHelper;
import com.tracer.util.Prefs;
import com.tracer.util.Utils;

public class NewCAFActivity extends ActionBarActivity {

	Context context = this;

	Button camera;
	Button digitalSignature;

	ImageView imagePreview;
	ImageView signaturePreview;
	ImageView scanImage;

	EditText dist_name;
	EditText dist_code;
	EditText totalCafs;
	EditText acceptedCafs;
	EditText returnedCafs;
	EditText rejectedCafs;

	TextView visit_count;;

	SharedPreferences prefs;
	Editor editor;

	String userType;
	String digital_signature_path;
	String mCurrentPhotoPath;
	String visitId;
	String authCode;
	String cafResponse;

	Uri cameraImagePath;
	JSONObject jsonObject;

	public static final int MEDIA_TYPE_IMAGE = 2;

	File photoFile;
	String strBitMap;
	Bundle bundle;
	DataBaseHelper dataBaseHelper = DataBaseHelper.getDBAdapterInstance(this);
	private static final String TAG = "NewCAFActivity";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_caf);

		prefs = Prefs.get(this);
		authCode = prefs.getString(Constants.AUTHCODE, null);
		userType = prefs.getString(Constants.USERTYPE, null);

		bundle = new Bundle();
		bundle = getIntent().getExtras();

		visitId = bundle.getString(Constants.VISITCODE);

		camera = (Button) findViewById(R.id.cameraButton);
		digitalSignature = (Button) findViewById(R.id.digitalSignatureButton);

		imagePreview = (ImageView) findViewById(R.id.imagePreview);
		signaturePreview = (ImageView) findViewById(R.id.signaturePreview);
		scanImage = (ImageView) findViewById(R.id.scanImage);

		visit_count = (TextView) findViewById(R.id.visit_count_value);

		dist_code = (EditText) findViewById(R.id.et_dist_code);
		dist_name = (EditText) findViewById(R.id.et_dist_name);

		totalCafs = (EditText) findViewById(R.id.et_total_caf_value);
		acceptedCafs = (EditText) findViewById(R.id.et_accepted_caf_value);
		rejectedCafs = (EditText) findViewById(R.id.et_rejected_caf_value);
		returnedCafs = (EditText) findViewById(R.id.et_returned_caf_value);

		dist_name.setText(bundle.getString(Constants.DISTRIBUTORNAME));
		dist_code.setText(bundle.getString(Constants.DISTRIBUTORCODE));

		visit_count.setText(bundle.getString(Constants.VISITCOUNT));

		/**
		 * Called when user clicks on barcode scan image, in order to scan the
		 * barcode.
		 */
		scanImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, Constants.BARCODE_SCAN_RESULT);
			}
		});

	}

	/**
	 * Method is called when the user click on Take Picture button in order to
	 * capture the image through the camera.
	 * 
	 * @param view
	 */
	public void onCameraButtonClick(View view) {

		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		this.startActivityForResult(camera, Constants.PICTURE_RESULT);

	}

	/**
	 * Method is called when the user click on Digital Signature button in order
	 * to take the digital signature.
	 * 
	 * @param view
	 */
	public void onDigitalSignatureButtonClick(View view) {
		startActivityForResult(new Intent(getApplicationContext(), DigitalSignatureActivity.class).putExtras(bundle),
				Constants.DIGITAL_SIGNATURE_RESULT);
		overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
	}

	/**
	 * Method is called when the user clicks on Submit button for sending the CAF
	 * details to the server.
	 * 
	 * @param view
	 */
	public void saveCAF(View view) {
		boolean gprsStatus = Utils.getConnectivityStatusString(getApplicationContext());
		if ((!totalCafs.getText().toString().equalsIgnoreCase("")) && (digital_signature_path != null) && (strBitMap != null)) {
			Toast.makeText(getApplicationContext(), "CAF has been successfully submitted.", Toast.LENGTH_LONG).show();
			if (gprsStatus) {
				String digitalSignatureData = imageBase64Encode(digital_signature_path);
				// String imageCaptureData = imageBase64Encode(digital_signature_path);

				System.out.println("Network Enabled");
				new sendCAFDetails().execute(authCode, totalCafs.getText().toString(), acceptedCafs.getText().toString(), rejectedCafs.getText()
						.toString(), returnedCafs.getText().toString(), strBitMap, digitalSignatureData, visitId);
			} else {
				dataBaseHelper.checkAndOpenDatabase();

				ContentValues cafDetails = new ContentValues();
				cafDetails.put(Constants.TOTAL_CAF_COUNT, totalCafs.getText().toString());
				cafDetails.put(Constants.ACCEPTED_CAF_COUNT, acceptedCafs.getText().toString());
				cafDetails.put(Constants.REJECTED_CAF_COUNT, rejectedCafs.getText().toString());
				cafDetails.put(Constants.RETURNED_CAF_COUNT, returnedCafs.getText().toString());
				cafDetails.put(Constants.DISTRIBUTOR_PHOTO_PATH, digital_signature_path);
				cafDetails.put(Constants.DISTRIBUTOR_SIGNATURE_PATH, digital_signature_path);
				cafDetails.put(Constants.RUNNER_VISIT_ID, 1);

				long recordId = dataBaseHelper.insertRecordsInDB("caf_collection_details", null, cafDetails);
				System.out.println(recordId);

				editor = prefs.edit();
				editor.putBoolean("hasDBRecords", true);
				editor.commit();
				startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
				overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please provide Digital Signature, Image and Total CAFs details", Toast.LENGTH_SHORT).show();
			// totalCafs.requestFocus();
		}
	}

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
					imagePreview.setImageBitmap(pic);
					imagePreview.invalidate();
					camera.setVisibility(View.GONE);
					imagePreview.setVisibility(View.VISIBLE);
				}

			}
		}
		/**
		 * Get the barcode scan result and displaying result in the distributor code
		 * field.
		 */
		else if (requestCode == Constants.BARCODE_SCAN_RESULT) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				dist_code.setText(contents);
				Log.i("xZing", "contents: " + contents + " format: " + format);
				// Toast.makeText(getApplicationContext(), "contents: " +
				// contents + " format: " + format, Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_CANCELED) {
				Log.i("xZing", "Cancelled");
			}
		}
		/**
		 * Get the digital signature image and set the image to the image view for
		 * the preview
		 */
		else if (requestCode == Constants.DIGITAL_SIGNATURE_RESULT) {
			if (resultCode == Activity.RESULT_OK) {
				digital_signature_path = data.getStringExtra("digital_signature_path");
				System.out.println(digital_signature_path);
				digitalSignature.setVisibility(View.INVISIBLE);

				signaturePreview.setVisibility(View.VISIBLE);
				signaturePreview.setImageURI(Uri.parse(digital_signature_path));
				// Toast.makeText(getApplicationContext(),
				// digital_signature_path, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Method for creating Menus in the current View
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Action to be performed when the clicked on Menu icons.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
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

	/** Create a file Uri for saving an image or video */
	class sendCAFDetails extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		protected String doInBackground(String... urls) {
			try {
				TestFlight.log("NewCAFActivity.sendCAFDetails()");
				System.out.println(urls[0]);
				jsonObject = new JSONObject();

				jsonObject.put("authCode", urls[0]);
				jsonObject.put("totalCAF", urls[1]);
				jsonObject.put("acceptedCAF", urls[2]);
				jsonObject.put("rejectedCAF", urls[3]);
				jsonObject.put("returnedCAF", urls[4]);
				jsonObject.put("photo", urls[5]);
				jsonObject.put("signature", urls[6]);
				jsonObject.put("visitCode", urls[7]);

				System.out.println(jsonObject.toString());
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
				HttpResponse response;
				HttpPost post = new HttpPost(Constants.WEBSERVICE_BASE_URL + "caf/save");

				StringEntity se = new StringEntity(jsonObject.toString());

				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				if (response != null) {
					InputStream in = response.getEntity().getContent();
					BufferedReader rd = new BufferedReader(new InputStreamReader(in));
					String line;
					while ((line = rd.readLine()) != null) {
						// Process line...
						System.out.println("line ::::: " + line);
						JSONObject jsonObject = new JSONObject(line);
						cafResponse = jsonObject.getString("responseMessage");
						System.out.println(jsonObject.toString());
					}
					if (cafResponse.equalsIgnoreCase("ok")) {
						File dir = context.getDir("TraceR", MODE_PRIVATE);
						if (dir != null && dir.isDirectory()) {
							deleteCacheDir(dir);
						}
					}
					rd.close();
					TestFlight.passCheckpoint("NewCAFActivity.sendCAFDetails()");
				}

			} catch (Exception e) {
				TestFlight.log("NewCAFActivity.sendCAFDetails() catch Exception " + e.getMessage());
				Log.e(TAG, "NewCAFActivity.sendCAFDetails():" + e.getMessage());
			}
			return authCode;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewCAFActivity.this);
			pDialog.setMessage("Sending Data Please Wait ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
	}

	public String imageBase64Encode(String filePath) {
		Bitmap selectedImage = BitmapFactory.decodeFile(digital_signature_path);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		selectedImage.compress(Bitmap.CompressFormat.JPEG, 10, stream);
		byte[] byteArray = stream.toByteArray();
		String strBase64 = Base64.encodeToString(byteArray, 0);
		return strBase64;
	}

	public static boolean deleteCacheDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteCacheDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	@SuppressWarnings("unused")
	private void loadImageToCache(String imagePath) {
		try {
			File cachePath = new File(context.getDir("TraceR", MODE_PRIVATE), "TraceR");
			if (!cachePath.exists()) {
				cachePath.mkdir();
			}
			String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
			InputStream input = null;
			OutputStream output = null;
			URL url = new URL(imagePath);
			input = url.openStream();
			String storagePath = cachePath + "/" + imageName;
			File file = new File(storagePath);
			if (!(file.exists()) || (file.length() == 0)) {
				output = new FileOutputStream(storagePath);
				byte[] buffer = new byte[1024];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				output.close();
				input.close();
			}
		} catch (Exception e) {
			// Log.v(TAG, "File Does Not Exists at this Path:" + imagePath);
		}
	}
}
