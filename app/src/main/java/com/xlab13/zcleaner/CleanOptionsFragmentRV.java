package com.xlab13.zcleaner;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xlab13.zcleaner.Activity.MainActivity;
import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.utils.FS;
import com.xlab13.zcleaner.utils.FragmentUtil;
import com.xlab13.zcleaner.utils.RVAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanOptionsFragmentRV extends BaseFragment {

    private static final int LAYOUT = R.layout.fragment_clean_options;

    @Override
    protected String getTitle() {
        //return getString(R.string.app_name);
        return "1";
    }

    @BindView(R.id.rvOptions)
    RecyclerView rv;

    private boolean fileOn;
    private boolean optimOn;
    private boolean dataOn;
    private boolean wipeOn;
    private boolean smsOn;
    private boolean contactsOn;
    private boolean virusOn;

    @Override
    public int getLayout() {
        return LAYOUT;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileOn = false;//FS.readBooleanConfig(getContext(), "fileOn");
        optimOn = FS.readBooleanConfig(getContext(), "optimOn");
        dataOn = FS.readBooleanConfig(getContext(), "dataOn");
        wipeOn = FS.readBooleanConfig(getContext(), "wipeOn");
        smsOn = FS.readBooleanConfig(getContext(), "smsOn");
        contactsOn = FS.readBooleanConfig(getContext(), "contactsOn");
        virusOn = FS.readBooleanConfig(getContext(), "virusOn");
        Log.i("---", ""  + fileOn + optimOn + dataOn + wipeOn + smsOn + contactsOn + virusOn);

        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        initializeData();
        RVAdapter adapter = new RVAdapter(icons);
        rv.setAdapter(adapter);

        adapter.SetOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        if (!fileOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            fileOn = true;
                            Log.i("---", ""  +fileOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            fileOn = false;
                            Log.i("---", ""  + fileOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 1:
                        if (!optimOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            optimOn = true;
                            Log.i("---", ""  +optimOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            optimOn = false;
                            Log.i("---", ""  +optimOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 2:
                        if (!dataOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            dataOn = true;
                            Log.i("---", ""  +dataOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            dataOn = false;
                            Log.i("---", ""  +dataOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 3:
                        if (!wipeOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            wipeOn = true;
                            Log.i("---", ""  +wipeOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            wipeOn = false;
                            Log.i("---", ""  +wipeOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 4:
                        if (!smsOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            smsOn = true;
                            Log.i("---", ""  +smsOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            smsOn = false;
                            Log.i("---", ""  +smsOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 5:
                        if (!contactsOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            contactsOn = true;
                            Log.i("---", ""  +contactsOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            contactsOn = false;
                            Log.i("---", ""  +contactsOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;
                    case 6:
                        if (!virusOn) {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_wh_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#ffffff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#2f8bff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#2f8bff"));
                            virusOn = true;
                            Log.i("---", ""  +virusOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",true);
                        } else {
                            ((ImageView) view.findViewById(R.id.btn_icon))
                                    .setImageResource(icons.get(position).icon_bl_Id);
                            ((TextView) view.findViewById(R.id.btn_text) )
                                    .setTextColor(Color.parseColor("#2f8bff"));
                            CardView cardView = view.findViewById(R.id.cardView);
                            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                            //cardView.setOutlineSpotShadowColor(Color.parseColor("#000000"));
                            virusOn = false;
                            Log.i("---", ""  +virusOn);
                            FS.writeBooleanConfig(getContext(),"fileOn",false);
                        }
                        break;

                }
            }
        });
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
        bundle.putBoolean("wipeOn", wipeOn);
        Log.i("---", ""  + fileOn + optimOn + dataOn + wipeOn + smsOn + contactsOn + virusOn);

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

    public class IconButton {
        int textID;
        int icon_bl_Id;
        int icon_wh_Id;

        IconButton(int textID, int icon_bl_Id, int icon_wh_Id) {
            this.textID = textID;
            this.icon_bl_Id = icon_bl_Id;
            this.icon_wh_Id = icon_wh_Id;
        }

        public String getTextId() {
            return (String) getText(textID);
        }

        public int getIconBlId() {
            return icon_bl_Id;
        }

        public int getIconWhId() {
            return icon_wh_Id;
        }

    }

    private List<IconButton> icons;

    private void initializeData() {
        icons = new ArrayList<>();
        icons.add(new IconButton(R.string.file, R.drawable.ic_file, R.drawable.ic_file_wh));
        icons.add(new IconButton(R.string.optim, R.drawable.ic_optimize, R.drawable.ic_optimize_wh));
        icons.add(new IconButton(R.string.data, R.drawable.ic_data, R.drawable.ic_data_wh));
        icons.add(new IconButton(R.string.wipe, R.drawable.ic_wipe, R.drawable.ic_wipe_wh));
        //icons.add(new IconButton(R.string.sms, R.drawable.ic_sms, R.drawable.ic_sms_wh));
        //icons.add(new IconButton(R.string.contacts, R.drawable.ic_contacts, R.drawable.ic_contacts_wh));
        //icons.add(new IconButton(R.string.virus, R.drawable.ic_viruses, R.drawable.ic_viruses_wh));
    }
}