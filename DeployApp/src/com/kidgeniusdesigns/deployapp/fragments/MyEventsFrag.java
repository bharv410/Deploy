package com.kidgeniusdesigns.deployapp.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kidgeniusdesigns.realdeploy.R;

public class MyEventsFrag extends ListFragment
{
	
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(
                R.layout.attendee_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle saved)
    {
        super.onActivityCreated(saved);
        String[] comingSoon= new String[30];
        for(int i=0; i<comingSoon.length; i++){
        	comingSoon[i]="Coming Soon";
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.simplest_list_item,comingSoon);
        setListAdapter(adapter);
    }
}