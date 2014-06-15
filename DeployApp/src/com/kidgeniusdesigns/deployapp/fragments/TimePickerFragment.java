package com.kidgeniusdesigns.deployapp.fragments;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import com.kidgeniusdesigns.deployapp.CreateEvent;
import com.kidgeniusdesigns.realdeploy.R;

public class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current time as the default values for the picker
final Calendar c = Calendar.getInstance();
int hour = c.get(Calendar.HOUR_OF_DAY);
int minute = c.get(Calendar.MINUTE);

// Create a new instance of TimePickerDialog and return it
return new TimePickerDialog(getActivity(), this, hour, minute,
DateFormat.is24HourFormat(getActivity()));
}

public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
// Do something with the time chosen by the user
	CreateEvent.tilEvent.set(Calendar.HOUR_OF_DAY, hourOfDay);
	CreateEvent.tilEvent.set(Calendar.MINUTE, minute);
	Button db = (Button)getActivity().findViewById(R.id.timeButton);
	db.setText(hourOfDay+":"+minute);

}
}