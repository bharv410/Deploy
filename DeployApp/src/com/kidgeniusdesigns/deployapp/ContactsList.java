package com.kidgeniusdesigns.deployapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class ContactsList extends ListActivity {

	ArrayList<String> contactNames;
	ArrayList<String> contactNums;
	String eventCode,message;
	EditText messageBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list);
		setTitle("Invite Some People");
		Intent intent = getIntent();
		eventCode=intent.getStringExtra("eventcode");
		
		messageBox=(EditText)findViewById(R.id.messageEditText);
		message="I'm having a private party! Download 'Deploy' from Play Store. When it opens type '"
				+ eventCode +"' for the info!";
		messageBox.setText(message);
		
		contactNames = new ArrayList<String>();	
		contactNums = new ArrayList<String>();
		getContacts();
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        R.layout.simplest_list_item, contactNames);
		    setListAdapter(adapter);
		    
		    Toast.makeText(getApplicationContext(), "Created Event now tell some ppl", Toast.LENGTH_LONG).show();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String phoneNum=contactNums.get(position);
		String nam=contactNames.get(position);
		message=messageBox.getText().toString();
		Toast.makeText(getApplicationContext(), "Sent to: " + nam, Toast.LENGTH_SHORT).show();
		//SmsManager smsManager = SmsManager.getDefault();
		//smsManager.sendTextMessage(phoneNo, null, message, null, null);	
		
	}
	
	private void getContacts() {
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
			String contctName=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			contactNames.add(contctName);
			try{
			contactNums.add(getPhoneNumber(contctName,getApplicationContext()));
			}catch(SQLiteException e){
				e.printStackTrace();
			}
		}
		phones.close();
	  }

	public void goHome(View v){
		startActivity(new Intent(this, HomeScreen.class));
	}
	public String getPhoneNumber(String name, Context context) {
		String ret = null;
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
		Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		        projection, selection, null, null);
		if (c.moveToFirst()) {
		    ret = c.getString(0);
		}
		c.close();
		if(ret==null)
		    ret = "Unsaved";
		return ret;
		}

}