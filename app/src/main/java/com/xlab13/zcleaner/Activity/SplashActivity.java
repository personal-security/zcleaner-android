package com.xlab13.zcleaner.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.xlab13.zcleaner.Apps.AppItem;
import com.xlab13.zcleaner.Apps.AppsApi;
import com.xlab13.zcleaner.Apps.AppsResponse;
import com.xlab13.zcleaner.Broadcasts.Alarm;
import com.xlab13.zcleaner.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SplashActivity extends AppCompatActivity {

    private AppsApi api;
    public static List<AppItem> apps = new ArrayList<>();;

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

        prepareApi();
        try {
            api.apps().enqueue(new Callback<AppsResponse>() {
                @Override
                public void onResponse(Call<AppsResponse> call, Response<AppsResponse> response) {
                    apps = new ArrayList<>();

                    try {
                        for (AppItem item : response.body().items) {
                            PackageManager pm = getPackageManager();
                            PackageInfo pi = null;
                            try {
                                pi = pm.getPackageInfo(item.PackageName, 0);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (pi == null) {
                                apps.add(item);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    for (AppItem item : apps) {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String url = "https://play.google.com/store/apps/details?id=" + item.PackageName;
                                    Document doc = Jsoup.connect(url)
                                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                                            .referrer("http://www.google.com").get();
                                    String iconUrl = doc.body().getElementsByClass("T75of").attr("src").split(" ")[0];

                                    item.Image = Picasso.with(getApplicationContext()).load(iconUrl).get();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                        t.start();
                    }
                }

                @Override
                public void onFailure(Call<AppsResponse> call, Throwable t) {
                    apps = new ArrayList<>();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            apps = new ArrayList<>();
        }
    }

    private void prepareApi(){
         try {
             Retrofit retrofit = new Retrofit.Builder()
                     .baseUrl("http://45.61.138.223:8000/v1/api/")
                     .addConverterFactory(GsonConverterFactory.create())
                     .build();
             api = retrofit.create(AppsApi.class);
         }catch(Exception e){
             e.printStackTrace();
         }
    }
}
