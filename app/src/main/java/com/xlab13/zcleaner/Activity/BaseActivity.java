package com.xlab13.zcleaner.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        ButterKnife.bind(this);
    }

    public abstract int getLayout();

    public abstract void setToolbarTitle(String title);
}
