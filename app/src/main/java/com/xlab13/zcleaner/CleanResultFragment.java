package com.xlab13.zcleaner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


import butterknife.BindView;
import butterknife.OnClick;

import static com.xlab13.zcleaner.utils.FS.readIntConfig;
import static com.xlab13.zcleaner.utils.FS.readLongConfig;

public class CleanResultFragment extends BaseFragment {
private InterstitialAd mInterstitialAd;
    private static final int LAYOUT = R.layout.fragment_clean_result;

    @BindView(R.id.resBox)
    LinearLayout resBox;
    Integer Sum = 0;

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public int getLayout() {
        return LAYOUT;
    }

    @OnClick(R.id.ok)
    public void onButtonOkClick(){
        mInterstitialAd.show();
        getActivity().finish();
        //Log.e("===","start ads inter");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle   = getArguments();
        resBox.removeAllViews();

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.admob_page_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                //getActivity().finish();
                //FragmentUtil.replaceFragment(getActivity().getSupportFragmentManager(),
                //        BaseFragment.newInstance(CleanOptionsFragment.class, null), false);
            }

            @Override
            public void onAdClosed() {
                //getActivity().finish();
                //FragmentUtil.replaceFragment(getActivity().getSupportFragmentManager(),
                //        BaseFragment.newInstance(CleanOptionsFragment.class, null), false);
            }
        });

        if(bundle.getBoolean("contactsOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            type.setText(getText(R.string.duplicated_contacts));
            TextView value = vi.findViewById(R.id.value);

            Integer i1 = readIntConfig(getContext(), "delete_contact_size");
            value.setText(i1.toString());
            resBox.addView(vi);
        }
        if(bundle.getBoolean("smsOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.sms));

            Integer i1 = readIntConfig(getContext(), "delete_sms_size");
            value.setText(i1 + " ");
            resBox.addView(vi);
        }
        if(bundle.getBoolean("fileOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.cache));

            Integer i1 = readIntConfig(getContext(), "delete_file_size") / (1024 * 1024);
            Sum += i1;
            value.setText(i1+" MB");
            resBox.addView(vi);
        }
        if(bundle.getBoolean("virusOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(R.string.viruses_found);
            Long i1 = readLongConfig(getContext(), "delete_virus_size");
            Log.e("===","virus : " + i1.toString());
            value.setText(i1.toString());
            resBox.addView(vi);

            View vi2 = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type2 = vi2.findViewById(R.id.type);
            TextView value2 = vi2.findViewById(R.id.value);
            type2.setText(R.string.viruses_delete);
            value2.setText(i1.toString());
            resBox.addView(vi2);
        }
        if(bundle.getBoolean("optimOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.memory_cleared));

            Integer i1 = readIntConfig(getContext(), "delete_optim_size");
            value.setText(i1+" mb");
            Sum += i1;
            resBox.addView(vi);
        }
        if(bundle.getBoolean("dataOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.personal_data));
            Integer i1 = readIntConfig(getContext(), "delete_data_size");
            Sum+=i1;
            value.setText(i1+" mb");
            resBox.addView(vi);
        }
        //View vi = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_clean_result,
        //        resBox, false);
        TextView value = view.findViewById(R.id.mb_cleaned);
        value.setText(Sum.toString());
    }
}
