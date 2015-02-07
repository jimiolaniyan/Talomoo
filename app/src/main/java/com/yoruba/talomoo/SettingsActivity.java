package com.yoruba.talomoo;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.parse.ParseUser;
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		userSettings();
	}

	private void userSettings() {
		
		Preference pref = findPreference("login");
		PreferenceCategory prefCategory = (PreferenceCategory) findPreference("log_in");
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null || !currentUser.isAuthenticated()) {
			prefCategory.removePreference(pref);
		}else {
				pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Preference pref = findPreference("login");
						PreferenceCategory prefCategory = (PreferenceCategory) findPreference("log_in");
						ParseUser.logOut();
						prefCategory.removePreference(pref);
						Intent i = new Intent(SettingsActivity.this, SignUpAndLogin.class);
						startActivity(i);
						finish();
						return false;
					}
				});
			}
			
	}
}
	
