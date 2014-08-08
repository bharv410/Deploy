package com.kidgeniusdesigns.deployapp;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kidgeniusdesigns.realdeploy.R;
import com.kidgeniusdesigns.deployapp.fragments.Events;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class HomeScreen extends Activity
{
    private EditText eventCode;
    private MobileServiceClient mClient;
    private MobileServiceTable<Events> mToDoTable;
    private ProgressBar mProgressBar;
    private String enteredCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        eventCode = (EditText) findViewById(R.id.eventCode);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar.setVisibility(ProgressBar.GONE);
        try
        {
            mClient = new MobileServiceClient(
                    "https://droiddemo.azure-mobile.net/",
                    "uGrjosMeSdfQaUqCPEMSgKJhADIqFY34",
                    getApplicationContext())
            // use getApplicationContext() instead of
            // this reference. Garbage collection
            // will not be called when this reference
            // is used
                    .withFilter(new ProgressFilter());
            mToDoTable = mClient.getTable(Events.class);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

    }

    public void goToEvent(View v)
    {
        enteredCode = eventCode.getText().toString();
        eventCode.setText("");

        if (enteredCode != null && !enteredCode.equals(""))
        {
            // drops keyboard
            InputMethodManager inputManager = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    eventCode.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            findItem(enteredCode);

        }
        else
        {
            Toast toast = Toast.makeText(
                    getApplicationContext(), "Type Event Code",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, -150);
            toast.show();
        }

    }

    public void findItem(String eventCode)
    {

        mToDoTable.where().field("eventcode").eq(eventCode)
                .execute(new TableQueryCallback<Events>()
                {

                    public void onCompleted(
                            List<Events> result, int count,
                            Exception exception,
                            ServiceFilterResponse response)
                    {
                        if (exception == null)
                        {
                            if (result.size() < 1)
                            {
                                Toast toast = Toast
                                        .makeText(
                                                getApplicationContext(),
                                                "Invalid Event Id",
                                                Toast.LENGTH_LONG);
                                toast.setGravity(
                                        Gravity.TOP
                                                | Gravity.CENTER_HORIZONTAL,
                                        0, 0);
                                toast.show();
                            }
                            else
                            {
                                Intent i = new Intent(
                                        getApplicationContext(),
                                        EventHome.class);
                                Events cur;
                                for (Events res : result)
                                {
                                    cur = res;
                                    Calendar c = Calendar
                                            .getInstance();
                                    long millisToday = c
                                            .getTimeInMillis();
                                    long tilEvent = (long) (cur
                                            .getTime() - millisToday);
                                    // if event passed then exit
                                    if (tilEvent < 0)
                                    {
                                        // if event has passed then delete it
                                        mToDoTable
                                                .delete(cur,
                                                        new TableDeleteCallback()
                                                        {
                                                            public void onCompleted(
                                                                    Exception exception,
                                                                    ServiceFilterResponse response)
                                                            {
                                                                if (exception == null)
                                                                {
                                                                    Log.i("CHeck",
                                                                            "Object deleted");
                                                                    Toast toast = Toast
                                                                            .makeText(
                                                                                    getApplicationContext(),
                                                                                    "Invalid Event Id",
                                                                                    Toast.LENGTH_LONG);
                                                                    toast.setGravity(
                                                                            Gravity.TOP
                                                                                    | Gravity.CENTER_HORIZONTAL,
                                                                            0,
                                                                            0);
                                                                    toast.show();
                                                                }
                                                            }
                                                        });
                                    }
                                    else
                                    {

                                        i.putExtra("title",
                                                cur.getTitle());
                                        i.putExtra(
                                                "location",
                                                cur.getLocation());
                                        i.putExtra("eventtime",
                                                cur.getTime());
                                        i.putExtra("code", cur
                                                .getEventCode());
                                        i.putExtra(
                                                "creator",
                                                cur.getOwnerId());
                                        i.putExtra(
                                                "descrip",
                                                cur.getDescrip());
                                        i.putExtra(
                                                "username",
                                                getIntent()
                                                        .getStringExtra(
                                                                "username"));
                                        startActivity(i);
                                    }
                                }
                            }
                        }
                        else
                        {
                            System.out
                                    .println("Error finding item");
                        }
                    }
                });
    }

    public void createEvent(View v)
    {
        Intent i = new Intent(getApplicationContext(),
                CreateEvent.class);
        i.putExtra("username",
                getIntent().getStringExtra("username"));
        startActivity(i);

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

    public void eventDetails(View v)
    {
        enteredCode = eventCode.getText().toString();
        if (enteredCode.equals(""))
        {
            displayPopUp();
            return;
        }
        else
        {
            eventCode.setText("");
            editItem(enteredCode);
        }
    }

    public void editItem(String eventCode)
    {

        mToDoTable.where().field("eventcode").eq(eventCode)
                .execute(new TableQueryCallback<Events>()
                {

                    public void onCompleted(
                            List<Events> result, int count,
                            Exception exception,
                            ServiceFilterResponse response)
                    {
                        if (exception == null)
                        {
                            if (result.size() < 1)
                            {
                                displayPopUp();
                            }
                            else
                            {
                                Intent i = new Intent(
                                        getApplicationContext(),
                                        CreatorDetailsActivity.class);
                                Events cur;
                                for (Events res : result)
                                {
                                    if (!res.getOwnerId()
                                            .equals(MainActivity.username))
                                    {
                                        displayPopUp();
                                        return;
                                    }
                                    cur = res;
                                    i.putExtra("title",
                                            cur.getTitle());
                                    i.putExtra("location",
                                            cur.getLocation());
                                    i.putExtra("eventtime",
                                            cur.getTime());
                                    i.putExtra("code",
                                            cur.getEventCode());
                                    i.putExtra("creator",
                                            cur.getOwnerId());
                                    i.putExtra("descrip",
                                            cur.getDescrip());
                                    i.putExtra(
                                            "username",
                                            getIntent()
                                                    .getStringExtra(
                                                            "username"));
                                    startActivity(i);
                                }
                            }
                        }
                        else
                        {
                            System.out
                                    .println("Error finding item");
                        }
                    }
                });
    }

    public void displayPopUp()
    {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(
                HomeScreen.this);

        // 2. Chain together various setter methods to set the dialog
        // characteristics
        builder.setMessage(
                "Enter event code of event that you created  so you can edit, send invites, etc")
                .setTitle("Event Creator button");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}