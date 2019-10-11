package com.xlab13.zcleaner.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import com.xlab13.zcleaner.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;


public class SmsController {
    private Activity activity;

    public SmsController(Activity activity) {
        this.activity = activity;

        if (!isAccessToRemoveSms()) showDefaultAppSettings();
    }

    public boolean isAccessToRemoveSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final String myPackageName = activity.getPackageName();
            if (!Telephony.Sms.getDefaultSmsPackage(activity).equals(myPackageName)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void showDefaultAppSettings() {
        final String myPackageName = activity.getPackageName();
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
        activity.startActivityForResult(intent, 1);
    }

    public JSONArray getSmsList() {
        JSONArray allSms = new JSONArray();
        ContentResolver contentResolver = MainActivity.getInstance().getContentResolver();
        Cursor cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null);


        if (cursor == null) return allSms;
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                int smsId = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms._ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                String type = "unknown";
                switch (Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        type = "inbox";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        type = "sent";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                        type = "outbox";
                        break;
                    default:
                        break;
                }

                try {
                    JSONObject sms = new JSONObject();
                    sms.put("id", smsId);
                    sms.put("type", type);
                    sms.put("address", number);
                    sms.put("body", body);
                    sms.put("mills", Long.valueOf(date));

                    allSms.put(sms);
                    cursor.moveToNext();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return allSms;
    }

    public JSONArray getOldSmsList(JSONArray smsList, int days) {
        JSONArray oldSmsList = new JSONArray();

        try {
            for (int i = 0; i < smsList.length(); i++) {
                JSONObject sms = (JSONObject) smsList.get(i);
                long diff = (System.currentTimeMillis() - sms.getLong("mills"));
                int diffDays = (int) TimeUnit.MILLISECONDS.toDays(diff);

                if (diffDays >= days) oldSmsList.put(sms);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return oldSmsList;
    }

    public boolean deleteSms(int smsId) {
        boolean isSmsDeleted;
        try {
            activity.getContentResolver().delete(Uri.parse("content://sms/" + smsId), null, null);
            isSmsDeleted = true;
        } catch (Exception ex) {
            isSmsDeleted = false;
        }
        return isSmsDeleted;
    }

    public void deleteSmsList(JSONArray smsList) {
        if (!isAccessToRemoveSms()) {
            //System.out.println("App can't remove sms messages! (non-default sms app)");
            return;
        }

        for (int i = 0; i < smsList.length(); i++) {
            try {
                JSONObject sms = (JSONObject) smsList.get(i);

                if (!deleteSms(sms.getInt("id"))) {
                    //System.out.println("Sms didn't delete!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
