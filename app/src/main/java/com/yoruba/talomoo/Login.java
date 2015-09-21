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

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.json.JSONObject;
//
//import com.facebook.FacebookRequestError;
//import com.facebook.HttpMethod;
//import com.facebook.LoggingBehavior;
//import com.facebook.Request;
//import com.facebook.Request.Callback;
//import com.facebook.RequestAsyncTask;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.Session.OpenRequest;
//import com.facebook.SessionState;
//import com.facebook.Settings;
//import com.parse.LogInCallback;
//import com.parse.ParseException;
//import com.parse.ParseFacebookUtils;
//import com.parse.ParseUser;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//public class Login extends Activity {
//
//	Dialog progressDialog;
//	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
//	public static final int REAUTH_ACTIVITY_CODE = 100;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		ParseUser currentUser = ParseUser.getCurrentUser();
//		if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
//			postToFeed();
//		}else {
//			loginUser();
//		}
//
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (requestCode != RESULT_CANCELED) {
//			ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
////			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//		}
//
//		if (requestCode == REAUTH_ACTIVITY_CODE) {
//			Log.d(" Facebook Login", " Login  REAUTH_ACTIVITY_CODe");
//			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//		}
//
//	}
//
//	private void loginUser() {
//		Login.this.progressDialog = ProgressDialog.show(Login.this, "", " Logging In", true);
//		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
//				"user_relationships", "user_birthday", "user_location");
//		ParseFacebookUtils.logIn(permissions, Login.this, new LogInCallback() {
//			
//			@Override
//			public void done(ParseUser user, ParseException e) {
//				Login.this.progressDialog.dismiss();
//				if (user == null) {
//					Log.d(" Facebook Login", " Login Unsuccessful");
//					Toast.makeText(Login.this, " Login Failed: Please Try Again ", Toast.LENGTH_SHORT).show();
//					finish();
//				}else if (user.isNew()) {
//					Log.d(" Facebook Login", " Sign Up successful");
//					Toast.makeText(Login.this, " Register successful ", Toast.LENGTH_SHORT).show();
//					postToFeed();
//				}else {
//					Log.d(" Facebook Login", " Login successful");
//					Toast.makeText(Login.this, " Login successful ", Toast.LENGTH_SHORT).show();
//					postToFeed();
//				}
//
//			}
//		});
//	}
//
//	private void postToFeed() {
////		ParseUser user = ParseUser.getCurrentUser();
////		if (user.isNew()) {
////			if (Session.getActiveSession() != null) {
////				Session.NewPermissionsRequest reauthPermission = new Session.NewPermissionsRequest(this, PERMISSIONS);
////				Session.getActiveSession().requestNewPublishPermissions(reauthPermission);
////				reauthPermission.setRequestCode(REAUTH_ACTIVITY_CODE);	
////				Log.d(" Post ", " New User permissions check");
////				connectToFacebook();
////			}
////			
////		}else {
//			connectToFacebook();
////		}
//		
//	}
//
//	private void connectToFacebook() {
//		Session session = new Session(Login.this);
//		Session.setActiveSession(session);
//		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
//
//		Session.StatusCallback statusCallBack= new Session.StatusCallback() {
//
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				String message = "Facebook Status Changed - " + session.getState() + " - Exception " + exception;
//				Log.w(" Facebook ", message);
//				if (session.isOpened() || session.getPermissions().contains("publish_actions")) {
//					publishPost();
//					Log.w(" Facebook ", " Session Posting Start");
//				}else if (session.isOpened()) {
//					OpenRequest open = new OpenRequest(Login.this).setCallback(this);
//					List<String> permissions = new ArrayList<String>();
//					permissions.add("publish_actions");
//					open.setPermissions(permissions);
//					session.openForPublish(open);
//					Log.w(" Facebook ", " Session Posting Else Part");
//				}
//			}
//
//		};
//
//		if (!session.isOpened() && !session.isClosed() && !(session.getState() != SessionState.OPENING)) {
//			session.openForRead(new Session.OpenRequest(Login.this).setCallback(statusCallBack));
//			Log.w(" Facebook ", " Session open for read");
//		}else{
//			Session.openActiveSession(Login.this, true, statusCallBack);
//			Log.w(" Facebook ", "Session Active");
//		}
//
//	}
//
//	protected void publishPost() {
//		Login.this.progressDialog = ProgressDialog.show(Login.this, "", " Posting Question...", true);
//		Session session = Session.openActiveSessionFromCache(this);
//		Bundle bundle = getIntent().getExtras();
//		String question = bundle.getString(DBHelper.KEY_QUESTION_TEXT);
//
//		if (session.isOpened() && !session.getPermissions().contains("publish_actions")) {
//			Session.NewPermissionsRequest reauthPermission = new Session.NewPermissionsRequest(this, PERMISSIONS);
//			reauthPermission.setRequestCode(REAUTH_ACTIVITY_CODE);			
//			session.requestNewPublishPermissions(reauthPermission);
//			Log.d(" Post ", " No permissions check");
//		}
//
//		final Bundle postStuff	= new Bundle();
//		postStuff.putString("name", "Talomoo");
//		postStuff.putString("caption", "Welcome on Board");
//		postStuff.putString("description", " Trial Post ");
//		postStuff.putString("message" , question);
//
//		Request.Callback callBack  = new Callback() {
//
//			@Override
//			public void onCompleted(Response response) {
//				FacebookRequestError error = response.getError();
//				if (error  != null) {
//					Login.this.progressDialog.dismiss();
//					Log.d(" Post ", error.getErrorMessage());
//					Toast.makeText(getApplicationContext(), " Poor Connection: Posting Failed", Toast.LENGTH_SHORT).show();
//					finish();
//				}else {
//					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
//					String postId = null;
//					try {
//						postId = graphResponse.getString("id");
//					} catch (Exception e) {
//						Log.i(" Facebook Error", "JSON Error " + e.getMessage());
//					}
//
//					Log.d(" Post ", "Post Successful");
//					Login.this.progressDialog.dismiss();
//					Toast.makeText(getApplicationContext(), " Your Question Is Online", Toast.LENGTH_SHORT).show();
//					finish();
//				}
//			}
//		};
//
//		Request request = new Request(Session.getActiveSession(), "me/feed", postStuff, HttpMethod.POST, callBack);
//		RequestAsyncTask task = new RequestAsyncTask(request);
//		task.execute();
//		
//
//	}
//
//}
