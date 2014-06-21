package com.kidgeniusdesigns.deployapp;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.maps.GeoPoint;
import com.kidgeniusdesigns.deployapp.fragments.Attendee;
import com.kidgeniusdesigns.deployapp.fragments.EventInfo;
import com.kidgeniusdesigns.deployapp.fragments.TabsPagerAdapter;
import com.kidgeniusdesigns.realdeploy.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class EventHome extends FragmentActivity {
	// Tab titles
	TabsPagerAdapter pageAdapter;
	public static FragmentManager fragmentManager;
	public static String countdown, title, creator, eventLocation, eventCode,
			description;
	public static long cd;
	private MobileServiceClient mClient;
	private MobileServiceTable<Attendee> mAttendeeTable;
	public static GeoPoint eventLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		BugSenseHandler.initAndStartSession(EventHome.this, "d76061ee");
		final ActionBar actionBar = getActionBar();
		setContentView(R.layout.activity_event_home);
		fragmentManager = getSupportFragmentManager();
		pageAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(pageAdapter);
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		bar.setTitle(title);
		creator = intent.getStringExtra("creator");
		eventCode = intent.getStringExtra("code");
		eventLocation = intent.getStringExtra("location");
		eventLatLng = geocodeAddress(eventLocation);
		description = intent.getStringExtra("descrip");
		// Toast.makeText(getApplicationContext(),
		// intent.getStringExtra("latOfEvent"), Toast.LENGTH_LONG).show();
		double millisTil = intent.getDoubleExtra("eventtime", 0.0);
		cd = (long) millisTil;
		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				pager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// probably ignore this event
			}
		};
		// Add tabs, specifying the tab's text and TabListener
		actionBar.addTab(actionBar.newTab().setText("Event Info")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Directions")
				.setTabListener(tabListener));

		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the
				// corresponding tab.
				getActionBar().setSelectedNavigationItem(position);
			}
		});
		setAttending(" checkedin");
	}

	public void startRoute(View v) {
		Calendar c = Calendar.getInstance();
		long millisToday = c.getTimeInMillis();
		long tilEvent = cd - millisToday;
		long hoursLeft = TimeUnit.MILLISECONDS.toHours(tilEvent);
		if (hoursLeft >= 8) {
			// 1. Instantiate an AlertDialog.Builder with its constructor
						AlertDialog.Builder builder = new AlertDialog.Builder(EventHome.this);

						// 2. Chain together various setter methods to set the dialog characteristics
						builder.setMessage("This is for security so we can ensure your event remains private.")
						       .setTitle("Directions Not Available until 8 hrs prior");

						// 3. Get the AlertDialog from create()
						AlertDialog dialog = builder.create();
						dialog.show();
		} else if(hoursLeft<0){
			AlertDialog.Builder builder = new AlertDialog.Builder(EventHome.this);
			builder.setMessage("This is for security so we can ensure your event remains private.")
			       .setTitle("Directions Not Available anymore");
			AlertDialog dialog = builder.create();
			dialog.show();
		}else{
			double lat = eventLatLng.getLatitudeE6() / 1E6;
			double lng = eventLatLng.getLongitudeE6() / 1E6;
			String uri = String.format(Locale.ENGLISH,
					"http://maps.google.com/maps?daddr=%f,%f (%s)", lat, lng,
					"DO NOT SHARE LOCATION");
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps",
					"com.google.android.maps.MapsActivity");
			startActivity(intent);
		}

	}

	private GeoPoint geocodeAddress(String eventLocation) {
		Geocoder coder = new Geocoder(getApplicationContext());
		List<Address> address;

		try {
			address = coder.getFromLocationName(eventLocation, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			GeoPoint p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));

			return p1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		GeoPoint nullGeo = new GeoPoint(0, 0);
		return nullGeo;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	public void markAttending(View v) {

		Toast.makeText(getApplicationContext(), "You are attending", Toast.LENGTH_LONG)
				.show();
		EventInfo.atButton.setClickable(false);
		setAttending(" attend");
	}
	
	private void setAttending(String goingOrCheckedIn){
		try {
			mClient = new MobileServiceClient(
					"https://droiddemo.azure-mobile.net/",
					"uGrjosMeSdfQaUqCPEMSgKJhADIqFY34", this);
			mAttendeeTable = mClient.getTable(Attendee.class);
			Attendee newAttendee = new Attendee();
			newAttendee.setEventCode(eventCode);
			newAttendee.setAttendee(getIntent().getStringExtra("username")+goingOrCheckedIn);
			mAttendeeTable.insert(newAttendee,
					new TableOperationCallback<Attendee>() {
						public void onCompleted(Attendee entity,
								Exception exception,
								ServiceFilterResponse response) {
							if (exception == null) {

							} else {
								// Insert failed
							}
						}
					});

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void goToCal(View v){
		Intent intent = new Intent(Intent.ACTION_EDIT);  
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", title);
		intent.putExtra("description", "Don't forget. Deply code is " + eventCode);
		intent.putExtra("beginTime", intent.getDoubleExtra("eventtime", 0.0));
		startActivity(intent);
		
	}

}