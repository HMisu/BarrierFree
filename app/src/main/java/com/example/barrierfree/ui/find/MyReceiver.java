package com.example.barrierfree.ui.find;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        Log.d("intent값", String.valueOf(intent));
        if(isEntering)
            Toast.makeText(context, "안심지역입니다.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "안심지역에서 벗어났습니디.", Toast.LENGTH_LONG).show();
    }

}
