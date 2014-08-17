package com.kidgeniusdesigns.deployapp;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kidgeniusdesigns.realdeploy.R;
import com.kidgeniusdesigns.realdeploy.helpers.StorageService;
import com.kidgeniusdesigns.realdeploy.model.BlockedMembers;
import com.kidgeniusdesigns.realdeploy.model.Events;
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
    private MobileServiceTable<EventsToImages> mEventsToImagesTable;
    ProgressDialog barProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_screen);
        
//        MainActivity.timesOnHomeScreen++;
//        if(MainActivity.timesOnHomeScreen>1 && MainActivity.timesOnHomeScreen<4){
//        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//    				HomeScreen.this);
//     
//    			// set title
//    			alertDialogBuilder.setTitle("Congrats on create an event!");
//     
//    			// set dialog message
//    			alertDialogBuilder
//    				.setMessage("-Edit details\n-Invite contacts\n-See who viewed\n Just type '"+ getIntent().getStringExtra("eventcode") +"'\nand click the green settings gear")
//    				.setCancelable(false)
//    				.setPositiveButton("Got it!",new DialogInterface.OnClickListener() {
//    					public void onClick(DialogInterface dialog,int id) {
//    						dialog.cancel();
//    					}
//    				  });
//    				// create alert dialog
//    				AlertDialog alertDialog = alertDialogBuilder.create();
//     
//    				// show it
//    				alertDialog.show();
//        }
        eventCode = (EditText) findViewById(R.id.eventCode);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar.setVisibility(ProgressBar.GONE);
        try
        {
            mClient = new MobileServiceClient(
                    "https://droiddemo.azure-mobile.net/",
                    "uGrjosMeSdfQaUqCPEMSgKJhADIqFY34", this)
                    .withFilter(new ProgressFilter());
            mToDoTable = mClient.getTable(Events.class);
            StorageService mStorageService = new StorageService(
                    getApplicationContext());
            MobileServiceClient mImagesClient = mStorageService
                    .getMobileServiceClient();
            mEventsToImagesTable = mImagesClient
                    .getTable(EventsToImages.class);
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
            
            mClient.getTable(BlockedMembers.class).where().field("blockedname").eq(getIntent()
                    .getStringExtra(
                            "username"))
            .execute(new TableQueryCallback<BlockedMembers>()
            {
                public void onCompleted(
                        List<BlockedMembers> result, int count,
                        Exception exception,
                        ServiceFilterResponse response)
                {
                    if (exception == null && result.size()>0)
                    {//means that the user has been blocked. check
                    	for(BlockedMembers e: result){
                    		if(e.getEventCode().contains(enteredCode)){
                    			//user was blocked from the event
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
                    		}else{
                    			findItem(enteredCode);
                    		}
                    	}
                    	
                    }else{
                    	//not blocked so show the event
                    	findItem(enteredCode);
                    }
                }
            }
            );
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

    public void findItem(final String eventCode)
    {
        if(eventCode.equals("clearupoldevents"))
        {
            clearUpOldEvents();
        }
        else
        {
            findEventCode(eventCode);
        }
    }
    
    public void clearUpOldEvents()
    {
        mToDoTable.execute(new TableQueryCallback<Events>()
                {

                    @Override
                    public void onCompleted(
                            List<Events> result, int count,
                            Exception exception,
                            ServiceFilterResponse response)
                    {
                        if(exception == null)
                        {
                            Events cur;
                            for(Events res : result)
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
                                    mToDoTable.delete(cur, new TableDeleteCallback()
                                    {

                                        @Override
                                        public void onCompleted(
                                                Exception exception,
                                                ServiceFilterResponse response)
                                        {
                                            if(exception == null)
                                            {
                                                Log.i("CHeck",
                                                        "Object deleted");
                                            }
                                            
                                        }
                                        
                                    });
                                    
                                    mEventsToImagesTable
                                    .where()
                                    .field("eventCode")
                                    .eq(cur.getEventCode())
                                    .execute(
                                            new TableQueryCallback<EventsToImages>()
                                            {
                                                @Override
                                                public void onCompleted(
                                                        List<EventsToImages> result,
                                                        int count,
                                                        Exception exception,
                                                        ServiceFilterResponse response)
                                                {
                                                    if (exception == null)
                                                    {
                                                        for (EventsToImages temp : result)
                                                        {
                                                            mEventsToImagesTable
                                                                    .delete(temp,
                                                                            new TableDeleteCallback()
                                                                            {
                                                                                @Override
                                                                                public void onCompleted(
                                                                                        Exception exception,
                                                                                        ServiceFilterResponse response)
                                                                                {
                                                                                    if(exception == null)
                                                                                    {
                                                                                        Log.i("CHeck", "eventToImages deleted");
                                                                                    }
                                                                                }
                                                                            });
                                                            
                                                            String imageName = temp.getImageName();
                                                            
                                                            if(imageName != null && !imageName.equals("deployicon"))
                                                            {
                                                            
                                                                StorageService mStorageService = new StorageService(
                                                                        getApplicationContext());
                                                                
                                                                mStorageService.deleteBlob("deployimages", temp.getImageName());
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                        
                    }
            
                });
    }
    
    public void findEventCode(final String eventCode)
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
                        final Intent i = new Intent(
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
                                mEventsToImagesTable
                                        .where()
                                        .field("eventCode")
                                        .eq(eventCode)
                                        .execute(
                                                new TableQueryCallback<EventsToImages>()
                                                {
                                                    @Override
                                                    public void onCompleted(
                                                            List<EventsToImages> result,
                                                            int count,
                                                            Exception exception,
                                                            ServiceFilterResponse response)
                                                    {
                                                        if (exception == null)
                                                        {
                                                            for (EventsToImages temp : result)
                                                            {
                                                                mEventsToImagesTable
                                                                        .delete(temp,
                                                                                new TableDeleteCallback()
                                                                                {
                                                                                    @Override
                                                                                    public void onCompleted(
                                                                                            Exception exception,
                                                                                            ServiceFilterResponse response)
                                                                                    {
                                                                                        if(exception == null)
                                                                                        {
                                                                                            Log.i("CHeck", "eventToImages deleted");
                                                                                        }
                                                                                    }
                                                                                });
                                                                
                                                                String imageName = temp.getImageName();
                                                                
                                                                if(imageName != null && !imageName.equals("deployicon"))
                                                                {
                                                                
                                                                    StorageService mStorageService = new StorageService(
                                                                            getApplicationContext());
                                                                    
                                                                    mStorageService.deleteBlob("deployimages", temp.getImageName());
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
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
                                mEventsToImagesTable
                                        .where()
                                        .field("eventcode")
                                        .eq(eventCode)
                                        .execute(
                                                new TableQueryCallback<EventsToImages>()
                                                {
                                                    @Override
                                                    public void onCompleted(
                                                            List<EventsToImages> result,
                                                            int count,
                                                            Exception exception,
                                                            ServiceFilterResponse response)
                                                    {
                                                        //if no error AND IF THEIR IS RLY A PIC AVAILABLE
                                                        if (exception == null && result.size()>0)
                                                        {
                                                            i.putExtra(
                                                                    "imagename",
                                                                    result.get(
                                                                            0)
                                                                            .getImageName());
                                                            startActivity(i);
                                                        }
                                                        else
                                                        {//else proceed without a pic
                                                            startActivity(i);
                                                        }
                                                    }
                                                });
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
                "Enter event code of event that you created so you can edit, send invites, etc")
                .setTitle("Event Creator button");
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
            if (intentAction.equals("blob.deleted"))
            {
                Log.i("CHeck", "blob image deleted");
            }
        }
    };
    
    @Override
    public void onResume()
    {
        super.onResume(); // Always call the superclass method first

        IntentFilter filter = new IntentFilter();
        filter.addAction("blob.deleted");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }
}