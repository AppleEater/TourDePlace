package com.example.uaharoni.tourdeplace.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;

public class PowerReceiver extends BroadcastReceiver{
    private Context context;
    private final String actionPowerConnect = "android.intent.action.ACTION_POWER_CONNECTED";
    private final String actionPowerDisconnect = "android.intent.action.ACTION_POWER_DISCONNECTED";

    public PowerReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        String localMessage = null;
        this.context = context;

        Log.d("onReceive","Received action " + intentAction);
        if(intentAction.equals(actionPowerConnect)){
            localMessage = context.getString(R.string.snackbar_message_power_connected);
        } else if(intentAction.equals(actionPowerDisconnect)){
            localMessage = context.getString(R.string.snackbar_message_power_disconnected);
        }
        // Toast.makeText(context, localMessage, Toast.LENGTH_LONG).show();
        doSendBroadcast(localMessage);
    }
    private void doSendBroadcast(String message) {
        Intent intentSnack = new Intent("EVENT_SNACKBAR");

        if (message != null)
            intentSnack.putExtra("MESSAGE",message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentSnack);
    }

}
