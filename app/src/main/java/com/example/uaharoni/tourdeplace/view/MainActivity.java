package com.example.uaharoni.tourdeplace.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private BroadcastReceiver snackBarMessageReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initReceivers();

        Log.d("onCreate-Main","Finished onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(snackBarMessageReceiver, new IntentFilter("EVENT_SNACKBAR"));
    }

    private void initReceivers(){
        snackBarMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null) {
                    String message = intent.getStringExtra("MESSAGE");
                    Log.d("onReceive","Got message " + message);
                    Snackbar.make(findViewById(R.id.main_container),message,Snackbar.LENGTH_LONG).show();
                }
            }
        };  // closing the receiver
    }
}
