package com.grandilo.financelearn.utils;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * @author Wan Clem
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

}
