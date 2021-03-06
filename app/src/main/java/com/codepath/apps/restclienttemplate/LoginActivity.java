package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthLoginActionBarActivity;

import org.json.JSONException;

import okhttp3.Headers;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	public static final String TAG = "LoginActivity";
	SampleModelDao sampleModelDao;
	Button btLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btLogin = findViewById(R.id.btLogin);

		final SampleModel sampleModel = new SampleModel();
		sampleModel.setName("CodePath");

		sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();

		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				sampleModelDao.insertModel(sampleModel);
			}
		});
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Log.d(TAG, "OnLoginSuccess");
		btLogin.setVisibility(View.INVISIBLE);

		getClient().verifyCredentials(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Headers headers, JSON json) {
				try {
					User.currentUser = User.fromJsonObject(json.jsonObject);
					Intent i = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
				Log.e(TAG, "OnFailure: " + throwable.getMessage());
			}
		});

	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
