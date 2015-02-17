package com.yoruba.talomoo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class FirstRun extends Activity {
	public static final String PREF_NAME = "My Preferece";
	SharedPreferences mLanguagePrefs;
	Button englishButton;
	Button yorubaButton;
	Locale locale;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences mPrefs = getSharedPreferences(PREF_NAME, 0);
		boolean firstrun = mPrefs.getBoolean("firstRunSettings", true);
		
		if (firstrun) {
			setContentView(R.layout.first_run);
			Typeface tpf = Typeface.createFromAsset(getAssets(), "Purisa.ttf");
			((TextView) findViewById(android.R.id.text1)).setTypeface(tpf);
			((TextView) findViewById(android.R.id.text2)).setTypeface(tpf);
			yorubaButton = (Button) findViewById(R.id.yoruba);
			yorubaButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkLanguageClicked("es");
				}
			});
			englishButton = (Button) findViewById(R.id.english);
			englishButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(FirstRun.this, MainActivity.class);
					startActivity(i);
				}
			});
			SharedPreferences.Editor edit = mPrefs.edit();
			edit.putBoolean("firstRunSettings", false);
			edit.commit();
		}else {
			Intent i = new Intent(FirstRun.this, CategoryActivity.class);
			startActivity(i);
		}
		
		
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}
	

	private void checkLanguageClicked(String lang) {
//		mLanguagePrefs = PreferenceManager.getDefaultSharedPreferences(this);
//		String str = mLanguagePrefs.getString("language_preference", "Eng");
//		if (!str.equals("Eng")) {
//			SharedPreferences.Editor edit = mLanguagePrefs.edit();
//			edit.putString("language_preference", "Yor");
//			edit.commit();
//		}
		
		locale = new Locale(lang);
		Resources res = getBaseContext().getResources();
		DisplayMetrics disp = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = locale;
	    res.updateConfiguration(conf, disp);
	    
	    SharedPreferences pref = getSharedPreferences(PREF_NAME, 0);
	    SharedPreferences.Editor edit = pref.edit();
	    edit.putString("used", "yoruba");
	    edit.commit();
	    
	    Intent i = new Intent(FirstRun.this, MainActivity.class);
		startActivity(i);
	}
	
}
