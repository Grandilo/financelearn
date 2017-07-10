package com.grandilo.financelearn.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author Ugo
 */

public class UiUtils {

    public static ProgressDialog operationsProgressDialog;
    private static String TAG = UiUtils.class.getSimpleName();

    public static void showProgressDialog(final Context context, final String message) {
        operationsProgressDialog = new ProgressDialog(context);
        operationsProgressDialog.setCancelable(false);
        operationsProgressDialog.setMessage(message);
        operationsProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        try {
            if (operationsProgressDialog != null) {
                if (operationsProgressDialog.isShowing()) {
                    operationsProgressDialog.dismiss();
                }
            }
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG, "Your father lip. Dialog no gree close again o. See the error na = " + e.getMessage());
        }
    }

}
