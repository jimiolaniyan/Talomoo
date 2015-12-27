package com.yoruba.talomoo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by gidimo on 24/12/2015.
 */
public class LanguageUtil {
    public void setResource(String lang, Resources resources){
        Locale locale = new Locale(lang);
        DisplayMetrics disp = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = locale;
        resources.updateConfiguration(conf, disp);
    }
}
