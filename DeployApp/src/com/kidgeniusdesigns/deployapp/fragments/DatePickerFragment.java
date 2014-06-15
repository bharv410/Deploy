package com.kidgeniusdesigns.deployapp.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import com.kidgeniusdesigns.deployapp.CreateEvent;
import com.kidgeniusdesigns.realdeploy.R;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current date as the default date in the picker
final Calendar c = Calendar.getInstance();
int year = c.get(Calendar.YEAR);
int month = c.get(Calendar.MONTH);
int day = c.get(Calendar.DAY_OF_MONTH);

// Create a new instance of DatePickerDialog and return it
return new DatePickerDialog(getActivity(), this, year, month, day);
}

public void onDateSet(DatePicker view, int year, int month, int day) {
// Do something with the date chosen by the user
	CreateEvent.tilEvent.set(Calendar.YEAR,year);
	CreateEvent.tilEvent.set(Calendar.MONTH,month);
	CreateEvent.tilEvent.set(Calendar.DAY_OF_MONTH,day);
	Button db = (Button)getActivity().findViewById(R.id.dateButton);
	db.setText(month+"/"+day+"/"+year);
	
	
			
}
}