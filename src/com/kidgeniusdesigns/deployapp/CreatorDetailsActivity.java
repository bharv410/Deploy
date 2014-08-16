package com.kidgeniusdesigns.deployapp;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.kidgeniusdesigns.deployapp.fragments.Attendee;
import com.kidgeniusdesigns.deployapp.fragments.BlockedMembers;
import com.kidgeniusdesigns.deployapp.fragments.CreatorTabsPagerAdapter;
import com.kidgeniusdesigns.deployapp.fragments.Events;
import com.kidgeniusdesigns.realdeploy.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class CreatorDetailsActivity extends FragmentActivity
{
    // Tab titles
    CreatorTabsPagerAdapter pageAdapter;
    public static FragmentManager fragmentManager;
    public static String countdown, title, creator,
            eventLocation, eventCode, description;
    public static long cd;
    private MobileServiceClient mClient;
    private MobileServiceTable<Attendee> mAttendeeTable;
    private MobileServiceTable<Events> mDetailsTable;
    public static GeoPoint eventLatLng;
    public static List<String> attendeesList;
    EditText editTitle, locationView, code, editDescrip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        setContentView(R.layout.activity_event_home);
        fragmentManager = getSupportFragmentManager();
        pageAdapter = new CreatorTabsPagerAdapter(
                getSupportFragmentManager());
        final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        attendeesList = new ArrayList<String>();
        attendeesList.add("Some blocked users may still appear on list. Don't worry. If you blocked em then they can't see");
        attendeesList.add("Mr. Miyogi");
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        creator = intent.getStringExtra("creator");
        eventCode = intent.getStringExtra("code");
        eventLocation = intent.getStringExtra("location");
        description = intent.getStringExtra("descrip");
        // Toast.makeText(getApplicationContext(),
        // intent.getStringExtra("latOfEvent"), Toast.LENGTH_LONG).show();
        double millisTil = intent.getDoubleExtra("eventtime",
                0.0);
        // Specify that tabs should be displayed in the action bar.
        actionBar
                .setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener()
        {
            public void onTabSelected(ActionBar.Tab tab,
                    FragmentTransaction ft)
            {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab,
                    FragmentTransaction ft)
            {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab,
                    FragmentTransaction ft)
            {
                // probably ignore this event
            }
        };
        // Add tabs, specifying the tab's text and TabListener
        actionBar.addTab(actionBar.newTab()
                .setText("Event Details")
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText("Attendees")
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText("Invite Friends")
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText("My Events")
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText("Deploy Specials")
                .setTabListener(tabListener));

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                // When swiping between pages, select the
                // corresponding tab.
                getActionBar().setSelectedNavigationItem(
                        position);
            }
        });

        try
        {
        	
            mClient = new MobileServiceClient(
                    "https://droiddemo.azure-mobile.net/",
                    "uGrjosMeSdfQaUqCPEMSgKJhADIqFY34", this);
            mAttendeeTable = mClient.getTable(Attendee.class);
            mDetailsTable = mClient.getTable(Events.class);
            mAttendeeTable.where().field("eventcode")
                    .eq(eventCode)
                    .execute(new TableQueryCallback<Attendee>()
                    {
                        public void onCompleted(
                                List<Attendee> result,
                                int count, Exception exception,
                                ServiceFilterResponse response)
                        {
                            if (exception == null)
                            {
                                for (Attendee item : result)
                                {
                                    // protect from duplicates
                                    if (!attendeesList.contains(item
                                            .getAttendee()))
                                    {
                                        attendeesList.add(item
                                                .getAttendee());
                                    }
                                }
                            }
                        }
                    });
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public void checkButtonClick(View v)
    {

                StringBuffer responseText = new StringBuffer();
                responseText.append("Sent to:\n");

                ArrayList<Contact> contactList = ContactsList.ctcs;
                for (int i = 0; i < contactList.size(); i++)
                {
                    Contact contact = contactList.get(i);
                    if (contact.isChecked())
                    {
                    	sendSMS(contact.getNum(),"Download the Android app \"Deploy\" and enter event code '"+eventCode+"' to get the private details about "+creator+"s secret event");
                        responseText.append("\n"
                                + contact.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
    private void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
    public void saveEvent(View v)
    {
        editTitle = (EditText) findViewById(R.id.editEventTitle);
        final String newTitle = editTitle.getText().toString();
        locationView = (EditText) findViewById(R.id.locationTextView);
        final String newLoc = locationView.getText().toString();
        code = (EditText) findViewById(R.id.editEventCode);
        final String newCode = code.getText().toString();
        editDescrip = (EditText) findViewById(R.id.editDescriptBox);
        final String newDescrip = editDescrip.getText()
                .toString();
        Events curEvent;
        mDetailsTable.where().field("eventcode").eq(eventCode)
                .execute(new TableQueryCallback<Events>()
                {
                    public void onCompleted(
                            List<Events> result, int count,
                            Exception exception,
                            ServiceFilterResponse response)
                    {
                        if (exception == null)
                        {
                            for (Events item : result)
                            {
                                item.setTitle(newTitle);
                                item.setLocation(newLoc);
                                item.setEventCode(newCode);
                                item.setDescrip(newDescrip);
                                mDetailsTable
                                        .update(item,
                                                new TableOperationCallback<Events>()
                                                {
                                                    public void onCompleted(
                                                            Events entity,
                                                            Exception exception,
                                                            ServiceFilterResponse response)
                                                    {
                                                        if (exception == null)
                                                        {
                                                            Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "Saved changes",
                                                                    Toast.LENGTH_LONG)
                                                                    .show();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "error saving changes",
                                                                    Toast.LENGTH_LONG)
                                                                    .show();
                                                        }
                                                    }
                                                });
                            }
                        }
                    }
                });

    }
    
    public void blockFromList(String username){
    	String currentEventCode=eventCode;
    	
    	String[] getFirstandLastName = username.split("\\s+");
    	final String blockThisPerson=getFirstandLastName[0]+" "+getFirstandLastName[1];
    	
    	BlockedMembers blockHim = new BlockedMembers(blockThisPerson,currentEventCode);
    	mClient.getTable(BlockedMembers.class).insert(blockHim, new TableOperationCallback<BlockedMembers>() {
    	      public void onCompleted(BlockedMembers entity, Exception exception, ServiceFilterResponse response) {
    	            if (exception == null) {
    	                  Toast.makeText(getApplicationContext(), "Blocked " +blockThisPerson +" from using event code "+ eventCode, Toast.LENGTH_LONG).show();
    	            } else {
    	                  // Insert failed
    	            }
    	      }
    	});
    }
}