package com.xlab13.zcleaner.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.xlab13.zcleaner.CleanOptionsFragmentRV;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.R;
import com.xlab13.zcleaner.utils.FragmentUtil;
import com.xlab13.zcleaner.utils.MyJobService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity implements SensorEventListener {
    //public final static String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final int PERMISSION_REQUEST_CODE = 1;
    public final static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    private float[] mGravity;
    private float mAccel,mAccelCurrent,mAccelLast;
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private boolean FlagReal = false;
    private boolean startUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        new MyJobService().start(this);

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        int checkPermissionResult = checkPermission(permissions);

        FragmentUtil.replaceFragment(getSupportFragmentManager(),
                BaseFragment.newInstance(CleanOptionsFragmentRV.class, null), false);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("open_app", params);

        countLaunch();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(mAccel > 1){
                if(!startUpdate) {
                    startUpdate = true;
                    FlagReal = true;

                    Log.i("===","start check");
                    int checkPermissionResult = checkPermission(permissions);
                }
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Rate App");
        menu.add("More Apps");
        //menu.add("Настройки");
        //menu.add("Обновить базу");
        Log.i("~~~", "OptionsMenu created!");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String itemName = item.toString();
        Log.i("~~~", "itemSelected: " + itemName);
        switch (itemName) {
            case "Rate App":
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case "Настройки":
                // TODO: 01.11.2018 показать настройки
                break;
            case "Обновить базу":

                break;
            case "More Apps":
                startActivity(new Intent(this, MoreAppsActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("===","check");
        if (requestCode == 1) {



            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName(); // TODO: fix
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                        int x = 0;
                    }
                }
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setToolbarTitle(String title) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    public int checkPermission(String[] permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            int rationale = 0;
            int needToRequest =0;
            int i=0;
            for(String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        rationale++;
                    }
                    needToRequest ++;
                }else{
                    List<String> list = new ArrayList<String>(Arrays.asList(permissions));
                    list.remove(permission);
                    permissions = list.toArray(new String[0]);
                }
                i++;
            }
            if(needToRequest>0) {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE); //new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}
                return -1;
            }else{
                return 1;
            }
        }else{
            return 0;
        }

        //return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Set<String> permissionsGranded = new HashSet<>();

        for(int i=0; i<grantResults.length;i++){
            //debug("grantResults["+i+"]:" + grantResults[i]);
            if(grantResults[i]!=-1){
                permissionsGranded.add(permissions[i]);
            }
        }

    }

    private void countLaunch(){
        SharedPreferences sPref = getSharedPreferences("app", Context.MODE_PRIVATE);
        int launches = sPref.getInt("countLaunch", 0);
        launches++;
        if (launches >= 10){
            launches = 0;
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this)
                    .setMessage(R.string.more_apps)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), MoreAppsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            Dialog dialog = mBuilder.create();
            dialog.show();
        }
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("countLaunch", launches);
        ed.commit();
    }
}
