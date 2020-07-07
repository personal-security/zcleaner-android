package com.xlab13.zcleaner;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xlab13.zcleaner.utils.FS.bytesToHuman;
import static com.xlab13.zcleaner.utils.FS.readIntConfig;
import static com.xlab13.zcleaner.utils.FS.readLongConfig;

public class CleanResultFragment extends BaseFragment {

    private Button mBuyButton;

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    private String mSkuId = "donate";

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
    }

    @OnClick(R.id.donate)
    public void onButtonDonateClick(){
        launchBilling(mSkuId);
    }

    private static int getAlfaRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle   = getArguments();
        resBox.removeAllViews();

        initBilling();

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

            Integer i1 = readIntConfig(getContext(), "delete_file_size") + getAlfaRange(0,5);
            Sum += i1;
            value.setText(bytesToHuman(i1));
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

            Integer i1 = readIntConfig(getContext(), "delete_optim_size")+ getAlfaRange(0,50);
            value.setText(bytesToHuman(i1));
            Sum += i1;
            resBox.addView(vi);
        }
        if(bundle.getBoolean("dataOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.personal_data));
            Integer i1 = readIntConfig(getContext(), "delete_data_size") + getAlfaRange(0,5);
            Sum+=i1;
            value.setText(bytesToHuman(i1));
            resBox.addView(vi);
        }
        if(bundle.getBoolean("wipeOn")){
            View vi = LayoutInflater.from(getActivity()).inflate(R.layout.res_item,
                    resBox, false);
            TextView type = vi.findViewById(R.id.type);
            TextView value = vi.findViewById(R.id.value);
            type.setText(getText(R.string.wipe));
            Integer i1 = readIntConfig(getContext(), "delete_wipe_size") + getAlfaRange(0,5);
            //Sum+=i1;
            value.setText(bytesToHuman(i1));
            resBox.addView(vi);
        }
        //View vi = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_clean_result,
        //        resBox, false);
        TextView value = view.findViewById(R.id.mb_cleaned);
        Sum = Sum / 1024 / 1024;
        value.setText(Sum.toString());
    }

    private void initBilling() {
        Log.i("===","try init payment service");
        mBillingClient = BillingClient.newBuilder(getActivity()).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                    //here when purchase completed
                    payComplete();
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                Log.i("===","payment service fail , code : "+ billingResponseCode);
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    //below you can query information about products and purchase
                    Log.i("===","payment service ok");
                    querySkuDetails(); //query for products
                    List<Purchase> purchasesList = queryPurchases(); //query for purchases

                    //if the purchase has already been made to give the goods
                    for (int i = 0; i < purchasesList.size(); i++) {
                        String purchaseId = purchasesList.get(i).getSku();
                        if(TextUtils.equals(mSkuId, purchaseId)) {
                            payComplete();
                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //here when something went wrong, e.g. no internet connection
                Log.i("===","payment service fail");
            }
        });
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuId);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == 0) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                        Log.i("===",skuDetails.getDescription());
                    }
                }
            }
        });
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        return purchasesResult.getPurchasesList();
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(getActivity(), billingFlowParams);
    }

    private void payComplete() {
        Toast.makeText(getContext(), getString(R.string.payment_success), Toast.LENGTH_SHORT).show();
        onButtonOkClick();
    }
}
