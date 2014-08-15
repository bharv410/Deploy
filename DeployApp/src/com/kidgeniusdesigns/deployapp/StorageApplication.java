package com.kidgeniusdesigns.deployapp;

import android.app.Application;
import android.content.Context;

/***
 * This class gives us a singleton instance of Storage Service to be used
 * throughout our application.
 * 
 */
public class StorageApplication extends Application
{

    private StorageService mStorageService;
    private Context mContext;

    public StorageApplication(Context context)
    {
        mContext = context;
    }

    public StorageService getStorageService()
    {
        if (mStorageService == null)
        {
            mStorageService = new StorageService(mContext);
        }
        return mStorageService;
    }

}
