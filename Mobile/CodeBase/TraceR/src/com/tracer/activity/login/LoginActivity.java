package com.tracer.activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tracer.R;
import com.tracer.activity.runner.RunnerHomeActivity;
import com.tracer.activity.runner.RunnersActivity;
import com.tracer.util.Prefs;

public class LoginActivity extends ActionBarActivity {

	ActionBar actionBar;
	EditText username;
	EditText password;
	String entered_username;
	String entered_password;
	RelativeLayout loginLayout;
	ImageView imageView;
	SharedPreferences preferences;
	Editor editor;
	RelativeLayout loginRelLayout;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginLayout = (RelativeLayout) findViewById(R.id.login_block);
		loginRelLayout = (RelativeLayout) findViewById(R.id.login_layout);
		imageView = (ImageView) findViewById(R.id.appLogoImage);

		preferences = Prefs.get(this);

		/**
		 * Below code is used to display animation for the App logo on the App
		 * launch Screen
		 */

		TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, -250);
		tAnimation.setDuration(2500);
		tAnimation.setRepeatCount(0);
		tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		tAnimation.setFillAfter(true);
		tAnimation.setAnimationListener(new AnimationListener() {

			/** Called when the animation is started. */
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			/** Called when the animation is completed. */
			@Override
			public void onAnimationEnd(Animation animation) {
				Animation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new DecelerateInterpolator());
				fadeIn.setDuration(500);
				loginLayout.setAnimation(fadeIn);
				loginLayout.setVisibility(View.VISIBLE);
			}
		});

		imageView.startAnimation(tAnimation);
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);

	}

	/**
	 * Method will be called when user clicks on login button.
	 * 
	 * @param view
	 * 
	 */
	public void login(View view) {
		entered_username = username.getText().toString();
		entered_password = password.getText().toString();
		if (entered_username.equalsIgnoreCase(entered_password)) {

			editor = preferences.edit();
			editor.putString("user", entered_username);
			editor.commit();

			Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
			if (entered_username.equals("runner")) {
				startActivity(new Intent(getApplicationContext(), RunnerHomeActivity.class));
			} else if (entered_username.equals("teamleader")) {
				startActivity(new Intent(getApplicationContext(), RunnersActivity.class));
			}
			overridePendingTransition(R.anim.from_right_anim, R.anim.to_left_anim);

		} else {
			Toast.makeText(getApplicationContext(), "Login Failed, Please try Again!", Toast.LENGTH_SHORT).show();
		}

	}
}
