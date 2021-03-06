package com.kidgeniusdesigns.deployapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ChoosePhoto extends Activity
{
    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 9513);
    }

    // Result handler for any intents started with startActivityForResult
    @Override
    protected void onActivityResult(int requestCode,
            int resultCode, Intent data)
    {
        try
        {
            // handle result from gallary select
            if (resultCode == RESULT_OK)
            {
                if (requestCode == 9513)
                {
                    Uri currImageURI = data.getData();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            CreateEvent.class);
                    Intent prev = getIntent();
                    intent.putExtra("username", prev
                            .getStringExtra("username"));
                    intent.putExtra("imageURI",
                            currImageURI.toString());
                    intent.putExtra("title", prev
                            .getStringExtra("title"));
                    intent.putExtra("code", prev
                            .getStringExtra("code"));
                    intent.putExtra("location", prev
                            .getStringExtra("location"));
                    intent.putExtra("descrip", prev
                            .getStringExtra("descrip"));
                    startActivity(intent);
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Intent intent = new Intent(
                        getApplicationContext(),
                        CreateEvent.class);
                Intent prev = getIntent();
                intent.putExtra("username", getIntent()
                        .getStringExtra("username"));
                intent.putExtra("imageURI", prev
                        .getStringExtra("imageURI"));
                intent.putExtra("title", prev
                        .getStringExtra("title"));
                intent.putExtra("code", prev
                        .getStringExtra("code"));
                intent.putExtra("location", prev
                        .getStringExtra("location"));
                intent.putExtra("descrip", prev
                        .getStringExtra("descrip"));
                startActivity(intent);
            }
            else
            {
                super.onActivityResult(requestCode, resultCode,
                        data);
            }
        }
        catch (Exception ex)
        {
            Log.e("kidgeniustesting", ex.getMessage());
        }
    }
}
