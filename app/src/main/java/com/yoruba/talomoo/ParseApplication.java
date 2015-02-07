package com.yoruba.talomoo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class ParseApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "i2D9P9GjEEQV2VrU8MsKsiLZ63ueMaliX4IzjVeA", "TvjMdw4XPngTbj8y3BZ8KQkatmFO3Pk7mHiGPFPj");
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
}
