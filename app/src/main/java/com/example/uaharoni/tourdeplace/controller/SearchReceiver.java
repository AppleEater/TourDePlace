package com.example.uaharoni.tourdeplace.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;

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
            if(intentAction.equals(context.getString(R.string.search_service_custom_intent_action))){
                String serviceStatus = intent.getStringExtra(context.getString(R.string.search_service_custom_intent_status));
                Log.d(TAG,"Got Search service status: " + serviceStatus);
                Intent intentSnack = new Intent(context.getString(R.string.power_receiver_custom_intent_action));
                String message = "Unknown";
                switch (Integer.parseInt(serviceStatus)){
                    case 0:
                        message = context.getString(R.string.search_service_status_RUNNING_text);
                        break;
                    case 1:
                        message = context.getString(R.string.search_service_status_FINISHED_text);
                        break;
                    case 2:
                        message = context.getString(R.string.search_service_status_ERROR_text);
                        break;
                    default:
                        message = context.getString(R.string.search_service_status_UNKNOWN_text);
                        break;
                }
                intentSnack.putExtra(context.getString(R.string.snackbar_message_custom_intent_extra_text),message);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentSnack);

                //SearchFragment.updateProgressBar(Integer.parseInt(serviceStatus));
                if(Integer.parseInt(serviceStatus)==1){
                    Log.d("onReceive", "Time to refresh the adapter");
                   // SearchFragment.refreshAdapter();
                }
            }
        }
    }
}
