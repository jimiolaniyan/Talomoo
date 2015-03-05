package com.yoruba.talomoo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Locale;

@SuppressLint("ShowToast")
public class MainActivity extends FragmentActivity {

	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	Animation anim;
	LinearLayout text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences setted = getSharedPreferences(FirstRun.PREF_NAME, 0);
		String clicked = setted.getString("used", null);
		if (clicked != null) {
			Locale locale = new Locale("es");
			Locale.setDefault(locale);
			Resources res = this.getResources();
			DisplayMetrics disp = res.getDisplayMetrics();
			Configuration conf = res.getConfiguration();
			conf.locale = locale;
		    res.updateConfiguration(conf, disp);
		}
		setContentView(R.layout.activity_main);
		cd = new ConnectionDetector(getApplicationContext());
		text = (LinearLayout) findViewById(R.id.main);
		anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
		text.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			
		
			@Override
			public void onAnimationRepeat(Animation arg0) throws UnsupportedOperationException{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationStart(Animation arg0) throws UnsupportedOperationException{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				LinearLayout lay = (LinearLayout) findViewById(R.id.root);
				lay.setVisibility(View.VISIBLE);
			}
		});
		
	}



	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	public void OnClickPlayOffline(View v) {
		Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
		startActivity(intent);
	}


	public void OnClickPlayOnline(View v){

		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent) {
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser == null && ParseFacebookUtils.isLinked(currentUser))  { //possible error in null-check
				Intent myintent = new Intent(MainActivity.this, CategoryActivity.class);
				startActivity(myintent);

			}else {
				Intent myintent = new Intent(MainActivity.this, SignUpAndLogin.class);
				startActivity(myintent);
				Toast.makeText(this, "Register to continue" , Toast.LENGTH_SHORT);
			}
		}else {
			FragmentManager fm = getSupportFragmentManager();
			AlertFragment df = new AlertFragment();
			df.show(fm, "Dialog Fragment");
		}
	}
	
	private class AlertFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity()).setIcon(R.drawable._no)
					.setTitle("No internet connection detected")
					.setMessage("Would You Like To Play Offline?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
							startActivity(intent);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
			}).create();
		}
	}

}
