package com.yoruba.talomoo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.easy.facebook.android.apicall.GraphApi;
import com.easy.facebook.android.data.User;
import com.easy.facebook.android.error.EasyFacebookError;
import com.easy.facebook.android.facebook.FBLoginManager;
import com.easy.facebook.android.facebook.Facebook;
import com.easy.facebook.android.facebook.LoginListener;

public class Login extends FragmentActivity implements LoginListener{

	private FBLoginManager fblogin;
	public final String APP_ID = "152710554939550";
	Boolean success = false;
	Dialog mDialog;
	ConnectionDetector cd;
	Boolean isInternetPresent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final int id = bundle.getInt(DBHelper.KEY_ID);
        Toast.makeText(this, " " + id, Toast.LENGTH_SHORT ).show();
		connectToFaceBooK();
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		setTitle("FaceBook Post");

		if (!isInternetPresent) {
			FragmentManager fm = getSupportFragmentManager();
			AlertDFragment df = new AlertDFragment();
			df.show(fm, "Dialog Fragment");
		}
	}

/*	@Override
	protected void onResume() {
		super.onResume();
		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == true) {
			this.mDialog = ProgressDialog.show(this, "", getResources().getString(R.string.posting));
		}
	}*/

	private void connectToFaceBooK() {
		String[] permissions = {
				"publish_stream",
				"publish_actions"
		};

		fblogin = new FBLoginManager(this, R.layout.activity_login, APP_ID, permissions);
		if (fblogin.existsSavedFacebook()) {
			fblogin.loadFacebook();
		}else {
			fblogin.login();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			fblogin.loginSuccess(data);
		}

	}

	public void loginSuccess(final Facebook facebook) {
		Bundle b = getIntent().getExtras();
		final String text = b.getString(DBHelper.KEY_QUESTION_TEXT);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				GraphApi graphApi = new GraphApi(facebook);
				User user = new User();
				try{
					user = graphApi.getMyAccountInfo();
					graphApi.setStatus(text);
					Log.d("Post successful", "Post successful");
					showToast(getResources().getString(R.string.successful));
				} catch(EasyFacebookError e){
					Log.w("FaceBook", e.getMessage());
				}

				if (!success) {
					finish();
				}


			}
		});
		thread.start();
	}

	public void  showToast(final String toast) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(Login.this, toast, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void logoutSuccess() {
		fblogin.displayToast("Logout Success!");
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
        finish();
	}

	public void loginFail() {
		fblogin.displayToast("Posting Failed, Try Again!");
		finish();
	}
	
	@SuppressLint("ValidFragment")
    private class AlertDFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle("No internet connection")
					.setMessage("You are currently not connected to the internet. \nAn internet connection is required to post questions to Facebook")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create();
		}
	}
}
