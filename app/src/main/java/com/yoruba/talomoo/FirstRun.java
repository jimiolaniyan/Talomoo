package com.yoruba.talomoo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
					Intent i = new Intent(FirstRun.this, CategoryActivity.class);
					startActivity(i);
				}
			});
			SharedPreferences.Editor edit = mPrefs.edit();
			edit.putBoolean("firstRunSettings", false);
			edit.apply();
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
		mLanguagePrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        mLanguagePrefs.getAll().toString();
//		String str = mLanguagePrefs.getString(getString(R.string.language_preference),
//                getString(R.string.pref_language_eng));
        SharedPreferences.Editor edit = mLanguagePrefs.edit();
        edit.putString(getString(R.string.language_preference), getString(R.string.pref_language_yor));
        edit.apply();


//		locale = new Locale(lang);
//		Resources res = getBaseContext().getResources();
//		DisplayMetrics disp = res.getDisplayMetrics();
//		Configuration conf = res.getConfiguration();
//		conf.locale = locale;
//	    res.updateConfiguration(conf, disp);
	    

	    Intent i = new Intent(FirstRun.this, CategoryActivity.class);
		startActivity(i);
	}
	
}
