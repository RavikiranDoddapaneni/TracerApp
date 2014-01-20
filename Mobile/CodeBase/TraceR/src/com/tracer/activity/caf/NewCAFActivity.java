package com.tracer.activity.caf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.activity.beatplan.BeatPlanActivity;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Prefs;

public class NewCAFActivity extends ActionBarActivity {

	private final static int PICTURE_RESULT = 1;
	private final static int BARCODE_SCAN_RESULT = 2;
	private final static int DIGITAL_SIGNATURE_RESULT = 3;
	Context context = this;
	Button camera;
	Button digitalSignature;
	ImageView imagePreview;
	ImageView signaturePreview;
	EditText dist_name;
	ImageView scanImage;
	EditText dist_code;
	SharedPreferences prefs;
	String userName;
	Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_caf);

		prefs = Prefs.get(this);
		userName = prefs.getString("user", null);

		bundle = new Bundle();
		bundle = getIntent().getExtras();

		camera = (Button) findViewById(R.id.cameraButton);
		digitalSignature = (Button) findViewById(R.id.digitalSignatureButton);

		imagePreview = (ImageView) findViewById(R.id.imagePreview);
		signaturePreview = (ImageView) findViewById(R.id.signaturePreview);
		scanImage = (ImageView) findViewById(R.id.scanImage);

		dist_code = (EditText) findViewById(R.id.et_dist_code);
		dist_name = (EditText) findViewById(R.id.dist_name);

		dist_name.setText(bundle.getString("dist_name"));
		dist_code.setText(bundle.getString("dist_code"));

		scanImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, BARCODE_SCAN_RESULT);
			}
		});

	}

	public void onCameraButtonClick(View view) {

		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		this.startActivityForResult(camera, PICTURE_RESULT);
	}

	public void onDigitalSignatureButtonClick(View view) {
		startActivityForResult(new Intent(getApplicationContext(), DigitalSignatureActivity.class), DIGITAL_SIGNATURE_RESULT);
		overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);
	}

	public void saveCAF(View view) {
		Toast.makeText(getApplicationContext(), "CAF has been successfully submitted.", Toast.LENGTH_LONG).show();
		startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
		overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICTURE_RESULT) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				Bitmap pic = (Bitmap) b.get("data");

				if (pic != null) {
					imagePreview.setImageBitmap(pic);
					imagePreview.invalidate();
					camera.setVisibility(View.GONE);
					imagePreview.setVisibility(View.VISIBLE);
				}

			}
		} else if (requestCode == BARCODE_SCAN_RESULT) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				dist_code.setText(contents);
				Log.i("xZing", "contents: " + contents + " format: " + format);
				Toast.makeText(getApplicationContext(), "contents: " + contents + " format: " + format, Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_CANCELED) {
				Log.i("xZing", "Cancelled");
			}
		} else if (requestCode == DIGITAL_SIGNATURE_RESULT) {
			if (resultCode == Activity.RESULT_OK) {
				String digital_signature_path = data.getStringExtra("digital_signature_path");
				digitalSignature.setVisibility(View.GONE);

				signaturePreview.setVisibility(View.VISIBLE);
				signaturePreview.setImageURI(Uri.parse(digital_signature_path));
				Toast.makeText(getApplicationContext(), digital_signature_path, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			startActivity(new Intent(getApplicationContext(), BeatPlanActivity.class));
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		} else if (item.getItemId() == R.id.home_button) {
			if (userName.equalsIgnoreCase("manager")) {
				startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
			} else if (userName.equalsIgnoreCase("teamleader")) {
				startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
			} else if (userName.equalsIgnoreCase("runner")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			}
			overridePendingTransition(R.anim.from_left_anim, R.anim.to_right_anim);
		}
		return true;
	}
}
