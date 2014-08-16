package com.kidgeniusdesigns.deployapp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coreform.open.android.formidablevalidation.ValidationManager;
import com.google.gson.JsonObject;
import com.kidgeniusdesigns.deployapp.fragments.DatePickerFragment;
import com.kidgeniusdesigns.deployapp.fragments.Events;
import com.kidgeniusdesigns.deployapp.fragments.TimePickerFragment;
import com.kidgeniusdesigns.realdeploy.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class CreateEvent extends FragmentActivity implements
        OnItemClickListener
{
    EditText eventTitle, eventCode, descripBox;
    ImageView eventPhoto;
    AutoCompleteTextView locationBox;

    public static Calendar tilEvent;
    private MobileServiceClient mClient;
    private MobileServiceTable<Events> mToDoTable;
    private StorageService mStorageService;
    private ProgressBar mProgressBar;

    private String imageURI;
    private String imageName;
    String partyTime;
    ValidationManager mValidationManager;
    String[] titleHints, codeHints, locHints, descripHints;

    private static final String LOG_TAG = "GooglePlaces";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCgP3QVf6vpoGqZJxlMY84RnYRo_BZ8JbI";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_event);

        CreateEvent.tilEvent = Calendar.getInstance();
        titleHints = new String[5];
        codeHints = new String[5];
        locHints = new String[5];
        descripHints = new String[5];
        titleHints[0] = "Ben's Album Release";
        titleHints[1] = "Exclusive Diner";
        titleHints[2] = "Meet-and-Greet";
        titleHints[3] = "Meet me at Wawas";
        codeHints[0] = "example: bensalbrel213";
        codeHints[1] = "example: jaysdiner22";
        codeHints[2] = "example: secretgreet115";
        codeHints[3] = "example: wawas323";
        locHints[0] = "123 Motown Street Detrot, MI";
        locHints[1] = "555 Jay Diner Way Dayton, OH";
        locHints[2] = "5600 Starbuck lane, Los Angeles CA";
        locHints[3] = "Wawas Hickory Ridge Road Columbia, MD";
        descripHints[0] = "Come hear a sneak preview of my new album. Don't tell anyone the code bc they won't get in. I can see whoever views the details";
        descripHints[1] = "Thanks for attending our conference. You've been invited to our post-event dinner. Follow Deploy's instructions to make your way to our secret location.";
        descripHints[2] = "You are one of the few that hold the event code to meet with our surprise music artist who has agreed to sign some autographs.";
        descripHints[3] = "Whats up bud. Could you drop the kids off at wawas. Easily get directions or add to your calendar through this app.";

        //hide keyboard at first
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        
        eventTitle = (EditText) findViewById(R.id.eventTitle);

        eventCode = (EditText) findViewById(R.id.eventCode);

        eventPhoto = (ImageView) findViewById(R.id.eventPhotoImage);

        imageURI = getIntent().getStringExtra("imageURI");

        if (imageURI != null)
        {
            eventPhoto.setImageURI(Uri.parse(imageURI));
        }

        locationBox = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

        descripBox = (EditText) findViewById(R.id.descriptBox);

        locationBox.setAdapter(new PlacesAutoCompleteAdapter(
                this, R.layout.list_item));
        locationBox.setOnItemClickListener(this);
        eventTitle.requestFocus();
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);

        try
        {
            mClient = new MobileServiceClient(
                    "https://droiddemo.azure-mobile.net/",
                    "uGrjosMeSdfQaUqCPEMSgKJhADIqFY34", this)
                    .withFilter(new ProgressFilter());

            mToDoTable = mClient.getTable(Events.class);

            mStorageService = new StorageApplication(
                    getApplicationContext())
                    .getStorageService();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // System.out.print("Coudnt get table");
        }

