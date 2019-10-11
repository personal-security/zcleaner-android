package com.xlab13.zcleaner.utils;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class network {

    public final static String szBase = "base";
    public final static String szUTF8 = "UTF-8";
    public final static String szGET = "GET";
    public final static String szPOST = "POST";
    public final static String szHttp = "http";
    public final static String szHttps = "https";

    public static String sendPost(String uls, JSONArray jos, boolean POST){
        String type = szGET;
        if(POST){
            type = szPOST;
        }
        StringBuilder result = new StringBuilder();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {

            if(uls.startsWith(szHttps)) {

            }else{
                URL url = new URL(uls);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(type);
                //if(type==szPOST){
                //conn.setDoOutput(true);
                //}
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write("");

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                //Log.e("===","code : "+responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                } else {
                    result = new StringBuilder();
                }
            }
        }catch(Exception e){
            Log.e("====",e.toString());
        }
        return result.toString();
    }
}
