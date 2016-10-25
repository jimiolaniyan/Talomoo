package com.yoruba.talomoo;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.parse.ParseUser;
import com.yoruba.talomoo.util.LanguageUtil;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    Locale locale;
    public static int prefIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.language_preference)));
	}

    private void bindPreferenceSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String strValue = newValue.toString();
        String Pref = PreferenceManager.getDefaultSharedPreferences(this).getAll().toString();

        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            prefIndex = listPreference.findIndexOfValue(strValue);
            if(prefIndex  >= 0){
                if(prefIndex == 0){
                    CharSequence sequence = listPreference.getEntries()[prefIndex + 1];
                    preference.setSummary("To " + sequence);
//                    changeConfig("en");
                    Toast.makeText(this, Pref, Toast.LENGTH_SHORT).show();
//                    Intent intent = getIntent();
//                    finish();
//                    startActivity(intent);

                }else if(prefIndex == 1){
                    CharSequence sequence = listPreference.getEntries()[prefIndex - 1];
                    preference.setSummary("To " + sequence);
                }

            }

        }
        else {
            preference.setSummary(strValue);
        }
        return true;
    }

    private void changeConfig(String lang) {
        new LanguageUtil().setResource(lang, getBaseContext().getResources());
        Intent refresh = new Intent(this, SettingsActivity.class);
        startActivity(refresh);

//        SharedPreferences pref = getSharedPreferences(FirstRun.PREF_NAME, 0);
//        SharedPreferences.Editor edited = pref.edit();
//        edited.putString("used", "yoruba");
//        edited.commit();

//        Intent i = new Intent(SettingsActivity.this, CategoryActivity.class);
//        startActivity(i);
//        finish();
    }
}
	
