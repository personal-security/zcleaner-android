package com.xlab13.zcleaner.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xlab13.zcleaner.Broadcasts.Alarm;
import com.xlab13.zcleaner.R;

import java.util.Calendar;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }, 1500);

        Intent intent = getIntent();
        if (TextUtils.isEmpty(intent.getAction())
                || !TextUtils.equals(intent.getAction(), Intent.ACTION_VIEW)
                || intent.getData() == null) {

        }else{
            // TODO : send referer
        }

        Intent myAlarm = new Intent(getApplicationContext(), Alarm.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(getApplicationContext(), 0, myAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar updateTime = Calendar.getInstance();
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 300000, recurringAlarm);
    }
}
