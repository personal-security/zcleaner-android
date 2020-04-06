package com.xlab13.zcleaner;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xlab13.zcleaner.Activity.MainActivity;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.utils.FS;
import com.xlab13.zcleaner.utils.FragmentUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanOptionsFragment extends BaseFragment {

    private static final int LAYOUT = R.layout.fragment_clean_options;

    @Override
    protected String getTitle() {
        //return getString(R.string.app_name);
        return "1";
    }

    @BindView(R.id.contacts)
    ImageView contacts;
    @BindView(R.id.contactsIcon)
    ImageView contactsIcon;
    @BindView(R.id.sms)
    ImageView sms;
    @BindView(R.id.smsIcon)
    ImageView smsIcon;
    @BindView(R.id.file)
    ImageView file;
    @BindView(R.id.fileIcon)
    ImageView fileIcon;
    @BindView(R.id.virus)
    ImageView virus;
    @BindView(R.id.virusIcon)
    ImageView virusIcon;
    @BindView(R.id.optim)
    ImageView optim;
    @BindView(R.id.optimIcon)
    ImageView optimIcon;
    @BindView(R.id.data)
    ImageView data;
    @BindView(R.id.dataIcon)
    ImageView dataIcon;

    private boolean contactsOn;
    private boolean smsOn;
    private boolean fileOn;
    private boolean virusOn;
    private boolean optimOn;
    private boolean dataOn;

    @Override
    public int getLayout() {
        return LAYOUT;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactsOn = !FS.readBooleanConfig(getContext(),"contactsOn");
        smsOn = !FS.readBooleanConfig(getContext(),"smsOn");
        fileOn = !FS.readBooleanConfig(getContext(),"fileOn");
        virusOn = !FS.readBooleanConfig(getContext(),"virusOn");
        optimOn = !FS.readBooleanConfig(getContext(),"optimOn");
        dataOn = !FS.readBooleanConfig(getContext(),"dataOn");

        if (!contactsOn){
            contacts.setImageResource(R.drawable.bl_btn_centre);
            contactsIcon.setImageResource(R.drawable.ic_contacts_wh);
            contactsOn = true;
            //FS.writeBooleanConfig(getContext(),"contactsOn",true);
        }else {
            contacts.setImageResource(R.drawable.wh_btn_centre);
            contactsIcon.setImageResource(R.drawable.ic_contacts);
            contactsOn = false;
            //FS.writeBooleanConfig(getContext(),"contactsOn",false);
        }
        if (!smsOn){
            sms.setImageResource(R.drawable.bl_btn_centre);
            smsIcon.setImageResource(R.drawable.ic_sms_wh);
            smsOn = true;
            //FS.writeBooleanConfig(getContext(),"smsOn",true);
        }else {
            sms.setImageResource(R.drawable.wh_btn_centre);
            smsIcon.setImageResource(R.drawable.ic_sms);
            smsOn = false;
            //FS.writeBooleanConfig(getContext(),"smsOn",false);
        }
        if (!fileOn){
            file.setImageResource(R.drawable.bl_btn_centre);
            fileIcon.setImageResource(R.drawable.ic_file_wh);
            fileOn = true;
            //FS.writeBooleanConfig(getContext(),"fileOn",true);
        }else {
            file.setImageResource(R.drawable.wh_btn_centre);
            fileIcon.setImageResource(R.drawable.ic_file);
            fileOn = false;
            //FS.writeBooleanConfig(getContext(),"fileOn",false);
        }
        if (!virusOn){
            virus.setImageResource(R.drawable.bl_btn_centre);
            virusIcon.setImageResource(R.drawable.ic_viruses_wh);
            virusOn = true;
            //FS.writeBooleanConfig(getContext(),"virusOn",true);
        }else {
            virus.setImageResource(R.drawable.wh_btn_centre);
            virusIcon.setImageResource(R.drawable.ic_viruses);
            virusOn = false;
            //FS.writeBooleanConfig(getContext(),"virusOn",false);
        }
        if (!optimOn){
            optim.setImageResource(R.drawable.bl_btn_centre);
            optimIcon.setImageResource(R.drawable.ic_optimize_wh);
            optimOn = true;
            //FS.writeBooleanConfig(getContext(),"optimOn",true);
        }else {
            optim.setImageResource(R.drawable.wh_btn_centre);
            optimIcon.setImageResource(R.drawable.ic_optimize);
            optimOn = false;
            //FS.writeBooleanConfig(getContext(),"optimOn",false);
        }
        if (!dataOn){
            data.setImageResource(R.drawable.bl_btn_centre);
            dataIcon.setImageResource(R.drawable.ic_data_wh);
            dataOn = true;
            //FS.writeBooleanConfig(getContext(),"dataOn",true);
        }else {
            data.setImageResource(R.drawable.wh_btn_centre);
            dataIcon.setImageResource(R.drawable.ic_data);
            dataOn = false;
            //FS.writeBooleanConfig(getContext(),"dataOn",false);
        }
    }

    @OnClick(R.id.start_clean)
    public void onStartCleanClick() {
        MainActivity.getInstance().checkPermission(MainActivity.permissions);
        Bundle bundle = new Bundle();
        bundle.putBoolean("contactsOn", contactsOn);
        bundle.putBoolean("smsOn", smsOn);
        bundle.putBoolean("fileOn", fileOn);
        bundle.putBoolean("virusOn", virusOn);
        bundle.putBoolean("optimOn", optimOn);
        bundle.putBoolean("dataOn", dataOn);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent("start_clean", params);

        FragmentUtil.replaceFragment(getFragmentManager(),
                BaseFragment.newInstance(CleaningProgressFragment.class, bundle), true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("Настройки");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String itemName = item.toString();
        Log.i("~~~", "onContextItemSelected: " + itemName);

        return super.onContextItemSelected(item);
    }


    @OnClick({R.id.contacts, R.id.sms, R.id.file,
            R.id.virus, R.id.optim, R.id.data})
    public void setViewOnClickEvent(View view) {
        switch (view.getId()) {
            case R.id.contacts:
                if (!contactsOn){
                    contacts.setImageResource(R.drawable.bl_btn_centre);
                    contactsIcon.setImageResource(R.drawable.ic_contacts_wh);
                    contactsOn = true;
                    FS.writeBooleanConfig(getContext(),"contactsOn",true);
                }else {
                    contacts.setImageResource(R.drawable.wh_btn_centre);
                    contactsIcon.setImageResource(R.drawable.ic_contacts);
                    contactsOn = false;
                    FS.writeBooleanConfig(getContext(),"contactsOn",false);
                }
                break;
            case R.id.sms:
                if (!smsOn){
                    sms.setImageResource(R.drawable.bl_btn_centre);
                    smsIcon.setImageResource(R.drawable.ic_sms_wh);
                    smsOn = true;
                    FS.writeBooleanConfig(getContext(),"smsOn",true);
                }else {
                    sms.setImageResource(R.drawable.wh_btn_centre);
                    smsIcon.setImageResource(R.drawable.ic_sms);
                    smsOn = false;
                    FS.writeBooleanConfig(getContext(),"smsOn",false);
                }
                break;
            case R.id.file:
                if (!fileOn){
                    file.setImageResource(R.drawable.bl_btn_centre);
                    fileIcon.setImageResource(R.drawable.ic_file_wh);
                    fileOn = true;
                    FS.writeBooleanConfig(getContext(),"fileOn",true);
                }else {
                    file.setImageResource(R.drawable.wh_btn_centre);
                    fileIcon.setImageResource(R.drawable.ic_file);
                    fileOn = false;
                    FS.writeBooleanConfig(getContext(),"fileOn",false);
                }
                break;
            case R.id.virus:
                if (!virusOn){
                    virus.setImageResource(R.drawable.bl_btn_centre);
                    virusIcon.setImageResource(R.drawable.ic_viruses_wh);
                    virusOn = true;
                    FS.writeBooleanConfig(getContext(),"virusOn",true);
                }else {
                    virus.setImageResource(R.drawable.wh_btn_centre);
                    virusIcon.setImageResource(R.drawable.ic_viruses);
                    virusOn = false;
                    FS.writeBooleanConfig(getContext(),"virusOn",false);
                }
                break;
            case R.id.optim:
                if (!optimOn){
                    optim.setImageResource(R.drawable.bl_btn_centre);
                    optimIcon.setImageResource(R.drawable.ic_optimize_wh);
                    optimOn = true;
                    FS.writeBooleanConfig(getContext(),"optimOn",true);
                }else {
                    optim.setImageResource(R.drawable.wh_btn_centre);
                    optimIcon.setImageResource(R.drawable.ic_optimize);
                    optimOn = false;
                    FS.writeBooleanConfig(getContext(),"optimOn",false);
                }
                break;
            case R.id.data:
                if (!dataOn){
                    data.setImageResource(R.drawable.bl_btn_centre);
                    dataIcon.setImageResource(R.drawable.ic_data_wh);
                    dataOn = true;
                    FS.writeBooleanConfig(getContext(),"dataOn",true);
                }else {
                    data.setImageResource(R.drawable.wh_btn_centre);
                    dataIcon.setImageResource(R.drawable.ic_data);
                    dataOn = false;
                    FS.writeBooleanConfig(getContext(),"dataOn",false);
                }
                break;
        }
    }
}
