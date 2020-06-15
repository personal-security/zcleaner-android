package com.xlab13.zcleaner.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xlab13.zcleaner.Activity.BaseActivity;
import com.xlab13.zcleaner.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    public static <T extends BaseFragment> T newInstance(Class<T> mClass, Bundle args) {
        try {
            T instance = mClass.newInstance();
            instance.setArguments(args);
            return instance;
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SwipeRefreshLayout swipeRefreshLayout;

    protected Unbinder unbinder;

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).setToolbarTitle(getTitle());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view = inflater.inflate(getLayout(), container, false);

        View view = inflateWithLoadingIndicator(getLayout(), container);

        MobileAds.initialize(getContext(),getString(R.string.admob_app_id));
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(mAdView!=null)
        mAdView.loadAd(adRequest);

        this.unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
        }
        super.onDestroyView();
    }


    protected View inflateWithLoadingIndicator(int resId, ViewGroup parent) {
        swipeRefreshLayout = new SwipeRefreshLayout(getContext());
        swipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        swipeRefreshLayout.setEnabled(false);
        View view = LayoutInflater.from(getContext()).inflate(resId, parent, false);
        swipeRefreshLayout.addView(view);
        return swipeRefreshLayout;
    }

    protected void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
    }

    protected abstract String getTitle();

    public abstract int getLayout();

}
