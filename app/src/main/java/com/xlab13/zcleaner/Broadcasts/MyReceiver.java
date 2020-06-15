package com.xlab13.zcleaner.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xlab13.zcleaner.utils.MyJobService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "android.intent.action.BOOT_COMPLETED":
                new MyJobService().start(context);
                break;
        }
    }
}
