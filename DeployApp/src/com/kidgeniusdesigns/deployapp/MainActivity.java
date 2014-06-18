package com.kidgeniusdesigns.deployapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.kidgeniusdesigns.realdeploy.R;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener {
	private static final int STATE_DEFAULT = 0;
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;
	private static final String SAVED_PROGRESS = "sign_in_progress";
	private GoogleApiClient mGoogleApiClient;
	private int mSignInProgress;
	private PendingIntent mSignInIntent;
	private SignInButton mSignInButton;
	public static String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 //dummy username for testing purposes
//		Intent i=new Intent(this, HomeScreen.class);
//		i.putExtra("username", "Tester tester");
//		username="Tester tester";
//		startActivity(i);
		
		
		
		mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(this);
		if (savedInstanceState != null) {
			mSignInProgress = savedInstanceState.getInt(SAVED_PROGRESS,
					STATE_DEFAULT);
		}
		mGoogleApiClient = buildGoogleApiClient();		
	}

	private GoogleApiClient buildGoogleApiClient() {
		return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SAVED_PROGRESS, mSignInProgress);
	}

	@Override
	public void onClick(View v) {
		if (!mGoogleApiClient.isConnecting()) {
			switch (v.getId()) {
			case R.id.sign_in_button:
				resolveSignInError();
				break;
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInButton.setEnabled(false);

		Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		username=currentUser.getDisplayName();
		Toast toast=Toast.makeText(getApplicationContext(),
				"Welcome " + username, Toast.LENGTH_LONG);
		
		toast.setGravity(Gravity.CENTER, 0, -150);
		toast.show();
		mSignInProgress = STATE_DEFAULT;
		Intent i=new Intent(this, HomeScreen.class);
		i.putExtra("username", currentUser.getDisplayName());
		startActivity(i);
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mSignInProgress != STATE_IN_PROGRESS) {
			mSignInIntent = result.getResolution();
			if (mSignInProgress == STATE_SIGN_IN) {
				resolveSignInError();
			}
		}
	}

	private void resolveSignInError() {
		if (mSignInIntent != null) {
			try {
				mSignInProgress = STATE_IN_PROGRESS;
				startIntentSenderForResult(mSignInIntent.getIntentSender(), 0,
						null, 0, 0, 0);
			} catch (SendIntentException e) {
				Log.i("Log error",
						"Sign in intent could not be sent: "
								+ e.getLocalizedMessage());
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Error signing in", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				mSignInProgress = STATE_SIGN_IN;
			} else {
				mSignInProgress = STATE_DEFAULT;
			}
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}
}
