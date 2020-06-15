package com.xlab13.zcleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xlab13.zcleaner.Fragment.BaseFragment;
import com.xlab13.zcleaner.utils.ContactManager;
import com.xlab13.zcleaner.utils.FS;
import com.xlab13.zcleaner.utils.FragmentUtil;
import com.xlab13.zcleaner.utils.SmsController;

import org.json.JSONArray;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xlab13.zcleaner.utils.FS.scanAVFiles;
import static com.xlab13.zcleaner.utils.FS.writeIntConfig;
import static java.lang.Thread.sleep;

public class CleaningProgressFragment extends BaseFragment {

    private Context cnt;
    private View ww;

    @BindView(R.id.prBar1)
    ImageView prBar1;
    @BindView(R.id.prBar2)
    ImageView prBar2;
    @BindView(R.id.prView)
    TextView prView;
    @BindView(R.id.prViewProgress)
    TextView prViewData;
    Bundle bundle;
    private static final int LAYOUT = R.layout.fragment_cleaning_progress;
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
             FragmentUtil.replaceFragment(getFragmentManager(),
                      BaseFragment.newInstance(CleanResultFragment.class, bundle), false);
        }
    };
    Handler handler = new Handler();
    StoryCountDown countDownTimer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        startAnimation();
        cnt = getContext();
        new MyTask().execute();

        //handler.postDelayed(myRunnable, 5100);
    }

    private void startAnimation(){
        RotateAnimation rotate = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5500);
        rotate.setInterpolator(new LinearInterpolator());

        RotateAnimation rotate1 = new RotateAnimation(0, -720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate1.setDuration(5500);

        rotate1.setRepeatCount(Animation.INFINITE);
        rotate1.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.RESTART);

        rotate1.setInterpolator(new LinearInterpolator());
        prBar1.startAnimation(rotate);
        prBar2.startAnimation(rotate1);
        countDownTimer = new StoryCountDown(5000, 100);
        countDownTimer.start();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public int getLayout() {
        return LAYOUT;
    }

    @OnClick(R.id.stop_clean)
    public void onStopCleanClick() {
        countDownTimer.cancel();
        handler.removeCallbacks(myRunnable);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private class StoryCountDown extends CountDownTimer {
        public StoryCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            prView.setText(getText(R.string.scanings)+" " + (5 - (millisUntilFinished / 1000))*20   +" %");
        }

        @Override
        public void onFinish() {
            prView.setText("Please wait!");
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInfo.setText("Begin");
        }

        @SuppressLint("SetTextI18n")
        private Integer removeSubFiles(JSONArray jas){
            Integer delete_file_size = 0;
            try {
                //JSONArray jas = new JSONArray(list);
                for (int i = 0; i < jas.length(); i++) {
                    Log.i("===", "json : " + jas.toString());
                    if (jas.getJSONObject(i).getString("type").equals("dir")){
                        String path = jas.getJSONObject(i).getString("path");
                        //prViewData.setText(path); // TODO: fix ui
                        //getActivity().runOnUiThread(() -> prViewData.setText(path));
                        delete_file_size += removeSubFiles(jas.getJSONObject(i).getJSONArray("parent"));
                        //sleep(300);
                    }else {
                        if (jas.getJSONObject(i).has("path")) {
                            final String file = jas.getJSONObject(i).getString("path");

                            File curfile = new File(file);
                            if (curfile.isFile()) {
                                Integer file_size = (int) curfile.length();
                                if (file.endsWith(".apk") || file.endsWith(".log") || file.endsWith(".tmp"))
                                    try {
                                        long diff = new Date().getTime() - curfile.lastModified();
                                        if (diff > 1 * 24 * 60 * 60 * 1000) {
                                            if (curfile.delete()) {
                                                delete_file_size += file_size;
                                            }
                                        }
                                    } catch (Exception ignored) {

                                    }
                            }

                            Log.i("===", "file : " + file);

                            //prViewData.setText(file);
                            //sleep(1000);
                        }
                    }
                }
                prViewData.setText(getString(R.string.scanings)+"...");
            } catch (Exception e) {
                Log.e("===", e.toString());
            }
            return delete_file_size;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (bundle.getBoolean("contactsOn")) {
                ContactManager contactManager = new ContactManager();
                JSONArray contactList = contactManager.getContactList();
                JSONArray clones = contactManager.getDublicateList(contactList);
//                contactManager.deleteContacts(clones);
                writeIntConfig(getContext(), "delete_contact_size", clones.length());
            }
            if (bundle.getBoolean("smsOn")) {
                SmsController smsController = new SmsController(getActivity());
                JSONArray smsList = smsController.getSmsList();
                JSONArray oldSmsList = smsController.getOldSmsList(smsList, 30);
//                smsController.deleteSmsList(oldSmsList);
                writeIntConfig(getContext(), "delete_sms_size", oldSmsList.length());
            }
            if (bundle.getBoolean("fileOn")) {
                Integer delete_file_size = 0;
                String list = FS.getRootTree();
                try {
                    delete_file_size = removeSubFiles(new JSONArray(list));
                }catch (Exception e){
                    Log.e("===",e.toString());
                }

                FS.writeIntConfig(getContext(), "delete_file_size", delete_file_size);
                writeIntConfig(getContext(), "delete_file_size", delete_file_size);
            }
            if (bundle.getBoolean("virusOn")) {
                File rootDir = Environment.getExternalStorageDirectory();
                // TODO: 26.10.2018 отказаться от хранения всех хешей в переменной, т.к. это очень затратно
                // TODO: 26.10.2018 автообновление базы
                ArrayList<String> fileHashes = FS.generateFileHashesByMask(rootDir, ".+\\.pdf");
                //writeIntConfig(getContext(), "delete_virus_size", 0);
                getActivity().runOnUiThread(() -> prViewData.setText("Antivirus scannig...wait..."));
                scanAVFiles(getContext(),Environment.getExternalStorageDirectory());
            }
            if (bundle.getBoolean("optimOn")) {
                writeIntConfig(getContext(), "delete_optim_size", 0);
            }
            if (bundle.getBoolean("dataOn")) {
                writeIntConfig(getContext(), "delete_data_size", 0);
            }

            if (bundle.getBoolean("wipeOn")) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long bytesAvailable;
                if (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                }
                else {
                    bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
                }
                Long megAvailable = bytesAvailable;

                try { //TODO update for new methods
                    File fileName = new File(Environment.getExternalStorageDirectory(), "wipe.dat");
                    FileOutputStream fos = new FileOutputStream(fileName);
                    fos.write(megAvailable.intValue()-1024*1024*10);
                    fos.close();
                    fileName.delete();
                }catch (Exception e){
                    Log.i("===",e.toString());
                }
                try{
                    File curfile = new File(Environment.getExternalStorageDirectory(), "wipe.dat");
                    curfile.delete();
                }catch (Exception e){
                    Log.i("===",e.toString());
                }

                writeIntConfig(getContext(), "delete_wipe_size", megAvailable.intValue());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //tvInfo.setText("End");
            handler.postDelayed(myRunnable, 5000);
        }
    }
}