//        mValidationManager = new ValidationManager(this);
//        mValidationManager.add("eventCodeError",
//                new RegExpressionValueValidator(eventCode,
//                        "^[a-zA-Z0-9\\-'\\s]{3,}$",
//                        "please enter event code."));
//        mValidationManager.add("eventTitleError",
//                new RegExpressionValueValidator(eventTitle,
//                        "^[a-zA-Z0-9\\-'\\s]{3,}$",
//                        "please enter event title."));
    }

    public class Item
    {
        public String Id;
        public String Text;
    }

    public void onItemClick(AdapterView<?> adapterView,
            View view, int position, long id)
    {
        String str = (String) adapterView
                .getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void choosePhoto(View v)
    {
        Intent intent = new Intent(getApplicationContext(),
                ChoosePhoto.class);
        intent.putExtra("username",
                getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v)
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void saveEvent(View v)
    {
//        if (mValidationManager.validateAllAndSetError())
//        {
            uploadImage();
        //}
    }

    public void uploadImage()
    {
        if (imageURI != null)
        {
            Calendar cal = Calendar.getInstance();
            
            imageName = getIntent().getStringExtra("username") + imageURI +
                    cal.getTimeInMillis();
            
            imageName = imageName.replaceAll("[^A-Za-z0-9]", "");
            
            mStorageService.getSasForNewBlob("deployimages",
                    imageName);
        }
        else
        {
            MobileServiceClient mImagesClient = mStorageService
                    .getMobileServiceClient();

            MobileServiceTable<EventsToImages> mEventsToImagesTable = mImagesClient
                    .getTable(EventsToImages.class);

            EventsToImages item = new EventsToImages();

            String code = eventCode.getText().toString();

            item.setEventCode(code);
            item.setImageName("deployicon");

            mEventsToImagesTable
                    .insert(item,
                            new TableOperationCallback<EventsToImages>()
                            {

                                public void onCompleted(
                                        EventsToImages entity,
                                        Exception exception,
                                        ServiceFilterResponse response)
                                {
                                    if (exception == null)
                                    {
                                        addItem();
                                    }
                                    else
                                    {

                                    }
                                }
                            });
        }
    }

    public void addItem()
    {
        String eventLocation = locationBox.getText().toString();

        Date calTime = tilEvent.getTime();
        String title = eventTitle.getText().toString();
        String code = eventCode.getText().toString();

        Events item = new Events();

        item.setTitle(title);
        item.setEventCode(code);
        Toast.makeText(getApplicationContext(), code,
                Toast.LENGTH_LONG).show();
        item.setComplete(false);
        item.setOwnerId(getIntent().getStringExtra("username"));
        item.setLocation(eventLocation);
        item.setTime(calTime.getTime());

        if (!descripBox.getText().toString().equals(""))
            item.setDescrip(descripBox.getText().toString());

        if (mToDoTable != null)
        {

            mToDoTable.insert(item,
                    new TableOperationCallback<Events>()
                    {

                        public void onCompleted(Events entity,
                                Exception exception,
                                ServiceFilterResponse response)
                        {
                            if (exception == null)
                            {
                                Intent i = new Intent(
                                        getApplicationContext(),
                                        HomeScreen.class);
                                i.putExtra("eventcode",
                                        entity.getEventCode());
                                startActivity(i);
                            }
                            else
                            {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "error saving",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }

        // alarm to notify of creator options
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateEvent.this);

        // 2. Chain together various setter methods to set the dialog
        // characteristics
        builder.setMessage(
                "To edit details \n View Attendees \n Invite Friends")
                .setTitle(
                        "Click the green gear on the home screen");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***
     * Broadcast receiver handles blobs being loaded or a new blob being created
     */
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        public void onReceive(Context context,
                android.content.Intent intent)
        {
            String intentAction = intent.getAction();
            if (intentAction.equals("blob.created"))
            {
                // If a blob has been created, upload the image
                JsonObject blob = mStorageService
                        .getLoadedBlob();
                String sasUrl = blob.getAsJsonPrimitive(
                        "sasUrl").toString();
                (new ImageUploaderTask(sasUrl)).execute(Uri
                        .parse(imageURI));
            }
        }
    };

    /***
     * Handles uploading an image to a specified url
     */
    class ImageUploaderTask extends
            AsyncTask<Uri, Void, Boolean>
    {
        private String mUrl;

        public ImageUploaderTask(String url)
        {
            mUrl = url;
        }

        @Override
        protected Boolean doInBackground(Uri... params)
        {
            try
            {
                InputStream inputStream = getContentResolver()
                        .openInputStream(params[0]);
                int bytesRead = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                while ((bytesRead = inputStream.read(b)) != -1)
                {
                    bos.write(b, 0, bytesRead);
                }
                byte[] bytes = bos.toByteArray();
                // Post our image data (byte array) to the server
                URL url = new URL(mUrl.replace("\"", ""));
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty(
                        "Content-Length", "" + bytes.length);
                // Write image data to server
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.write(bytes);
                wr.flush();
                wr.close();
                int response = urlConnection.getResponseCode();
                // If we successfully uploaded, return true
                if (response == 201
                        && urlConnection.getResponseMessage()
                                .equals("Created"))
                {
                    return true;
                }
            }
            catch (Exception ex)
            {
                Log.e("kidgeniustesting", ex.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean uploaded)
        {
            if (uploaded)
            {
                MobileServiceClient mImagesClient = mStorageService
                        .getMobileServiceClient();

                MobileServiceTable<EventsToImages> mEventsToImagesTable = mImagesClient
                        .getTable(EventsToImages.class);

                EventsToImages item = new EventsToImages();

                String code = eventCode.getText().toString();

                item.setEventCode(code);
                item.setImageName(imageName);

                mEventsToImagesTable
                        .insert(item,
                                new TableOperationCallback<EventsToImages>()
                                {

                                    public void onCompleted(
                                            EventsToImages entity,
                                            Exception exception,
                                            ServiceFilterResponse response)
                                    {
                                        if (exception == null)
                                        {
                                            addItem();
                                        }
                                        else
                                        {

                                        }
                                    }
                                });
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume(); // Always call the superclass method first
        Random rg = new Random();
        int rand = rg.nextInt(4);
        System.out.println(String.valueOf(rand));
        System.out.println(String.valueOf(rand));
        System.out.println(String.valueOf(rand));
        System.out.println(String.valueOf(rand));
        System.out.println(String.valueOf(rand));
        System.out.println(String.valueOf(rand));
        eventTitle.setHint(titleHints[rand]);
        eventCode.setHint(codeHints[rand]);
        locationBox.setHint(locHints[rand]);
        descripBox.setHint(descripHints[rand]);

        IntentFilter filter = new IntentFilter();
        filter.addAction("blob.created");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private class ProgressFilter implements ServiceFilter
    {

        @Override
        public void handleRequest(
                ServiceFilterRequest request,
                NextServiceFilterCallback nextServiceFilterCallback,
                final ServiceFilterResponseCallback responseCallback)
        {
            runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {
                    if (mProgressBar != null)
                        mProgressBar
                                .setVisibility(ProgressBar.VISIBLE);
                }
            });

            nextServiceFilterCallback.onNext(request,
                    new ServiceFilterResponseCallback()
                    {

                        @Override
                        public void onResponse(
                                ServiceFilterResponse response,
                                Exception exception)
                        {
                            runOnUiThread(new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    if (mProgressBar != null)
                                        mProgressBar
                                                .setVisibility(ProgressBar.GONE);
                                }
                            });

                            if (responseCallback != null)
                                responseCallback.onResponse(
                                        response, exception);
                        }
                    });
        }
    }

    private ArrayList<String> autocomplete(String input)
    {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try
        {
            StringBuilder sb = new StringBuilder(
                    PLACES_API_BASE + TYPE_AUTOCOMPLETE
                            + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input="
                    + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(
                    conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1)
            {
                jsonResults.append(buff, 0, read);
            }
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }

        try
        {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(
                    jsonResults.toString());
            JSONArray predsJsonArray = jsonObj
                    .getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(
                    predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++)
            {
                resultList.add(predsJsonArray.getJSONObject(i)
                        .getString("description"));
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private class PlacesAutoCompleteAdapter extends
            ArrayAdapter<String> implements Filterable
    {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context,
                int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount()
        {
            return resultList.size();
        }

        @Override
        public String getItem(int index)
        {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter()
        {
            Filter filter = new Filter()
            {
                @Override
                protected FilterResults performFiltering(
                        CharSequence constraint)
                {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null)
                    {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint
                                .toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(
                        CharSequence constraint,
                        FilterResults results)
                {
                    if (results != null && results.count > 0)
                    {
                        notifyDataSetChanged();
                    }
                    else
                    {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
