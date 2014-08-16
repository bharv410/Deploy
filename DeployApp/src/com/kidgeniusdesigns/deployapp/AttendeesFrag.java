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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.simplest_list_item,
                CreatorDetailsActivity.attendeesList);
        setListAdapter(adapter);
        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                	String name=adapter.getItem(position);
                                	adapter.remove(name);
                                	((CreatorDetailsActivity)getActivity()).blockFromList(name);
                                }
                                
                                adapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onListItemClick(ListView l, View v,
            int position, long id)
    {

    	String name=CreatorDetailsActivity.attendeesList.get(position);
    	Toast.makeText(getActivity(), "Swipe to delete "+name, Toast.LENGTH_SHORT).show();
    	//((CreatorDetailsActivity)getActivity()).blockFromList(name);
}
}