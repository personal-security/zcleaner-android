package com.xlab13.zcleaner.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlab13.zcleaner.Apps.AppsAdapter;
import com.xlab13.zcleaner.R;

import static com.xlab13.zcleaner.Activity.SplashActivity.apps;

public class MoreAppsActivity extends AppCompatActivity {
    Context context;

    private RecyclerView rvApps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        context = this;

        rvApps = findViewById(R.id.rvApps);


        if (!apps.isEmpty()){
            rvApps.setLayoutManager(new LinearLayoutManager(context));
            rvApps.setAdapter(new AppsAdapter(context, apps));
        }
    }


}
