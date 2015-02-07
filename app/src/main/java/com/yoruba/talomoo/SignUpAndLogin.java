package com.yoruba.talomoo;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpAndLogin extends Activity {

	Button signupbutton;
	Button FacebookLogin;
	Button LogOut;
	EditText username;
	EditText password;
	String txtuser;
	Dialog progressDialog;
	String txtpass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_and_login);
		setupActionBar();
		signUp();
		FacebookLogin = (Button) findViewById(R.id.facebookbtn);
		FacebookLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFacebookLogin();
			}
		});
		
		LogOut = (Button) findViewById(R.id.logout_btn);
		LogOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ParseUser.logOut();
				finish();
			}
		});
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			startOptionsActivity();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onFacebookLogin() {
		SignUpAndLogin.this.progressDialog = ProgressDialog.show(
				SignUpAndLogin.this, "", "Logging in...", true);
//		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
//				"user_relationships", "user_birthday", "user_location");
		ParseFacebookUtils.logIn(SignUpAndLogin.this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				SignUpAndLogin.this.progressDialog.dismiss();
				if (user == null) {
					Log.d("MyApp", "Sign Up Cancelled");
				}else if (user.isNew()) {
					Log.d("MyApp", "User signed up and logged in through Facebook!");
					startOptionsActivity();
				}else {
					Log.d("MyApp", "User logged in through Facebook!");
					startOptionsActivity();
				}
			}
		});
	}

	private void signUp() {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		signupbutton = (Button) findViewById(R.id.signupbtn);
		signupbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				txtuser = username.getText().toString();
				txtpass = password.getText().toString();

				if (txtuser.equals("") || txtpass.equals("")) {
					Toast.makeText(getApplicationContext(), "Invalid Username or password", Toast.LENGTH_SHORT).show();
				}else {
					SignUpAndLogin.this.progressDialog = ProgressDialog.show(
							SignUpAndLogin.this, "Confirmation", "Creating your account", true);
					ParseUser user = new ParseUser();
					user.setUsername(txtuser);
					user.setPassword(txtpass);
					user.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								Intent i = new Intent(SignUpAndLogin.this, CategoryActivity.class);
								startActivity(i);
								SignUpAndLogin.this.progressDialog.dismiss();
							}else {
								Toast.makeText(getApplicationContext(), "Username not available or No Internet", Toast.LENGTH_SHORT).show();
								SignUpAndLogin.this.progressDialog.dismiss();
							}
						}
					});
				}
			}
		});

	}
	
	private void startOptionsActivity() {
		Intent intent = new Intent(SignUpAndLogin.this, CategoryActivity.class);
		startActivity(intent);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up_and_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
