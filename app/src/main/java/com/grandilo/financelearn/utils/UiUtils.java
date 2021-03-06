package com.grandilo.financelearn.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.grandilo.financelearn.ui.ApplicationLoader;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
public class UiUtils {

    private static ProgressDialog operationsProgressDialog;
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

        }
    }

    public static void showToast(String message){
        Toast.makeText(ApplicationLoader.getInstance(),message,Toast.LENGTH_LONG).show();
    }
}
