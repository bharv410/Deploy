package com.kidgeniusdesigns.realdeploy.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kidgeniusdesigns.deployapp.fragments.AttendeesFrag;
import com.kidgeniusdesigns.deployapp.fragments.ContactsList;
import com.kidgeniusdesigns.deployapp.fragments.DeploySpecialsFrag;
import com.kidgeniusdesigns.deployapp.fragments.EditEventFrag;
import com.kidgeniusdesigns.deployapp.fragments.MyEventsFrag;

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
            case 3:
            	return new MyEventsFrag();
            case 4:
            	return new DeploySpecialsFrag();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        // get item count - equal to number of tabs
        return 5;
    }
}