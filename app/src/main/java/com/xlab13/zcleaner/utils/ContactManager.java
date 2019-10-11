package com.xlab13.zcleaner.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.xlab13.zcleaner.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactManager {

    public JSONArray getContactList() {
        ContentResolver contentResolver = MainActivity.getInstance().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        JSONArray contacts = new JSONArray();

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor != null && cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //System.out.println("Name: " + name);
                        //System.out.println("Phone Number: " + phoneNo);

                        JSONObject contact = new JSONObject();
                        try {
                            contact.put("name", name);
                            contact.put("phone", phoneNo);
                            contacts.put(contact);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pCur.close();
                }
            }
        }
        if(cursor!=null){
            cursor.close();
        }

        return contacts;
    }

    public JSONArray getDublicateList(JSONArray contacts) {
        JSONArray result = new JSONArray();
        try {
            for (int i = 0; i < contacts.length() - 1; i++) {
                JSONObject contactA = (JSONObject) contacts.get(i);
                String phoneA = getValidNumber(contactA.getString("phone"));
                for (int j = i + 1; j < contacts.length(); j++) {
                    if (i == j) continue;
                    JSONObject contactB = (JSONObject) contacts.get(j);
                    String phoneB = getValidNumber(contactB.getString("phone"));
                    if (phoneA.equals(phoneB)) {
                        result.put(contactB);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean deleteContact(String name, String phone) {
        ContentResolver contentResolver = MainActivity.getInstance().getContentResolver();
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = contentResolver.query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        contentResolver.delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    public void deleteContacts(JSONArray contacts) {
        try {
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = (JSONObject) contacts.get(i);
                if (!deleteContact(contact.getString("name"), contact.getString("phone")))
                    System.out.println("Contact didn't delete!");
            }
        } catch(JSONException e) {
                e.printStackTrace();
        }
    }

    private String getValidNumber(String phone) {
        return phone.replaceAll("[ \\-)(]","").replace("+7", "8");
    }
}
