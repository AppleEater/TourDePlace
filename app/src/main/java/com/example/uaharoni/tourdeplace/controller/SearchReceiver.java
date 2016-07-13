package com.example.uaharoni.tourdeplace.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SearchReceiver extends BroadcastReceiver {

    private String TAG = "SearchReceiver";

    public SearchReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");
        if(intent != null){
            String intentAction = intent.getAction();
            Log.d(TAG,"Received action " + intentAction);

        }
    }
}
