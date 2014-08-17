package com.kidgeniusdesigns.deployapp.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kidgeniusdesigns.realdeploy.R;

public class DeploySpecialsFrag  extends ListFragment
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
        String[] comingSoon= new String[4];

        	comingSoon[0]="Security";
        	comingSoon[1]="Bartenders (21+)";
        	comingSoon[2]="Models";
        	comingSoon[3]="Drivers (Uber)";
        	
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.simplest_list_item,comingSoon);
        setListAdapter(adapter);
        
        TextView tv = (TextView)getActivity().findViewById(R.id.listHeaderText);
        tv.setText("Request:");
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "Coming soon!!", Toast.LENGTH_SHORT).show();
    }
}