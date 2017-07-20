package com.grandilo.financelearn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

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
            Log.d("NewUserProps",jsonObject.toString());
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

}
