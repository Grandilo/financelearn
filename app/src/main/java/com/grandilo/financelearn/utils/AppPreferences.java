package com.grandilo.financelearn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.grandilo.financelearn.ui.ApplicationLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Ugo
 */
@SuppressLint("ApplySharedPref")
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
        getSharedPreferences().edit().putBoolean(email, b).commit();
    }

    public static boolean hasEmailBeingSent(String email) {
        return getSharedPreferences().getBoolean(email, false);
    }

    public static boolean isFirstTime() {
        return getSharedPreferences()
                .getBoolean(FinanceLearningConstants.LOGGED_IN, true);
    }

    public static void saveNotFirstLogIn() {
        getSharedPreferences()
                .edit()
                .putBoolean(FinanceLearningConstants.LOGGED_IN, false).apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void saveDownloadReference(long refId, String fileName) {
        getSharedPreferences().edit().putString("_" + refId, fileName).commit();
    }

    public static String getDownloadReference(long refId) {
        return getSharedPreferences().getString("_" + refId, "file");
    }

    public static void saveCourse(String courseId, String coursePropsString) {
        getSharedPreferences().edit().putString(courseId, coursePropsString).commit();
    }

    static String getCourseMapString(String courseId) {
        return getSharedPreferences().getString(courseId, null);
    }

    private static SharedPreferences getSharedPreferences() {
        return ApplicationLoader.getInstance().getSharedPreferences(FinanceLearningConstants.SHARED_PREFS, Context.MODE_PRIVATE);
    }

}
