package com.kidgeniusdesigns.deployapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.kidgeniusdesigns.realdeploy.R;

public class EditEventFrag extends Fragment{
	EditText editTitle, locationView, code, editDescrip;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.edit_event_frag, container, false);
        return rootView;
    }
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) {
	  super.onActivityCreated(savedInstanceState);
	  editTitle=(EditText)getActivity().findViewById(R.id.editEventTitle);
	    editTitle.setText(CreatorDetailsActivity.title);
	    locationView=(EditText)getActivity().findViewById(R.id.locationTextView);
	    locationView.setText(CreatorDetailsActivity.eventLocation);
	    code=(EditText)getActivity().findViewById(R.id.editEventCode);
	    code.setText(CreatorDetailsActivity.eventCode);
	    editDescrip=(EditText)getActivity().findViewById(R.id.editDescriptBox);
	    editDescrip.setText(EventHome.description);
	    }
	
}
