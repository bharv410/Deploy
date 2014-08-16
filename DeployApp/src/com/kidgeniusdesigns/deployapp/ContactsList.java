package com.kidgeniusdesigns.deployapp;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kidgeniusdesigns.realdeploy.R;

public class ContactsList extends Fragment
{

    String message;
    MyCustomAdapter dataAdapter = null;
    public static ArrayList<Contact> ctcs;

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(
                R.layout.activity_contacts_list, container,
                false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle saved)
    {
        super.onActivityCreated(saved);
        ctcs = getContacts();
        // create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(getActivity()
                .getApplicationContext(),
                R.layout.rowbuttonlayout, ctcs);
        ListView listView = (ListView) getActivity()
                .findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    private ArrayList<Contact> getContacts()
    {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        Cursor phones = getActivity()
                .getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);
        while (phones.moveToNext())
        {
            try
            {
                String contctName = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contctNumb = getPhoneNumber(contctName,
                        getActivity().getApplicationContext());
                contacts.add(new Contact(contctName, contctNumb));
            }
            catch (SQLiteException e)
            {
            }
        }
        phones.close();
        contacts.add(new Contact("Dummy", ""));
        contacts.add(new Contact("Dummy2", ""));
        return contacts;
    }

    public String getPhoneNumber(String name, Context context)
    {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " like'%" + name + "%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor c = context
                .getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, selection, null, null);
        if (c.moveToFirst())
        {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "Unsaved";
        return ret;
    }

    private class MyCustomAdapter extends ArrayAdapter<Contact>
    {

        private ArrayList<Contact> contactList;

        public MyCustomAdapter(Context context,
                int textViewResourceId,
                ArrayList<Contact> contactList)
        {
            super(context, textViewResourceId, contactList);
            this.contactList = new ArrayList<Contact>();
            this.contactList.addAll(contactList);
        }

        private class ViewHolder
        {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView,
                ViewGroup parent)
        {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(
                        R.layout.rowbuttonlayout, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView
                        .findViewById(R.id.label);
                holder.name = (CheckBox) convertView
                        .findViewById(R.id.check);
                convertView.setTag(holder);

                holder.name
                        .setOnClickListener(new View.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                CheckBox cb = (CheckBox) v;
                                Contact contact = (Contact) cb
                                        .getTag();
                                
                                contact.setChecked(cb
                                        .isChecked());
                            }
                        });
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            Contact contact = (Contact) getItem(position);
            holder.code.setText(contact.getName());
            holder.name.setChecked(contact.isChecked());
            holder.name.setTag(contact);

            return convertView;

        }

    }
}