package com.xlab13.zcleaner.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.xlab13.zcleaner.R;

import java.util.List;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        int appsInstalled = list.size();
        int prevAppsInstalled = LocalStorage.readInteger(this, "appsInstalled");

        Log.i("~~~", "Apps Installed: " + appsInstalled);
        Log.i("~~~", "Previous Apps Installed: " + prevAppsInstalled);

        if (appsInstalled > prevAppsInstalled)
            showNotification("Установлено новой приложение, необходима проверка.");
        else if (appsInstalled < prevAppsInstalled)
            showNotification("Приложение удалено, необходимо очистить систему.");

        LocalStorage.writeInteger(this, "appsInstalled", appsInstalled);
        start(this);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void start(Context context) {
        ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);
        builder.setMinimumLatency(6000); // wait at least
        builder.setOverrideDeadline(12000); // maximum delay
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require unmetered network
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    private void showNotification(String text) {
        Log.i("~~~", "ShowNotification: " + "Оптимизация" + ", " + text);

        Notification notification;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel;
        // The id of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("zxc", "Google Play", importance);
            // Configure the notification channel.
            mChannel.setDescription(text);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        }


        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Оптимизация");
        builder.setContentText(text);
        builder.setChannelId("zxc");
        builder.setSmallIcon(R.drawable.ic_launcher_background);

        notification = builder.build();

        notificationManager.notify(1, notification);
    }
}
