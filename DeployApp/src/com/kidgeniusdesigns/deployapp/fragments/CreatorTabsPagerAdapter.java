package com.kidgeniusdesigns.deployapp.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kidgeniusdesigns.deployapp.AttendeesFrag;
import com.kidgeniusdesigns.deployapp.ContactsList;
import com.kidgeniusdesigns.deployapp.EditEventFrag;

public class CreatorTabsPagerAdapter extends
        FragmentPagerAdapter
{

    public CreatorTabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {

        switch (index)
        {
            case 0:
                return new EditEventFrag();
            case 1:
                return (Fragment) new AttendeesFrag();
            case 2:
                return new ContactsList();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        // get item count - equal to number of tabs
        return 3;
    }
}