package com.xlab13.zcleaner.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;


public class InstallReferrerReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = "";
        try {
            referrer = new String(Base64.decode(intent.getStringExtra("referrer"), Base64.DEFAULT));
        }catch (Exception e){

        }
        this.context = context;

    }
}

