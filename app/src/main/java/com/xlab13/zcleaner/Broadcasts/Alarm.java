package com.xlab13.zcleaner.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xlab13.zcleaner.Activity.ActivityFAD;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentApp;
        intentApp = new Intent(context, ActivityFAD.class);
        intentApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentApp.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intentApp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intentApp);
    }
}