package com.example.avvos.androidweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Settings {

    public static String apiUrl     = "";
    public static String appVersion = "1.0";
    public static String language   = Locale.getDefault().getLanguage();
    public static String country    = Locale.getDefault().getCountry();
    public static Activity activity = null;

    public Settings(Activity activity) {
        Settings.activity = activity;
    }

    public static String getUUID() {
        String uuid = android.provider.Settings.Secure.getString(Settings.activity.getApplicationContext().getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
        return  uuid;
    }

    public static void request(final String method, Map<String,String> params , final VolleyCallback callback) {

        String apiUrl = Settings.apiUrl+method;

        if (params == null) {
            params = new HashMap<>();
        }
        RequestQueue requestQueue = VolleySingleton.getsInstance().getRequestQueue();
        final Map<String, String> finalParams = params;
        StringRequest request = new StringRequest(Request.Method.POST, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug",response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                finalParams.put("deviceType","android");
                finalParams.put("language",Settings.language);
                finalParams.put("country",Settings.country);
                finalParams.put("version",Settings.appVersion);
                return finalParams;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(60 * 60 * 1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        try {
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }

    public static void showDialog(String title,String message,String buton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(buton,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void setPref(String record, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(record, value);
        editor.apply();
    }

    public static String getPref(String record) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.activity);
        String value = prefs.getString(record, "");
        if(value.isEmpty()) return "";
        return value;
    }

    public static String encode(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch(Exception e) {
            return text;
        }
    }

    public static String base64decode(String st) {
        String r = "";
        byte[] data = Base64.decode(st,Base64.DEFAULT);
        try {
            r = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return r;
    }

}
