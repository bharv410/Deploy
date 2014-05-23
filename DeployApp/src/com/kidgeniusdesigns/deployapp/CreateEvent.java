package com.kidgeniusdesigns.deployapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coreform.open.android.formidablevalidation.RegExpressionValueValidator;
import com.coreform.open.android.formidablevalidation.ValidationManager;
//import com.coreform.open.android.formidablevalidation.RegExpressionValueValidator;
//import com.coreform.open.android.formidablevalidation.ValidationManager;
import com.kidgeniusdesigns.deployapp.fragments.DatePickerFragment;
import com.kidgeniusdesigns.deployapp.fragments.Events;
import com.kidgeniusdesigns.deployapp.fragments.TimePickerFragment;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class CreateEvent extends FragmentActivity implements OnItemClickListener {
	EditText eventTitle, eventCode, descripBox;
	AutoCompleteTextView locationBox;
	public static Calendar tilEvent;
	private MobileServiceClient mClient;
	private MobileServiceTable<Events> mToDoTable;
	private ProgressBar mProgressBar;
	String partyTime;
	ValidationManager mValidationManager;
	
	private static final String LOG_TAG = "GooglePlaces";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyCgP3QVf6vpoGqZJxlMY84RnYRo_BZ8JbI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		CreateEvent.tilEvent = Calendar.getInstance();
		eventTitle = (EditText) findViewById(R.id.eventTitle);
		eventCode = (EditText) findViewById(R.id.eventCode);
		locationBox = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		descripBox= (EditText) findViewById(R.id.descriptBox);
		locationBox.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		locationBox.setOnItemClickListener(this);
		eventTitle.requestFocus();
		mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
		mProgressBar.setVisibility(ProgressBar.GONE);

		try {
			mClient = new MobileServiceClient(
					"https://droiddemo.azure-mobile.net/",
					"uGrjosMeSdfQaUqCPEMSgKJhADIqFY34", this)
					.withFilter(new ProgressFilter());
			mToDoTable = mClient.getTable(Events.class);
		} catch (Exception e) {
			System.out.print("Coudnt get table");
		}
		mValidationManager = new ValidationManager(this);
		mValidationManager
				.add("eventTitleError", new RegExpressionValueValidator(
						eventCode, "^[a-zA-Z0-9\\-'\\s]{3,}$",
						"please enter event code."));
		mValidationManager
				.add("eventTitleError", new RegExpressionValueValidator(
						eventTitle, "^[a-zA-Z0-9\\-'\\s]{3,}$",
						"please enter event title."));
	}
	
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void saveEvent(View v) {
		if (mValidationManager.validateAllAndSetError()) {
			addItem();
	}

	}

	public void addItem() {
		String eventLocation = locationBox.getText().toString();

		Date calTime = tilEvent.getTime();
		String title = eventTitle.getText().toString();
		String code = eventCode.getText().toString();

		Events item = new Events();

		item.setTitle(title);
		item.setEventCode(code);
		Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();
		item.setComplete(false);
		item.setOwnerId(getIntent().getStringExtra("username"));
		item.setLocation(eventLocation);
		item.setTime(calTime.getTime());
		if(!descripBox.getText().toString().equals(""))
			item.setDescrip(descripBox.getText().toString());
		mToDoTable.insert(item, new TableOperationCallback<Events>() {

			public void onCompleted(Events entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					Intent i = new Intent(getApplicationContext(),
							HomeScreen.class);
					i.putExtra("eventcode", entity.getEventCode());
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), "error saving",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private class ProgressFilter implements ServiceFilter {

		@Override
		public void handleRequest(ServiceFilterRequest request,
				NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mProgressBar != null)
						mProgressBar.setVisibility(ProgressBar.VISIBLE);
				}
			});

			nextServiceFilterCallback.onNext(request,
					new ServiceFilterResponseCallback() {

						@Override
						public void onResponse(ServiceFilterResponse response,
								Exception exception) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (mProgressBar != null)
										mProgressBar
												.setVisibility(ProgressBar.GONE);
								}
							});

							if (responseCallback != null)
								responseCallback
										.onResponse(response, exception);
						}
					});
		}
	}
	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=false&key=" + API_KEY);
	        sb.append("&components=country:us");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;

	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }

	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}
}