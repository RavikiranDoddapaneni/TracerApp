package com.tracer.activity.caf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tracer.R;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Prefs;

public class DigitalSignatureActivity extends ActionBarActivity implements OnGesturePerformedListener {
	GestureOverlayView gestureOverlayView;
	Context context = this;
	ActionBar actionBar;
	SharedPreferences prefs;
	String userName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_digital_signature);

		prefs = Prefs.get(this);
		userName = prefs.getString("user", null);

		actionBar = getSupportActionBar();
		gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
		gestureOverlayView.addOnGesturePerformedListener(this);
		gestureOverlayView.setFadeEnabled(false);

	}

	/**
	 * Called when the user clicked on Save button. Saves the digital signature
	 * in the application cache after saving.
	 * 
	 * @param view
	 */
	public void digitalSignatureDone(View view) {
		gestureOverlayView.setDrawingCacheEnabled(true);
		Bitmap bitmap = gestureOverlayView.getDrawingCache();
		File imagePath = saveBitmap(bitmap);

		// Intent intent = new Intent(getApplicationContext(),
		// NewCAFCollectionActivity.class);
		// intent.putExtra("digital_signature_path", imagePath.toString());
		// startActivity(intent);

		Intent intent = new Intent();
		intent.putExtra("digital_signature_path", imagePath.toString());
		setResult(RESULT_OK, intent);
		finish();

	}

	/**
	 * Called when the user clicked on Reset button in order to clear the
	 * digital signature screen for giving new digital signature input.
	 * 
	 * @param view
	 */

	public void onResetButton(View view) {
		gestureOverlayView.clear(false);
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
			startActivity(new Intent(getApplicationContext(), NewCAFActivity.class));
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

	/**
	 * Method is used to the save the digital signature to the application
	 * cache.
	 * 
	 * @param bitmap
	 * @return Image path where digital signature is saved.
	 */
	public File saveBitmap(Bitmap bitmap) {

		File cachePath = new File(context.getExternalCacheDir() + "/" + "Fins");
		cachePath.mkdir();
		Calendar cal = Calendar.getInstance();
		File imagePath = new File(cachePath, (cal.getTimeInMillis() + ".jpg"));

		System.out.println(imagePath.toString());

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

	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture arg1) {

	}
}