package com.xlab13.zcleaner.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

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
        }catch(Exception e){
            Log.e("====",e.toString());
        }
        return result.toString();
    }

    static int randomInteger(int aStart, int aEnd){
        Random aRandom = new Random();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("");
        }
        long range = (long)aEnd - (long)aStart + 1;
        long fraction = (long)(range * aRandom.nextDouble());
        int randomNumber =  (int)(fraction + aStart);
        return randomNumber;
    }

    public static String download_file(final Context cnt, final String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        String fileName = randomInteger(11111, 99999) + ".apk" ;

        String path = Environment.getExternalStorageDirectory() + "/";
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            URL url1 = new URL(url);
            File file = new File(dir, fileName);

            URLConnection uconn = url1.openConnection();
            uconn.setReadTimeout(120000);
            uconn.setConnectTimeout(15000);

            InputStream is = uconn.getInputStream();
            BufferedInputStream bufferinstream = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bufferinstream.read()) != -1) {
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return "";
        }
        return path + "" + fileName;
    }
}
