package com.kidgeniusdesigns.deployapp;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.kidgeniusdesigns.realdeploy.R;

public class AttendeesFrag extends ListFragment
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.simplest_list_item,
                CreatorDetailsActivity.attendeesList);
        setListAdapter(adapter);
        Toast.makeText(getActivity(), "Loading contacts.", Toast.LENGTH_SHORT).show();;
        Toast.makeText(getActivity(), "Loading contacts..", Toast.LENGTH_SHORT).show();;
        Toast.makeText(getActivity(), "Loading contacts...", Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void onListItemClick(ListView l, View v,
            int position, long id)
    {

        Toast.makeText(
                getActivity(),"Soon when you swipe "+
                CreatorDetailsActivity.attendeesList
                        .get(position)+" won't be able to ever see the details again", Toast.LENGTH_LONG)
                .show();

    }
}
