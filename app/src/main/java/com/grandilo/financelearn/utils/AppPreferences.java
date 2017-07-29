package com.grandilo.financelearn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.grandilo.financelearn.ui.ApplicationLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Ugo
 */

public class AppPreferences {

    public static String getLoggedInType(Context context) {
        return context
                .getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE)
                .getString(FinanceLearningConstants.LOGIN_TYPE, null);
    }

    @SuppressLint("ApplySharedPref")
    public static void saveLoggedInType(Context context, String loggedInType) {
        context.getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE)
                .edit().putString(FinanceLearningConstants.LOGIN_TYPE, loggedInType)
                .commit();
    }

    public static boolean isUserLoggedIn(Context context) {
        return context.getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).getString(FinanceLearningConstants.LOGGED_IN_USER, null) != null;
    }

    @SuppressLint("ApplySharedPref")
    public static void saveLoggedInUser(Context context, HashMap<String, Object> loggedInUserProps) {
        if (loggedInUserProps != null) {
            JSONObject jsonObject = new JSONObject();
            for (String key : loggedInUserProps.keySet()) {
                try {
                    jsonObject.put(key, loggedInUserProps.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            context.getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(FinanceLearningConstants.LOGGED_IN_USER, jsonObject.toString()).commit();
            Log.d("NewUserProps", jsonObject.toString());
        } else {
            context.getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(FinanceLearningConstants.LOGGED_IN_USER, null).commit();
        }
    }

    public static JSONObject getSignedInUser(Context context) {
        try {
            String loggedInUser = context.getSharedPreferences(FinanceLearningConstants.SHARED_PREFS,
                    Context.MODE_PRIVATE)
                    .getString(FinanceLearningConstants.LOGGED_IN_USER, null);
            if (loggedInUser != null) {
                return new JSONObject(loggedInUser);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("ApplySharedPref")
    static void saveEmailSentStatus(String email, boolean b) {
        ApplicationLoader.getInstance().getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).edit().putBoolean(email, b).commit();
    }

    public static boolean hasEmailBeingSent(String email) {
        return ApplicationLoader.getInstance().getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(email, false);
    }

    public static boolean isFirstTime() {
        return ApplicationLoader.getInstance()
                .getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE)
                .getBoolean(FinanceLearningConstants.LOGGED_IN, true);
    }

    public static void saveNotFirstLogIn() {
        ApplicationLoader.getInstance()
                .getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(FinanceLearningConstants.LOGGED_IN, false).apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void saveDownloadReference(long refId, String fileName) {
        ApplicationLoader.getInstance().getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).edit().putString("_" + refId, fileName).commit();
    }

    public static String getDownloadReference(long refId) {
        return ApplicationLoader.getInstance().getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE).getString("_" + refId, "file");
    }

}
