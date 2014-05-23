package com.kidgeniusdesigns.deployapp.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kidgeniusdesigns.deployapp.EventHome;
import com.kidgeniusdesigns.deployapp.R;
 
public class EventInfo extends Fragment {
	static TextView partyCountdown, titleView, descrView, dateView, creatorView, startTimeView;
	public static Button atButton;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_event_info, container, false);
        return rootView;
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	partyCountdown = (TextView)getActivity().findViewById(R.id.durationTextView);
    	countdown(EventHome.cd);
    	titleView = (TextView)getActivity().findViewById(R.id.titleView);
    	titleView.setText(EventHome.title);
    	dateView=(TextView)getActivity().findViewById(R.id.realDateView);
    	dateView.setText(getDate(EventHome.cd,"EEE, MMM d, ''yy"));
    	creatorView=(TextView)getActivity().findViewById(R.id.partyCreator);
    	creatorView.setText("By:" +EventHome.creator);
    	atButton = (Button)getActivity().findViewById(R.id.attendingButton);
    	descrView=(TextView)getActivity().findViewById(R.id.descrip);
    	descrView.setText(EventHome.description);
        super.onActivityCreated(savedInstanceState);
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date. 
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(milliSeconds);
         return formatter.format(calendar.getTime());
    }
    
    public static void countdown(long seconds) {
        new CountDownTimer(seconds * 1000, 1000) {
        	
                public void onTick(long millisUntilFinished) {
                	Calendar c = Calendar.getInstance(); 
                	long millisToday=c.getTimeInMillis();
                	long millisEvent=EventHome.cd;
                	long tilEvent=millisEvent-millisToday;
                	long daysLeft=TimeUnit.MILLISECONDS.toDays(tilEvent);   
                	long hoursLeft=TimeUnit.MILLISECONDS.toHours(tilEvent)-TimeUnit.DAYS.toHours(daysLeft);
                	partyCountdown.setText(daysLeft+ "days "+ hoursLeft+"hrs");
                                }
                
                public void onFinish() {
                        partyCountdown.setText("Party is over");
                }
        }.start();

}
}